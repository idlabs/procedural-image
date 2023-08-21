package com.ikea.spatiallab.procedural.operators;

public class SubtractOperator extends OperatorImplementation {

    private enum SubtractInput implements OpcodeInput {
        minuend(),
        subtrahend();

        SubtractInput(float... minMax) {
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

    protected SubtractOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return SubtractInput.values();
    }

}
