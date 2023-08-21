package com.ikea.spatiallab.procedural.operators;

public class L2NormOperator extends OperatorImplementation {

    private enum L2NormInput implements OpcodeInput {
        input();

        L2NormInput(float... minMax) {
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

    protected L2NormOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return L2NormInput.values();
    }

}
