package org.bidib.jbidibc.lcmacro;

import org.bidib.jbidibc.LcMacro;
import org.bidib.jbidibc.enumeration.AnalogPortEnum;
import org.bidib.jbidibc.enumeration.BidibEnum;
import org.bidib.jbidibc.enumeration.LightPortEnum;
import org.bidib.jbidibc.enumeration.MotorPortEnum;
import org.bidib.jbidibc.enumeration.ServoPortEnum;
import org.bidib.jbidibc.enumeration.SoundPortEnum;
import org.bidib.jbidibc.enumeration.SwitchPortEnum;
import org.bidib.jbidibc.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LcMacroExporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LcMacroExporter.class);

    public LcMacroPointType prepareLcMacroPoint(LcMacro lcMacro) {
        LOGGER.info("Export the LcMacro: {}", lcMacro);

        LcMacroPointType lcMacroPoint = null;

        BidibEnum val = lcMacro.getStatus();
        switch (lcMacro.getOutputType()) {
            case ANALOGPORT:
                AnalogPortEnum analogPortEnum = AnalogPortEnum.valueOf(val.getType());
                AnalogPortPoint analogPortPoint = new AnalogPortPoint();
                analogPortPoint.setAnalogPortActionType(AnalogPortActionType.fromValue(analogPortEnum.name()));
                analogPortPoint.setOutputNumber(lcMacro.getOutputNumber());
                lcMacroPoint = analogPortPoint;
                break;
            case BEGIN_CRITICAL:
                CriticalSectionPoint beginCriticalSectionPoint = new CriticalSectionPoint();
                beginCriticalSectionPoint.setCriticalSectionActionType(CriticalSectionActionType.BEGIN);
                lcMacroPoint = beginCriticalSectionPoint;
                break;
            case DELAY:
                DelayPoint delayPoint = new DelayPoint();
                delayPoint.setDelayActionType(ByteUtils.getInt(lcMacro.getOutputNumber()));
                lcMacroPoint = delayPoint;
                break;
            case END_CRITICAL:
                CriticalSectionPoint endCriticalSectionPoint = new CriticalSectionPoint();
                endCriticalSectionPoint.setCriticalSectionActionType(CriticalSectionActionType.END);
                lcMacroPoint = endCriticalSectionPoint;
                break;
            case FLAG_CLEAR:
                FlagActionType flagClearActionType = new FlagActionType();
                flagClearActionType.setOperation(FlagOperationType.CLEAR);
                flagClearActionType.setFlagNumber(ByteUtils.getInt(lcMacro.getOutputNumber()));
                FlagPoint flagClearPoint = new FlagPoint();
                flagClearPoint.setFlagActionType(flagClearActionType);
                lcMacroPoint = flagClearPoint;
                break;
            case FLAG_SET:
                FlagActionType flagSetActionType = new FlagActionType();
                flagSetActionType.setOperation(FlagOperationType.SET);
                flagSetActionType.setFlagNumber(ByteUtils.getInt(lcMacro.getOutputNumber()));
                FlagPoint flagSetPoint = new FlagPoint();
                flagSetPoint.setFlagActionType(flagSetActionType);
                lcMacroPoint = flagSetPoint;
                break;
            case FLAG_QUERY:
                FlagActionType flagQueryActionType = new FlagActionType();
                flagQueryActionType.setOperation(FlagOperationType.QUERY);
                flagQueryActionType.setFlagNumber(ByteUtils.getInt(lcMacro.getOutputNumber()));
                FlagPoint flagQueryPoint = new FlagPoint();
                flagQueryPoint.setFlagActionType(flagQueryActionType);
                lcMacroPoint = flagQueryPoint;
                break;
            case INPUT_QUERY0:
                InputQuery0Point inputQuery0Point = new InputQuery0Point();
                //                inputQuery0Point.setInputQuery0ActionType(ByteUtils.getInt(lcMacro.getOutputNumber()));
                inputQuery0Point.setInputNumber(lcMacro.getOutputNumber());
                lcMacroPoint = inputQuery0Point;
                break;
            case INPUT_QUERY1:
                InputQuery1Point inputQuery1Point = new InputQuery1Point();
                //                inputQuery1Point.setInputQuery1ActionType(ByteUtils.getInt(lcMacro.getOutputNumber()));
                inputQuery1Point.setInputNumber(lcMacro.getOutputNumber());
                lcMacroPoint = inputQuery1Point;
                break;
            case LIGHTPORT:
                LightPortEnum lightPortEnum = LightPortEnum.valueOf(val.getType());
                LightPortPoint lightPortPoint = new LightPortPoint();
                lightPortPoint.setLightPortActionType(LightPortActionType.fromValue(lightPortEnum.name()));
                lightPortPoint.setOutputNumber(lcMacro.getOutputNumber());
                lcMacroPoint = lightPortPoint;
                break;
            case END_OF_MACRO:
                MacroActionType macroEndActionType = new MacroActionType();
                macroEndActionType.setOperation(MacroOperationType.END);
                macroEndActionType.setMacroNumber(ByteUtils.getInt(lcMacro.getOutputNumber()));
                MacroActionPoint macroEndActionPoint = new MacroActionPoint();
                macroEndActionPoint.setMacroActionType(macroEndActionType);
                lcMacroPoint = macroEndActionPoint;
                break;
            case START_MACRO:
                MacroActionType macroStartActionType = new MacroActionType();
                macroStartActionType.setOperation(MacroOperationType.START);
                macroStartActionType.setMacroNumber(ByteUtils.getInt(lcMacro.getOutputNumber()));
                MacroActionPoint macroStartActionPoint = new MacroActionPoint();
                macroStartActionPoint.setMacroActionType(macroStartActionType);
                lcMacroPoint = macroStartActionPoint;
                break;
            case STOP_MACRO:
                MacroActionType macroStopActionType = new MacroActionType();
                macroStopActionType.setOperation(MacroOperationType.STOP);
                macroStopActionType.setMacroNumber(ByteUtils.getInt(lcMacro.getOutputNumber()));
                MacroActionPoint macroStopActionPoint = new MacroActionPoint();
                macroStopActionPoint.setMacroActionType(macroStopActionType);
                lcMacroPoint = macroStopActionPoint;
                break;
            case MOTORPORT:
                MotorPortEnum motorPortEnum = MotorPortEnum.valueOf(val.getType());
                MotorPortPoint motorPortPoint = new MotorPortPoint();
                motorPortPoint.setMotorPortActionType(MotorPortActionType.fromValue(motorPortEnum.name()));
                motorPortPoint.setOutputNumber(lcMacro.getOutputNumber());
                lcMacroPoint = motorPortPoint;
                break;
            case RANDOM_DELAY:
                RandomDelayPoint randomDelayPoint = new RandomDelayPoint();
                randomDelayPoint.setRandomDelayActionType(ByteUtils.getInt(lcMacro.getOutputNumber()));
                lcMacroPoint = randomDelayPoint;
                break;
            case SERVOPORT:
                ServoPortEnum servoPortEnum = ServoPortEnum.valueOf(val.getType());
                ServoPortActionType servoPortActionType = new ServoPortActionType();
                servoPortActionType.setAction(ServoActionType.fromValue(servoPortEnum.name()));
                servoPortActionType.setDestination(lcMacro.getValue());
                ServoPortPoint servoPortPoint = new ServoPortPoint();
                servoPortPoint.setServoPortActionType(servoPortActionType);
                servoPortPoint.setOutputNumber(lcMacro.getOutputNumber());
                lcMacroPoint = servoPortPoint;
                break;
            case SOUNDPORT:
                SoundPortEnum soundPortEnum = SoundPortEnum.valueOf(val.getType());
                SoundPortPoint soundPortPoint = new SoundPortPoint();
                soundPortPoint.setSoundPortActionType(SoundPortActionType.fromValue(soundPortEnum.name()));
                soundPortPoint.setOutputNumber(lcMacro.getOutputNumber());
                lcMacroPoint = soundPortPoint;
                break;
            case SWITCHPORT:
                SwitchPortEnum switchPortEnum = SwitchPortEnum.valueOf(val.getType());
                SwitchPortPoint switchPortPoint = new SwitchPortPoint();
                switchPortPoint.setSwitchPortActionType(SwitchPortActionType.fromValue(switchPortEnum.name()));
                switchPortPoint.setOutputNumber(lcMacro.getOutputNumber());
                lcMacroPoint = switchPortPoint;
                break;
            default:
                LOGGER.warn("Unsupported port type detected!");
                lcMacroPoint = null;
                break;
        }
        lcMacroPoint.setDelay(ByteUtils.getInt(lcMacro.getDelay()));
        lcMacroPoint.setIndex(ByteUtils.getInt(lcMacro.getStepNumber()));

        LOGGER.info("Return lcMacroPoint: {}", lcMacroPoint);
        return lcMacroPoint;
    }

}
