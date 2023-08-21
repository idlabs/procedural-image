/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class NoiseOperator extends OperatorImplementation {

    private enum NoiseInput implements OpcodeInput {
        position(),
        min_level(0f, 10f),
        max_level(0f, 10f),
        beta(0f, 8f);

        NoiseInput(float... minMax) {
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

    public NoiseOperator(Operator operator) {
        super(operator);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return NoiseInput.values();
    }

}
