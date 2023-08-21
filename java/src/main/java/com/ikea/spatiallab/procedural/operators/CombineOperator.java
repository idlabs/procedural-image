package com.ikea.spatiallab.procedural.operators;

public class CombineOperator extends OperatorImplementation {

    private enum CombineInput implements OpcodeInput {
        red(),
        green(),
        blue(),
        alpha();

        CombineInput(float... minMax) {
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

    protected CombineOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return CombineInput.values();
    }

}
