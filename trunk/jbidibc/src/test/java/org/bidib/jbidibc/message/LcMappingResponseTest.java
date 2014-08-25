package org.bidib.jbidibc.message;

import org.bidib.jbidibc.BidibLibrary;
import org.bidib.jbidibc.core.LcPortMapping;
import org.bidib.jbidibc.enumeration.LcMappingPortType;
import org.bidib.jbidibc.exception.ProtocolException;
import org.bidib.jbidibc.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LcMappingResponseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LcMappingResponseTest.class);

    @Test
    public void getPortAvailable16Ports() throws ProtocolException {
        byte[] message = { 0x0a, 0x01, 0x00, (byte) 0xd5, (byte) 0xc5, 0x00, 0x10, 0x01, 0x06, 0x02, 0x00 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MAPPING);

        LcMappingResponse lcMappingResponse = (LcMappingResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMappingResponse);

        Assert.assertEquals(lcMappingResponse.getLcMappingPortType(), LcMappingPortType.SWITCHPORT);
        Assert.assertEquals(lcMappingResponse.getPortCount(), 16);

        Assert.assertEquals(lcMappingResponse.getPortConfigAvailable(), new int[] { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, /**/0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00 });
        Assert.assertEquals(lcMappingResponse.getPortMapping(), new int[] { 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, /**/0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
    }

    @Test
    public void getPortAvailable1Port() throws ProtocolException {
        byte[] message = { 0x08, 0x01, 0x00, (byte) 0xd5, (byte) 0xc5, 0x00, 0x01, 0x01, 0x02 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MAPPING);

        LcMappingResponse lcMappingResponse = (LcMappingResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMappingResponse);

        Assert.assertEquals(lcMappingResponse.getLcMappingPortType(), LcMappingPortType.SWITCHPORT);
        Assert.assertEquals(lcMappingResponse.getPortCount(), 1);

        Assert.assertEquals(lcMappingResponse.getPortConfigAvailable(), new int[] { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00 });
        Assert.assertEquals(lcMappingResponse.getPortMapping(), new int[] { 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00 });
    }

    @Test
    public void getLcPortMapping16Ports() throws ProtocolException {
        byte[] message = { 0x0a, 0x01, 0x00, (byte) 0xd5, (byte) 0xc5, 0x00, 0x10, 0x01, 0x06, 0x02, 0x00 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MAPPING);

        LcMappingResponse lcMappingResponse = (LcMappingResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMappingResponse);

        Assert.assertNotNull(lcMappingResponse.getLcPortMapping());
        LcPortMapping lcPortMapping = lcMappingResponse.getLcPortMapping();

        Assert.assertEquals(lcPortMapping.getLcMappingPortType(), LcMappingPortType.SWITCHPORT);
        Assert.assertEquals(lcPortMapping.getPortCount(), 16);

        Assert.assertEquals(lcPortMapping.getPortConfigAvailable(), new int[] { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, /**/0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00 });
        Assert.assertEquals(lcPortMapping.getPortMapping(), new int[] { 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /**/
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
    }

    @Test
    public void getLcPortMapping0Ports() throws ProtocolException {
        byte[] message = { 0x06, 0x01, 0x00, (byte) 0xd5, (byte) 0xc5, 0x01, 0x00 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MAPPING);

        LcMappingResponse lcMappingResponse = (LcMappingResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMappingResponse);

        Assert.assertNotNull(lcMappingResponse.getLcPortMapping());
        LcPortMapping lcPortMapping = lcMappingResponse.getLcPortMapping();

        Assert.assertEquals(lcPortMapping.getLcMappingPortType(), LcMappingPortType.LIGHTPORT);
        Assert.assertEquals(lcPortMapping.getPortCount(), 0);

        Assert.assertNull(lcPortMapping.getPortConfigAvailable());
        Assert.assertNull(lcPortMapping.getPortMapping());
    }
}
