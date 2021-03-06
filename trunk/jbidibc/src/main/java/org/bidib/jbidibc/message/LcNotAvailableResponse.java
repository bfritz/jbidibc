package org.bidib.jbidibc.message;

import org.bidib.jbidibc.BidibLibrary;
import org.bidib.jbidibc.enumeration.LcOutputType;
import org.bidib.jbidibc.exception.ProtocolException;
import org.bidib.jbidibc.utils.ByteUtils;

public class LcNotAvailableResponse extends BidibMessage {
    public static final Integer TYPE = BidibLibrary.MSG_LC_NA;

    LcNotAvailableResponse(byte[] addr, int num, int type, byte... data) throws ProtocolException {
        super(addr, num, type, data);
        if (data == null || data.length != 2) {
            throw new ProtocolException("LC not available response received");
        }
    }

    public LcNotAvailableResponse(byte[] addr, int num, byte portType, byte portNum) throws ProtocolException {
        this(addr, num, BidibLibrary.MSG_LC_NA, portType, portNum);
    }

    public String getName() {
        return "MSG_LC_NA";
    }

    public LcOutputType getPortType() {
        return LcOutputType.valueOf(getData()[0]);
    }

    public int getPortNumber() {
        return ByteUtils.getInt(getData()[1]);
    }
}
