package org.bidib.jbidibc.message;

import org.bidib.jbidibc.BidibLibrary;
import org.bidib.jbidibc.LcMacro;
import org.bidib.jbidibc.enumeration.BacklightPortEnum;
import org.bidib.jbidibc.enumeration.LcOutputType;
import org.bidib.jbidibc.enumeration.LightPortEnum;
import org.bidib.jbidibc.enumeration.ServoPortEnum;
import org.bidib.jbidibc.exception.ProtocolException;
import org.bidib.jbidibc.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LcMacroResponseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LcMacroResponseTest.class);

    @Test
    public void createLcMacroResponse1FromByteArray() throws ProtocolException {
        byte[] message = { 0x0a, 0x01, 0x00, (byte) 0xd5, (byte) 0xc9, 0x00, 0x00, 0x00, 0x06, 0x02, 0x00 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MACRO);

        LcMacroResponse lcMacroResponse = (LcMacroResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMacroResponse);

        LcMacro lcMacro = lcMacroResponse.getMacro();
        Assert.assertNotNull(lcMacro);
        LOGGER.info("lcMacro: {}", lcMacro);

        Assert.assertEquals(lcMacro.getStepNumber(), 0);
        Assert.assertEquals(lcMacro.getOutputNumber(), 2);
        Assert.assertEquals(lcMacro.getOutputType(), LcOutputType.BACKLIGHTPORT);
        Assert.assertEquals(lcMacro.getStatus(), BacklightPortEnum.START);
        Assert.assertEquals(lcMacro.getValue(), 0);
    }

    @Test
    public void createLcMacroResponse2FromByteArray() throws ProtocolException {
        byte[] message = { 0x0a, 0x01, 0x00, (byte) 0xd6, (byte) 0xc9, 0x00, 0x01, (byte) 0xc8, 0x06, 0x02, 0x05 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MACRO);

        LcMacroResponse lcMacroResponse = (LcMacroResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMacroResponse);

        LcMacro lcMacro = lcMacroResponse.getMacro();
        Assert.assertNotNull(lcMacro);
        LOGGER.info("lcMacro: {}", lcMacro);

        Assert.assertEquals(lcMacro.getStepNumber(), 1);
        Assert.assertEquals(lcMacro.getOutputNumber(), 2);
        Assert.assertEquals(lcMacro.getOutputType(), LcOutputType.BACKLIGHTPORT);
        Assert.assertEquals(lcMacro.getStatus(), BacklightPortEnum.START);
        Assert.assertEquals(lcMacro.getValue(), 5);
    }

    @Test
    public void createLcMacroResponse3FromByteArray() throws ProtocolException {
        byte[] message = { 0x0a, 0x01, 0x00, (byte) 0xd7, (byte) 0xc9, 0x00, 0x02, (byte) 0xc8, 0x01, 0x01, 0x00 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MACRO);

        LcMacroResponse lcMacroResponse = (LcMacroResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMacroResponse);

        LcMacro lcMacro = lcMacroResponse.getMacro();
        Assert.assertNotNull(lcMacro);
        LOGGER.info("lcMacro: {}", lcMacro);

        Assert.assertEquals(lcMacro.getStepNumber(), 2);
        Assert.assertEquals(lcMacro.getOutputNumber(), 1);
        Assert.assertEquals(lcMacro.getOutputType(), LcOutputType.LIGHTPORT);
        Assert.assertEquals(lcMacro.getStatus(), LightPortEnum.OFF);
        Assert.assertEquals(lcMacro.getValue(), 0); // value is not set for light port
    }

    @Test
    public void createLcMacroResponse4FromByteArray() throws ProtocolException {
        byte[] message =
            { 0x0a, 0x01, 0x00, (byte) 0xd8, (byte) 0xc9, 0x00, 0x03, (byte) 0xff, (byte) 0xff, 0x00, 0x00 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MACRO);

        LcMacroResponse lcMacroResponse = (LcMacroResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMacroResponse);

        LcMacro lcMacro = lcMacroResponse.getMacro();
        Assert.assertNotNull(lcMacro);
        LOGGER.info("lcMacro: {}", lcMacro);

        Assert.assertEquals(lcMacro.getStepNumber(), 3);
        Assert.assertEquals(lcMacro.getOutputNumber(), 0);
        Assert.assertEquals(lcMacro.getOutputType(), LcOutputType.END_OF_MACRO);
    }

    @Test
    public void createLcMacroResponse5FromByteArray() throws ProtocolException {
        byte[] message = { 0x0a, 0x01, 0x00, (byte) 0xd6, (byte) 0xc9, 0x00, 0x01, (byte) 0xc8, 0x02, 0x02, 0x25 };

        BidibMessage bidibMessage = ResponseFactory.create(message);
        Assert.assertNotNull(bidibMessage);
        Assert.assertEquals(ByteUtils.getInt(bidibMessage.getType()), BidibLibrary.MSG_LC_MACRO);

        LcMacroResponse lcMacroResponse = (LcMacroResponse) bidibMessage;
        LOGGER.info("lcMacroResponse: {}", lcMacroResponse);

        LcMacro lcMacro = lcMacroResponse.getMacro();
        Assert.assertNotNull(lcMacro);
        LOGGER.info("lcMacro: {}", lcMacro);

        Assert.assertEquals(lcMacro.getStepNumber(), 1);
        Assert.assertEquals(lcMacro.getOutputNumber(), 2);
        Assert.assertEquals(lcMacro.getOutputType(), LcOutputType.SERVOPORT);
        Assert.assertEquals(lcMacro.getStatus(), ServoPortEnum.START);
        Assert.assertEquals(lcMacro.getValue(), 0x25);
    }
}
