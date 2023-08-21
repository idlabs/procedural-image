/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class PositionOperator extends OperatorImplementation {

    private enum PositiontInput implements OpcodeInput {
        offset(-10, 10),
        rotate(-3.14159f, 3.14159f),
        scale(0f, 5f);

        PositiontInput(float... minMax) {
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

    public PositionOperator() {

    }

    /**
     * @param operator
     */
    public PositionOperator(Operator operator) {
        super(operator);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return PositiontInput.values();
    }

    @Override
    public boolean allowNullInput(IndexParameter index) {
        return true;
    }

}
