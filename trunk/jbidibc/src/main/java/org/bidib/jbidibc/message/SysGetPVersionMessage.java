package org.bidib.jbidibc.message;

import org.bidib.jbidibc.BidibLibrary;
import org.bidib.jbidibc.exception.ProtocolException;

public class SysGetPVersionMessage extends BidibMessage {
    public SysGetPVersionMessage() {
        super(0, BidibLibrary.MSG_SYS_GET_P_VERSION);
    }

    public SysGetPVersionMessage(byte[] message) throws ProtocolException {
        super(message);
    }
}
