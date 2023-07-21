/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class VoronoiOperator extends OperatorImplementation {

    private enum VoronoiInput implements OpcodeInput {
        input((byte) 0);

        private final byte index;

        VoronoiInput(byte index) {
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

    protected VoronoiOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return VoronoiInput.values();
    }

}
