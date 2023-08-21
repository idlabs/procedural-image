package com.ikea.spatiallab.procedural.operators;

public class DivideOperator extends OperatorImplementation {

    private enum DivideInput implements OpcodeInput {
        dividend(),
        divisor();

        DivideInput(float... minMax) {
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

    protected DivideOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return DivideInput.values();
    }

}
