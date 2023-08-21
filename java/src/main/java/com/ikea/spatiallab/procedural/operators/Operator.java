/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class Operator extends OperatorImplementation {

    public Operator() {
    }

    /**
     * @param source
     */
    protected Operator(OperatorImplementation source) {
        super(source);
    }

    public enum OperatorName {
        blend((byte) 1, "BLEND"),
        voronoi((byte) 2, "VORONOI"),
        uniform_color((byte) 3, "RGB"),
        levels((byte) 4, "LEVELS"),
        normal_map((byte) 5, "NORMALS"),
        curve((byte) 6, "CURVE"),
        position((byte) 9, "POSITION"),
        multiply((byte) 10, "MUL"),
        grayscale_conversion((byte) 11, "GRAY"),
        noise((byte) 12, "NOISE"),
        transformation_trs((byte) 13, "TRS"),
        blur((byte) 14, "BLUR"),
        colorize((byte) 15, "COLOR"),
        sharpen((byte) 16, "SHARP"),
        add((byte) 17, "ADD"),
        divide((byte) 18, "DIVIDE"),
        combine((byte) 19, "COMBINE"),
        subtract((byte) 20, "SUB"),
        l2_norm((byte) 21, "NORM"),
        sawtooth_wave((byte) 22, "REPEAT");

        public final byte opCode;
        public final String displayName;

        OperatorName(byte opCode, String displayName) {
            this.opCode = opCode;
            this.displayName = displayName;
        }

        public static OperatorName get(byte opCode) {
            for (OperatorName op : values()) {
                if (op.opCode == opCode) {
                    return op;
                }
            }
            return null;
        }

    }

    @Override
    protected int getOpcodeSize() {
        throw new IllegalArgumentException();
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        throw new IllegalArgumentException();
    }

    @Override
    protected OpcodeInput[] internalGetOperatorInputs() {
        throw new IllegalArgumentException();
    }

}
