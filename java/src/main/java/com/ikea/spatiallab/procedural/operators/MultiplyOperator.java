/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class MultiplyOperator extends OperatorImplementation {

    private enum MultiplyInput implements OpcodeInput {
        factor_1((byte) 0),
        factor_2((byte) 1);

        private final byte index;

        MultiplyInput(byte index) {
            this.index = index;
        }

        @Override
        public String getName() {
            return name();
        }

        @Override
        public byte getIndex() {
            return index;
        }
    }

    public MultiplyOperator(Operator operator) {
        super(operator);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return MultiplyInput.values();
    }

}
