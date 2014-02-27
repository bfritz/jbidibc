package org.bidib.jbidibc.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.bidib.jbidibc.BidibInterface;
import org.bidib.jbidibc.ConnectionListener;
import org.bidib.jbidibc.MessageReceiver;
import org.bidib.jbidibc.Node;
import org.bidib.jbidibc.exception.PortNotFoundException;
import org.bidib.jbidibc.exception.PortNotOpenedException;
import org.bidib.jbidibc.exception.ProtocolException;
import org.bidib.jbidibc.message.RequestFactory;
import org.bidib.jbidibc.node.AccessoryNode;
import org.bidib.jbidibc.node.BidibNode;
import org.bidib.jbidibc.node.BoosterNode;
import org.bidib.jbidibc.node.CommandStationNode;
import org.bidib.jbidibc.node.NodeFactory;
import org.bidib.jbidibc.node.RootNode;
import org.bidib.jbidibc.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetBidib implements BidibInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetBidib.class);

    private static NetBidib INSTANCE;

    private MessageReceiver messageReceiver;

    private NodeFactory nodeFactory;

    private RequestFactory requestFactory;

    private NetBidibPort port;

    private Thread portWorker;

    private String connectedPortName;

    private InetAddress address;

    private int portNumber;

    private int sessionKey;

    private int sequence;

    private ConnectionListener connectionListener;

    private int responseTimeout = BidibInterface.DEFAULT_TIMEOUT * 100;

    //////////////////////////////////////////////////////////////////////////////
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    LOGGER.debug("Close the communication ports and perform cleanup.");
                    getInstance().close();
                }
                catch (Exception e) {
                }
            }
        });
    }

    private NetBidib() {

    }

    /**
     * Initialize the instance. This must only be called from this class
     */
    protected void initialize() {
        LOGGER.info("Initialize NetBidib.");
        nodeFactory = new NodeFactory();
        nodeFactory.setBidib(this);
        requestFactory = new RequestFactory();
        nodeFactory.setRequestFactory(requestFactory);
        // create the message receiver
        messageReceiver = new NetMessageReceiver(nodeFactory);
    }

    public static synchronized BidibInterface getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetBidib();
            INSTANCE.initialize();
        }
        return INSTANCE;
    }

    @Override
    public BidibNode getNode(Node node) {
        LOGGER.info("Get node: {}", node);
        return nodeFactory.getNode(node);
    }

    @Override
    public RootNode getRootNode() {
        return nodeFactory.getRootNode();
    }

    @Override
    public AccessoryNode getAccessoryNode(Node node) {
        return nodeFactory.getAccessoryNode(node);
    }

    @Override
    public BoosterNode getBoosterNode(Node node) {
        return nodeFactory.getBoosterNode(node);
    }

    @Override
    public CommandStationNode getCommandStationNode(Node node) {
        return nodeFactory.getCommandStationNode(node);
    }

    @Override
    public void open(String portName, ConnectionListener connectionListener) throws PortNotFoundException,
        PortNotOpenedException {
        LOGGER.info("Open port: {}", portName);

        this.connectionListener = connectionListener;

        if (port == null) {
            if (portName == null || portName.trim().isEmpty()) {
                throw new PortNotFoundException("");
            }

            connectedPortName = portName;

            try {
                close();
                port = internalOpen(connectedPortName);
                LOGGER.info("Port is opened, send the magic. The connected port is: {}", connectedPortName);
                sendMagic();
            }
            catch (Exception ex) {
                LOGGER.warn("Open port and send magic failed.", ex);
            }
        }
        else {
            LOGGER.warn("Port is already opened.");
        }
    }

    private NetBidibPort internalOpen(String portName) throws SocketException, UnknownHostException {

        String[] hostAndPort = portName.split(":");

        address = InetAddress.getByName(hostAndPort[0]);
        portNumber = Integer.parseInt(hostAndPort[1]);

        LOGGER.info("Configured address: {}, portNumber: {}", address, portNumber);

        // enable the message receiver before the event listener is added
        messageReceiver.enable();

        DatagramSocket datagramSocket = new DatagramSocket();
        // open the port
        NetBidibPort netBidibPort = new NetBidibPort(datagramSocket, (NetMessageReceiver) messageReceiver);

        LOGGER.info("Prepare and start the port worker.");

        portWorker = new Thread(netBidibPort);
        portWorker.start();

        return netBidibPort;
    }

    @Override
    public boolean isOpened() {
        // TODO Auto-generated method stub
        return port != null;
    }

    @Override
    public void close() {
        LOGGER.info("Close the port.");

        if (port != null) {
            LOGGER.info("Stop the port.");
            port.stop();

            if (portWorker != null) {
                synchronized (portWorker) {
                    try {
                        portWorker.join(5000L);
                    }
                    catch (InterruptedException ex) {
                        LOGGER.warn("Wait for termination of port worker failed.", ex);
                    }
                    portWorker = null;
                }
            }

            port = null;
        }
        LOGGER.info("Close the port finished.");
    }

    @Override
    public void send(byte[] bytes) {
        // TODO Auto-generated method stub
        if (port != null) {

            try {
                // TODO add the UDP packet wrapper ...
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bos.write(ByteUtils.getHighByte(sessionKey));
                bos.write(ByteUtils.getLowByte(sessionKey));
                bos.write(ByteUtils.getHighByte(sequence));
                bos.write(ByteUtils.getLowByte(sequence));
                bos.write(bytes);

                port.send(bos.toByteArray(), address, portNumber);

                // increment the sequence counter after sending the message sucessfully
                prepareNextSequence();
            }
            catch (IOException ex) {
                LOGGER.warn("Send message to port failed.", ex);
                throw new RuntimeException("Send message to datagram socket failed.", ex);
            }
        }
        else {
            LOGGER.warn("Send not possible, the port is closed.");
        }
    }

    private void prepareNextSequence() {
        sequence++;
        if (sequence > 65535) {
            sequence = 0;
        }
    }

    /**
     * Get the magic from the root node
     * @return the magic provided by the root node
     * @throws ProtocolException
     */
    private int sendMagic() throws ProtocolException {
        BidibNode rootNode = getRootNode();

        // Ignore the first exception ...
        try {
            rootNode.getMagic();
        }
        catch (Exception e) {
        }
        int magic = rootNode.getMagic();
        LOGGER.debug("The node returned magic: {}", magic);
        return magic;
    }

    @Override
    public void setReceiveTimeout(int timeout) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLogFile(String logFile) {
        // TODO Auto-generated method stub

    }

    @Override
    public MessageReceiver getMessageReceiver() {
        return messageReceiver;
    }

    @Override
    public List<String> getPortIdentifiers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIgnoreWaitTimeout(boolean ignoreWaitTimeout) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getResponseTimeout() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setResponseTimeout(int responseTimeout) {
        // TODO Auto-generated method stub

    }

}
