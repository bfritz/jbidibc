package org.bidib.jbidibc.message;

import org.bidib.jbidibc.BidibLibrary;
import org.bidib.jbidibc.exception.ProtocolException;
import org.bidib.jbidibc.utils.ByteUtils;

public class FeatureSetMessage extends BidibCommandMessage {
    protected FeatureSetMessage(int number, int value) {
        super(0, BidibLibrary.MSG_FEATURE_SET, new byte[] { (byte) number, (byte) value });
    }

    public FeatureSetMessage(byte[] message) throws ProtocolException {
        super(message);
    }

    public int getNumber() {
        return ByteUtils.getInt(getData()[0]);
    }

    public int getValue() {
        return ByteUtils.getInt(getData()[1]);
    }

    @Override
    public Integer[] getExpectedResponseTypes() {
        return new Integer[] { FeatureResponse.TYPE, FeatureNotAvailableResponse.TYPE };
    }
}
