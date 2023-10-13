/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Hashtable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.ikea.digitallabs.dela.ErrorMessage;
import com.ikea.spatiallab.procedural.Asset;
import com.ikea.spatiallab.procedural.operators.IndexParameter.IndexType;

/**
 *
 */
public class ProceduralImage {

    public static final String OLLM_EXTENSION = "ollm";
    public static final String OLL_EXTENSION = "oll";

    public enum Target {
        NORMAL("normal"),
        RGB("base_color"),
        ROUGHNESS("roughness");

        public final String value;

        Target(String value) {
            this.value = value;
        }
    }

    public static final String EXTRAS = "extras";
    public static final String OPERATORS = "operations";
    public static final String OUTPUTS = "outputs";
    public static final String ASSET = "asset";
    public static final String OFFSET = "offset";
    public static final String SCALE = "scale";

    @SerializedName(ASSET)
    private Asset asset;

    @SerializedName(EXTRAS)
    private Extras extras;

    @SerializedName(OPERATORS)
    private HashMap<String, Operator> operations;

    @SerializedName(OUTPUTS)
    private HashMap<String, Reference> outputs;

    @SerializedName(OFFSET)
    private float[] offset;

    @SerializedName(SCALE)
    private float[] scale;

    private transient int inputFloatIndex = 0;
    private transient int inputVec4Index = 0;
    private transient int outputIndex = 0;
    private transient Hashtable<String, IndexParameter> outputLinks = new Hashtable<String, IndexParameter>();
    protected transient ArrayDeque<String> refStack = new ArrayDeque<>();

    /**
     * 
     * @return
     */
    public float[] getScale() {
        return scale != null ? scale : new float[] { 2, 2, 2 };
    }

    /**
     * 
     * @return
     */
    public float[] getOffset() {
        return offset != null ? offset : new float[] { -1, -1, -1 };
    }

    /**
     * Returns the asset info
     * 
     * @return
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Returns the reference for the type if declared, otherwise null
     * 
     * @param target
     * @return
     */
    public Reference getOutput(Target target) {
        for (String k : outputs.keySet()) {
            if (k.endsWith(target.value)) {
                return outputs.get(k);
            }
        }
        return null;
    }

    /**
     * 
     * @param reference
     * @return
     */
    public OperatorImplementation getOperator(Reference reference) {
        String ref = reference.getOperatorReference();
        if (ref == null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "No operator reference " + reference
                    .getOperatorReference());
        }
        return getOperator(ref);
    }

    /**
     * Returns the operator for the key
     * 
     * @param key
     * @return
     */
    public OperatorImplementation getOperator(String key) {
        OperatorImplementation operator = operations.get(key);
        if (operator == null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "No operator for key " + key);
        }
        operator.setKey(key);
        return operator;
    }

    /**
     * Returns an array with all operators in the list
     * 
     * @return
     */
    public OperatorImplementation[] getOperators() {
        return operations.values().toArray(new OperatorImplementation[0]);
    }

    /**
     * 
     * @return
     */
    public String[] getOperatorKeys() {
        return operations.keySet().toArray(new String[0]);
    }

    /**
     * Returns the operator queue for the reference, this will not reset input and output indexes
     * Internal method - DO NOT USE
     * 
     * @param reference
     * @return
     */
    public OperatorRecipe getOperatorQueue(Reference reference) {
        OperatorImplementation op = getOperator(reference);
        return op.getOperatorQueue(this);
    }

    /**
     * Returns the operator queue for the specified target
     * 
     * @param target
     * @return
     */
    public OperatorRecipe getOutputOperatorQueue(Target target) {
        resetIndexes();
        Reference output = getOutput(target);
        return getOperatorQueue(output);
    }

    /**
     * 
     */
    public void resetIndexes() {
        inputFloatIndex = 0;
        inputVec4Index = 0;
        outputIndex = 0;
        outputLinks.clear();
        refStack.clear();
    }

    /**
     * Increment input index
     * 
     * @param indexParam
     */
    protected void incrementInputIndex(FloatParameter inParam) {
        switch (inParam.getType()) {
            case INDEX_INPUT_FLOAT:
                inputFloatIndex++;
                break;
            case INDEX_INPUT_VEC4:
            case INDEX_INPUT_VEC3:
                inputVec4Index++;
                break;
            case INDEX_INPUT_ARRAY:
                inputFloatIndex += inParam.getValues().length;
                break;
            default:
                throw new IllegalArgumentException(inParam.getType().name());
        }
    }

    /**
     * 
     * @param add
     */
    protected void incrementOutputIndex(int add) {
        outputIndex += add;
    }

    /**
     * 
     * @param ref
     * @return
     */
    protected IndexParameter getOutputLink(Reference ref) {
        return outputLinks.get(ref.getOperatorReference());
    }

    /**
     * 
     * @param ref
     * @return
     */
    protected IndexParameter getOutputLink(String ref) {
        return outputLinks.get(ref);
    }

    /**
     * 
     * @param ref
     */
    protected void setOutputLink(IndexParameter outParam, String ref) {
        if (outputLinks.contains(ref)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Already added outputlink for key "
                    + ref);
        }
        outputLinks.put(ref, outParam);
    }

    /**
     * 
     * @return
     */
    protected int getInputIndex(IndexType type) {
        switch (type) {
            case INDEX_INPUT_FLOAT:
            case INDEX_INPUT_ARRAY:
                return inputFloatIndex;
            case INDEX_INPUT_VEC4:
            case INDEX_INPUT_VEC3:
                return inputVec4Index;
            default:
                throw new IllegalArgumentException(type.name());
        }
    }

    /**
     * 
     * @return
     */
    protected int getOutputIndex() {
        return outputIndex;
    }

    public static final Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Parameter.class, new Parameter());
        builder.registerTypeAdapter(Operator.class, new OperatorDeserializer(builder.create()));
        builder.registerTypeAdapter(Extras.class, new Extras());
        return builder.create();
    }

    /**
     * Returns the extras object or null
     * 
     * @return
     */
    public Extras getExtras() {
        return extras;
    }

}
