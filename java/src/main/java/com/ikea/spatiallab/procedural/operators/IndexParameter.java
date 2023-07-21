/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 *
 */
public class IndexParameter extends Parameter {

    public enum IndexType {
        INDEX_INPUT_FLOAT(),
        INDEX_INPUT_VEC4(),
        INDEX_INPUT_VEC3(),
        INDEX_INPUT_ARRAY(),
        INDEX_OUTPUT_FLOAT(),
        INDEX_OUTPUT_VEC4;
    }

    public final String key;
    public final int index;
    public final IndexType type;

    protected IndexParameter(String key, int index, IndexType type) {
        this.key = key;
        this.index = index;
        this.type = type;
    }

    /**
     * 
     * @return
     */
    boolean isOutput() {
        return type == IndexType.INDEX_OUTPUT_FLOAT || type == IndexType.INDEX_OUTPUT_VEC4;
    }

    /**
     * Returns the index as an opcode
     * 
     * @return
     */
    protected Short getOpcode() {
        return isOutput() ? (short) (index | 0x8000) : (short) index;
    }

}
