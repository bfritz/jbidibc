package org.bidib.message;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

import org.bidib.exception.ProtocolException;


public class BidibMessage {
    // The address field is only valid for a response message!
    private byte[] addr = null;
    private int num = 0;
    private byte type = 0;
    private byte[] data = null;

    BidibMessage(byte[] addr, int num, int type, byte... data) {
        this.addr = addr;
        this.num = num & 0xFF;
        this.type = (byte) type;
        this.data = data;
    }

    BidibMessage(int num, int type, byte... data) {
        this.num = num & 0xFF;
        this.type = (byte) type;
        this.data = data;
    }

    /**
     * Create a BidibMessage from an array of bytes.
     * 
     * @param message
     *            array of bytes, containing the leading magic byte, but wthout
     *            the trailing magic byte
     * 
     * @throws ProtocolException
     *             Thrown if the leading magic byte was missing.
     */
    BidibMessage(byte[] message) throws ProtocolException {
        if (message != null && message.length > 0) {
            int index = 0;
            int length = message[index++];

            // address
            ByteArrayOutputStream addrBytes = new ByteArrayOutputStream();

            while (message[index] != 0) {
                addrBytes.write(message[index++]);
                if (index >= message.length) {
                    throw new ProtocolException("address too long");
                }
            }
            if (addrBytes.size() < 4) {
                addrBytes.write(0);
            }
            addr = addrBytes.toByteArray();
            index++;

            num = message[index++];
            type = message[index++];

            // data
            ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();

            while (index <= length) {
                dataBytes.write(message[index++]);
            }
            data = dataBytes.toByteArray();
        }
    }

    public static byte[] convertLongToUniqueId(long uniqueId) {
        return BigInteger.valueOf(uniqueId).toByteArray();
    }

    public static long convertUniqueIdToLong(byte[] uniqueId) {
        return new BigInteger(uniqueId).longValue();
    }

    public byte[] getAddr() {
        return addr;
    }

    public int getNum() {
        return num;
    }

    public byte getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + (addr != null ? Arrays.toString(addr) + "," : "") + "num=" + num
                + ",type=" + type + ",data=" + Arrays.toString(data) + "]";
    }
}
