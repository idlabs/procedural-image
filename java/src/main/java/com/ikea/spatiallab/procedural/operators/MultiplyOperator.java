/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class MultiplyOperator extends OperatorImplementation {

    private enum MultiplyInput implements OpcodeInput {
        factor_1(),
        factor_2();

        MultiplyInput(float... minMax) {
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

    public MultiplyOperator(Operator operator) {
        super(operator);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return MultiplyInput.values();
    }

}
