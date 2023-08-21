package com.ikea.spatiallab.procedural.operators;

public class SharpenOperator extends OperatorImplementation {

    private enum SharpenInput implements OpcodeInput {
        intensity(),
        input();

        SharpenInput(float... minMax) {
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

    protected SharpenOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return SharpenInput.values();
    }
}
