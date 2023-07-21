/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 *
 */
public class Parameter implements JsonDeserializer<Parameter> {

    @Override
    public Parameter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            int size = array.size();
            float[] values = new float[size];
            for (int i = 0; i < size; i++) {
                JsonElement element = array.get(i);
                values[i] = element.getAsFloat();
            }
            return new FloatParameter(values);
        } else if (json.isJsonObject()) {
            JsonObject jsonobj = json.getAsJsonObject();
            JsonElement jsonref = jsonobj.get("$ref");
            String ref = jsonref.getAsString();
            return new Reference(ref);
        } else if (json.isJsonPrimitive()) {
            return new FloatParameter(json.getAsFloat());
        }
        throw new IllegalArgumentException();
    }

}
