package com.ikea.spatiallab.procedural.operators;

public class SawtoothWaveOperator extends OperatorImplementation {

    private enum SawtoothWaveInput implements OpcodeInput {
        input();

        SawtoothWaveInput(float... minMax) {
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

    protected SawtoothWaveOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return SawtoothWaveInput.values();
    }

}
