/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class NormalMapOperator extends OperatorImplementation {

    private enum NormalMapInput implements OpcodeInput {
        input((byte) 0),
        intensity((byte) 1);

        private final byte index;

        NormalMapInput(byte index) {
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

    protected NormalMapOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return NormalMapInput.values();
    }

}
