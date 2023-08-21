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
    public static final String VERSION = "version";
    public static final String GENERATOR = "generator";

    @SerializedName(NAME)
    private String name;
    @SerializedName(GENERATOR)
    private String generator;
    @SerializedName(VERSION)
    private String version;

    /**
     * Returns the asset name
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the asset version
     * 
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the asset generator
     * 
     * @return
     */
    public String getGenerator() {
        return generator;
    }

}
