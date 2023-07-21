/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class CurveOperator extends OperatorImplementation {

    private enum CurveInput implements OpcodeInput {
        input((byte) 0),
        red((byte) 1),
        green((byte) 2),
        blue((byte) 3),
        alpha((byte) 4);

        private final byte index;

        CurveInput(byte index) {
            this.index = index;
        }

        @Override
        public String getName() {
            return name();
        }

        @Override
        public byte getIndex() {
            return index;
        }
    }

    protected CurveOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        FloatParameter input = getFloatParameter(CurveInput.red.name());
        // Store number of controlpoints
        // TODO - makes sure that data in red,green,blue,alpha matches
        opCodes.addOpcode(getOperator().opCode, (byte) (input.getNumberOfValues() / 2));
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return CurveInput.values();
    }

}
