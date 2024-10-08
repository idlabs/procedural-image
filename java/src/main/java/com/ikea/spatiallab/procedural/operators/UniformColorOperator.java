/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class UniformColorOperator extends OperatorImplementation {

    private enum UniformColorInput implements OpcodeInput {
        output_color();

        UniformColorInput(float... minMax) {
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

    protected UniformColorOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return UniformColorInput.values();
    }

}
