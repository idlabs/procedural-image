/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.ikea.spatiallab.procedural.operators.Operator;
import com.ikea.spatiallab.procedural.operators.OperatorDeserializer;
import com.ikea.spatiallab.procedural.operators.Parameter;
import com.ikea.spatiallab.procedural.operators.ProceduralImage;

/**
 *
 */
public class Module {

    public static final String NAME = "name";
    public static final String OLL = "oll";

    @SerializedName(NAME)
    private String name;
    @SerializedName(OLL)
    private ProceduralImage ol;

    public static final Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Parameter.class, new Parameter());
        builder.registerTypeAdapter(Operator.class, new OperatorDeserializer(builder.create()));
        return builder.create();
    }

    /**
     * Returns the name
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the operatorlist
     * 
     * @return
     */
    public ProceduralImage getOperatorList() {
        return ol;
    }

}
