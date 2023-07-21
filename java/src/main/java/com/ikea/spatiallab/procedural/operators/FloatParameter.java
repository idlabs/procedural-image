/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import com.ikea.digitallabs.dela.ErrorMessage;
import com.ikea.spatiallab.procedural.operators.IndexParameter.IndexType;

/**
 *
 */
public class FloatParameter extends Parameter {

    private final float[] values;

    public FloatParameter(float... values) {
        this.values = values;
    }

    /**
     * 
     * @return
     */
    public float[] getValues() {
        return values;
    }

    /**
     * Returns the number of values
     * 
     * @return
     */
    public int getNumberOfValues() {
        return values.length;
    }

    /**
     * 
     * @return
     */
    public float getValue() {
        if (values.length > 1) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "FloatParameter contains array");
        }
        return values[0];
    }

    /**
     * Returns the value as int
     * 
     * @return
     */
    public int getAsIntValue() {
        return (int) getValue();
    }

    /**
     * 
     * @return
     */
    public IndexType getType() {
        switch (values.length) {
            case 1:
                return IndexType.INDEX_INPUT_FLOAT;
            case 3:
                return IndexType.INDEX_INPUT_VEC3;
            case 4:
                return IndexType.INDEX_INPUT_VEC4;
            default:
                if (values.length > 0) {
                    return IndexType.INDEX_INPUT_ARRAY;
                }
                return null;
        }
    }

}
