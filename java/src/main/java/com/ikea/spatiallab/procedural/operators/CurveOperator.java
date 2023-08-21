/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class CurveOperator extends OperatorImplementation {

    private enum CurveInput implements OpcodeInput {
        input(),
        red(),
        green(),
        blue(),
        alpha();

        CurveInput(float... minMax) {
            this.minMax = minMax;
        }

        private final float[] minMax;

        @Override
        public float[] getMinMax() {
            return minMax;
        }

        @Override
        public String getName() {
            return name();
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
        opCodes.addOpcode(getKey(), getOperator().opCode, (byte) (input.getNumberOfValues() / 2));
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return CurveInput.values();
    }

}
