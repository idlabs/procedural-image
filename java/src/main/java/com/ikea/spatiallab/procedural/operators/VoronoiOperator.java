/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class VoronoiOperator extends OperatorImplementation {

    private enum VoronoiInput implements OpcodeInput {
        randomness(0f, 1f),
        position();

        VoronoiInput(float... minMax) {
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

    protected VoronoiOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return VoronoiInput.values();
    }

}
