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
import com.ikea.digitallabs.dela.ErrorMessage;

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
        if (operator == null || operator.getOperator() == null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "No operator for: " + json
                    .toString());
        }
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
            case transformation_trs:
                return new TransformTRSOperator(operator);
            case blur:
                return new BlurOperator(operator);
            case colorize:
                return new ColorizeOperator(operator);
            case sharpen:
                return new SharpenOperator(operator);
            case add:
                return new AddOperator(operator);
            case divide:
                return new DivideOperator(operator);
            case combine:
                return new CombineOperator(operator);
            case subtract:
                return new SubtractOperator(operator);
            case l2_norm:
                return new L2NormOperator(operator);
            case sawtooth_wave:
                return new SawtoothWaveOperator(operator);
            default:
                throw new IllegalArgumentException(operator.getOperator().name());
        }
    }

}
