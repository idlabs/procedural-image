/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural;

import com.google.gson.annotations.SerializedName;

/**
 * 
 */
public class Asset {

    public static final String NAME = "name";
    public static final String GENERATOR = "generator";

    @SerializedName(NAME)
    private String name;
    @SerializedName(GENERATOR)
    private String generator;

}
