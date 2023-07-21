/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class NoiseOperator extends OperatorImplementation {

    private enum NoiseInput implements OpcodeInput {
        position((byte) 0),
        min_level((byte) 1),
        max_level((byte) 2),
        beta((byte) 3);

        private final byte index;

        NoiseInput(byte index) {
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

    public NoiseOperator(Operator operator) {
        super(operator);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return NoiseInput.values();
    }

}
