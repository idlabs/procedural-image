/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class Output {

    public static final String OPERATOR = "operation";
    public static final String NAME = "name";

    @SerializedName(OPERATOR)
    private int operatorIndex;
    @SerializedName(NAME)
    private String name;
}
