package com.ikea.spatiallab.procedural.operators;

public class TransformTRSOperator extends OperatorImplementation {

    private enum TransformTRSInput implements OpcodeInput {
        translation(),
        rotation(),
        scale(),
        input();

        TransformTRSInput(float... minMax) {
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

    protected TransformTRSOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return TransformTRSInput.values();
    }

}
