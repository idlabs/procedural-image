/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 * black_point vec4
 * gamma vec4
 * white_point vec4
 * output_from vec4
 * output_to vec4
 * input vec4
 */
public class LevelsOperator extends OperatorImplementation {

    private enum LevelsInput implements OpcodeInput {
        input((byte) 0),
        black_point((byte) 1),
        white_point((byte) 2),
        gamma((byte) 3),
        output_from((byte) 4),
        output_to((byte) 5);

        private final byte index;

        LevelsInput(byte index) {
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

    protected LevelsOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return LevelsInput.values();
    }

}
