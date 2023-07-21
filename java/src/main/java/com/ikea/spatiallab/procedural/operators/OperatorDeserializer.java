/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 *
 */
public class OperatorDeserializer implements JsonDeserializer<OperatorImplementation> {

    private final Gson gson;

    public OperatorDeserializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public OperatorImplementation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Operator operator = gson.fromJson(json, Operator.class);

        switch (operator.getOperator()) {
            case blend:
                return new BlendOperator(operator);
            case curve:
                return new CurveOperator(operator);
            case uniform_color:
                return new UniformColorOperator(operator);
            case levels:
                return new LevelsOperator(operator);
            case normal_map:
                return new NormalMapOperator(operator);
            case voronoi:
                return new VoronoiOperator(operator);
            case position:
                return new PositionOperator(operator);
            case multiply:
                return new MultiplyOperator(operator);
            case grayscale_conversion:
                return new GrayscaleConversionOperator(operator);
            case noise:
                return new NoiseOperator(operator);
            default:
                throw new IllegalArgumentException(operator.getOperator().name());
        }
    }

}
