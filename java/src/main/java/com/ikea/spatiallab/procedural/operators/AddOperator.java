package com.ikea.spatiallab.procedural.operators;

public class AddOperator extends OperatorImplementation {

    private enum AddInput implements OpcodeInput {
        summand_1(),
        summand_2();

        AddInput(float... minMax) {
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

    protected AddOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return AddInput.values();
    }

}
