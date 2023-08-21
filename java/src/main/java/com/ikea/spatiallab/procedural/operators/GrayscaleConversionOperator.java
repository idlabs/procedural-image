/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class GrayscaleConversionOperator extends OperatorImplementation {

    private enum GrayscaleConversionInput implements OpcodeInput {
        input(),
        weights(0f, 1f);

        GrayscaleConversionInput(float... minMax) {
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

    /**
     * @param operator
     */
    public GrayscaleConversionOperator(Operator operator) {
        super(operator);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return GrayscaleConversionInput.values();
    }

}
