package org.bidib.jbidibc.message;

import org.bidib.jbidibc.BidibLibrary;
import org.bidib.jbidibc.exception.ProtocolException;
import org.bidib.jbidibc.utils.ByteUtils;

public class SysMagicResponse extends BidibMessage {
    public static final Integer TYPE = BidibLibrary.MSG_SYS_MAGIC;

    SysMagicResponse(byte[] addr, int num, int type, byte... data) throws ProtocolException {
        super(addr, num, type, data);
        if (data == null
            || data.length != 2
            || (!(data[0] == (byte) 0xFE && data[1] == (byte) 0xAF) && !(data[0] == (byte) 0x0D && data[1] == (byte) 0xB0))) {
            throw new ProtocolException("no magic received");
        }
    }

    public SysMagicResponse(byte[] addr, int num, byte... data) throws ProtocolException {
        this(addr, num, BidibLibrary.MSG_SYS_MAGIC, data);
    }

    public int getMagic() {
        byte[] data = getData();

        return ByteUtils.getInt(data[0], data[1]);
    }
}
