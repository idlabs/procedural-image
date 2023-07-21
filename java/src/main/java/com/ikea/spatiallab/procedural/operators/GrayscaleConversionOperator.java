/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class GrayscaleConversionOperator extends OperatorImplementation {

    private enum GrayscaleConversionInput implements OpcodeInput {
        input((byte) 0),
        weights((byte) 1);

        private final byte index;

        GrayscaleConversionInput(byte index) {
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

    /**
     * @param operator
     */
    public GrayscaleConversionOperator(Operator operator) {
        super(operator);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return GrayscaleConversionInput.values();
    }

}
