package org.bidib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import org.bidib.exception.ProtocolException;
import org.bidib.message.BidibMessage;
import org.bidib.message.BoostCurrentResponse;
import org.bidib.message.BoostStatResponse;
import org.bidib.message.FeedbackAddressResponse;
import org.bidib.message.FeedbackConfidenceResponse;
import org.bidib.message.FeedbackSpeedResponse;
import org.bidib.message.ResponseFactory;

public class LogFileAnalyzer {
    private static final DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

    private final Collection<Message> messages = new LinkedList<Message>();

    public LogFileAnalyzer(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(": ");

            // @formatter:off
            if (parts.length == 3
                    && (parts[1].startsWith("receive BoostCurrentResponse")
                            || parts[1].startsWith("receive BoostStatResponse")
                            || parts[1].startsWith("receive FeedbackAddressResponse")
                            || parts[1].startsWith("receive FeedbackConfidenceResponse")
                            || parts[1].startsWith("receive FeedbackSpeedResponse"))) {
                try {
                    
                    messages.add(new Message(dateFormat.parse(parts[0].trim()).getTime(),
                            ResponseFactory.create(getBytes(parts[2].trim()))));

                } catch (ProtocolException e) {
                    System.err.println("unknown message " + BidibMessage.toString(getBytes(parts[2].trim()))
                            + " in line " + line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // @formatter:on
        }
        reader.close();
        new Thread() {
            public void run() {
                try {
                    Message previousMessage = null;

                    for (Message message : messages) {
                        if (previousMessage != null) {
                            Thread.sleep(message.time - previousMessage.time);
                        } else {
                            Thread.sleep(5000);
                        }

                        System.out.println("message: " + message.message);

                        if (message.message instanceof BoostCurrentResponse) {
                            MessageReceiver.fireBoosterCurrent(message.message.getAddr(),
                                    ((BoostCurrentResponse) message.message).getCurrent());
                        } else if (message.message instanceof BoostStatResponse) {
                            MessageReceiver.fireBoosterState(message.message.getAddr(),
                                    ((BoostStatResponse) message.message).getState());
                        } else if (message.message instanceof FeedbackAddressResponse) {
                            MessageReceiver.fireAddress(message.message.getAddr(),
                                    ((FeedbackAddressResponse) message.message).getDetectorNumber(),
                                    ((FeedbackAddressResponse) message.message).getAddresses());
                        } else if (message.message instanceof FeedbackConfidenceResponse) {
                            MessageReceiver.fireConfidence(message.message.getAddr(),
                                    ((FeedbackConfidenceResponse) message.message).getValid(),
                                    ((FeedbackConfidenceResponse) message.message).getFreeze(),
                                    ((FeedbackConfidenceResponse) message.message).getSignal());
                        } else if (message.message instanceof FeedbackSpeedResponse) {
                            MessageReceiver.fireSpeed(message.message.getAddr(),
                                    ((FeedbackSpeedResponse) message.message).getAddress(),
                                    ((FeedbackSpeedResponse) message.message).getSpeed());
                        }
                        previousMessage = message;
                    }
                    System.out.println("no more messages to fire");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static byte[] getBytes(String bytes) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        String[] parts = bytes.split(" ");
        boolean escape_hot = false;

        for (String part : parts) {
            byte b = (byte) Integer.parseInt(part, 16);

            if (b == (byte) BidibLibrary.BIDIB_PKT_ESCAPE) {
                escape_hot = true;
            } else if (b != (byte) BidibLibrary.BIDIB_PKT_MAGIC) {
                if (escape_hot) {
                    b ^= 0x20;
                    escape_hot = false;
                }
                result.write(b);
            }
        }
        return result.toByteArray();
    }

    private class Message {
        public final long time;
        public final BidibMessage message;

        public Message(long time, BidibMessage message) {
            this.time = time;
            this.message = message;
        }
    }
}
