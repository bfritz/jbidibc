package org.bidib.jbidibc.message;

import org.bidib.jbidibc.AddressData;
import org.bidib.jbidibc.enumeration.AddressTypeEnum;
import org.bidib.jbidibc.enumeration.CommandStationPom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CommandStationPomMessageTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandStationPomMessageTest.class);

    @Test
    public void prepareCommandStationPomMessage() {
        AddressData locoAddress = new AddressData(3, AddressTypeEnum.LOCOMOTIVE_FORWARD);
        CommandStationPom opCode = CommandStationPom.RD_BYTE;
        int cvNumber = 7;
        byte data = 0;

        CommandStationPomMessage message = new CommandStationPomMessage(locoAddress, opCode, cvNumber, data);

        LOGGER.debug("Created message: {}", message);
        Assert.assertNotNull(message);

        Assert.assertEquals(message.getData()[0], (byte) 0x03); // ADDR_L
        Assert.assertEquals(message.getData()[1], (byte) 0x00); // ADDR_H

        Assert.assertEquals(message.getData()[2], (byte) 0x00); // ADDR_XL
        Assert.assertEquals(message.getData()[3], (byte) 0x00); // ADDR_XH

        Assert.assertEquals(message.getData()[4], (byte) 0x00); // MID

        Assert.assertEquals(message.getData()[5], opCode.getType()); // OPCODE

        Assert.assertEquals(message.getData()[6], (byte) cvNumber - 1); // CV_L
        Assert.assertEquals(message.getData()[7], (byte) 0x00); // CV_H
        Assert.assertEquals(message.getData()[8], (byte) 0x00); // CV_X

        Assert.assertEquals(message.getData()[9], (byte) 0x00); // DATA
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void prepareInvalidCommandStationPomMessage1() {
        AddressData locoAddress = new AddressData(3, AddressTypeEnum.LOCOMOTIVE_FORWARD);
        CommandStationPom opCode = CommandStationPom.RD_BYTE;
        int cvNumber = 0;
        byte data = 0;

        new CommandStationPomMessage(locoAddress, opCode, cvNumber, data);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void prepareInvalidCommandStationPomMessage2() {
        AddressData locoAddress = new AddressData(3, AddressTypeEnum.LOCOMOTIVE_FORWARD);
        CommandStationPom opCode = CommandStationPom.RD_BYTE;
        int cvNumber = 1025;
        byte data = 0;

        new CommandStationPomMessage(locoAddress, opCode, cvNumber, data);
    }
}
