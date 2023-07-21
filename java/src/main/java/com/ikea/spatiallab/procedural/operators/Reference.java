/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import com.google.gson.annotations.SerializedName;
import com.ikea.digitallabs.dela.ErrorMessage;

/**
 *
 */
public class Reference extends Parameter {

    public static final String OPERATOR_REF = "#/operations/";

    public static final String REF = "$ref";

    @SerializedName(REF)
    private String ref;

    public Reference() {
    }

    public Reference(String ref) {
        this.ref = ref;
    }

    /**
     * Key is the name of the target, eg "n1"
     * 
     * @param key
     * @throws IllegalArgumentException If key is already set
     */
    public void setOperatorRef(String key) {
        if (ref != null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Reference already set: " + ref);
        }
        this.ref = OPERATOR_REF + key;
    }

    /**
     * Returns the full reference
     * 
     * @return
     */
    public String getReference() {
        return ref;
    }

    /**
     * Returns the operator key if this is an operator ref, otherwise null
     * 
     * @return
     */
    public String getOperatorReference() {
        if (ref == null || !ref.startsWith(OPERATOR_REF)) {
            return null;
        }
        return ref.substring(OPERATOR_REF.length());
    }

    @Override
    public String toString() {
        return ref;
    }

}
