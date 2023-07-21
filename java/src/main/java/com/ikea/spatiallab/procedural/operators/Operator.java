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
        blend((byte) 1),
        voronoi((byte) 2),
        uniform_color((byte) 3),
        levels((byte) 4),
        normal_map((byte) 5),
        curve((byte) 6),
        position((byte) 9),
        multiply((byte) 10),
        grayscale_conversion((byte) 11),
        noise((byte) 12);

        public final byte opCode;

        OperatorName(byte opCode) {
            this.opCode = opCode;
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
    protected OpcodeInput[] getInputs() {
        throw new IllegalArgumentException();
    }

}
