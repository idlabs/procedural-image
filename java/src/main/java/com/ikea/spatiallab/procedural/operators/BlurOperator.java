package com.ikea.spatiallab.procedural.operators;

public class BlurOperator extends OperatorImplementation {

    private enum BlurInput implements OpcodeInput {
        intensity(),
        input();

        BlurInput(float... minMax) {
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

    protected BlurOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return BlurInput.values();
    }

}
