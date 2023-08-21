package com.ikea.spatiallab.procedural.operators;

public class ColorizeOperator extends OperatorImplementation {

    private enum ColorizeInput implements OpcodeInput {
        keys(),
        input();

        ColorizeInput(float... minMax) {
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

    protected ColorizeOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return ColorizeInput.values();
    }

}
