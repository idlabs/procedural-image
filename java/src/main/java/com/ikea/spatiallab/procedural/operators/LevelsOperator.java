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
        input(),
        black_point(),
        white_point(),
        gamma(),
        output_from(),
        output_to();

        LevelsInput(float... minMax) {
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

    protected LevelsOperator(Operator source) {
        super(source);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getKey(), getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        return LevelsInput.values();
    }

}
