/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.SerializedName;
import com.ikea.digitallabs.dela.Buffers;
import com.ikea.digitallabs.dela.ErrorMessage;
import com.ikea.digitallabs.dela.logger.Logger;
import com.ikea.spatiallab.procedural.operators.IndexParameter.IndexType;
import com.ikea.spatiallab.procedural.operators.Operator.OperatorName;
import com.ikea.spatiallab.procedural.operators.OperatorRecipe.OperatorData;

/**
 *
 */
public abstract class OperatorImplementation {

    private enum DefaultInput implements OpcodeInput {
        input_offset(-50, 50),
        input_rotate(-3.14159f, 3.14159f),
        input_scale(0f, 50f);

        DefaultInput(float... minMax) {
            this.minMax = minMax;
        }

        private final float[] minMax;

        @Override
        public float[] getMinMax() {
            return minMax;
        }

        @Override
        public String getName() {
            return name();
        }
    }

    public static final String OPERATORNAME = "operation";
    public static final String PARAMETERS = "parameters";
    public static final String INPUT = "input";

    @SerializedName(OPERATORNAME)
    private OperatorName operatorName;
    @SerializedName(PARAMETERS)
    private HashMap<String, Parameter> parameters;

    private transient String listKey;

    OperatorImplementation() {

    }

    /**
     * The operator parameters
     */
    public interface OpcodeInput {
        String getName();

        /**
         * Returns the min and max allowed values if defined, otherwise null
         * 
         * @return
         */
        float[] getMinMax();
    }

    protected OperatorImplementation(OperatorImplementation source) {
        this.operatorName = source.operatorName;
        this.parameters = source.parameters;
        this.listKey = source.listKey;
    }

    public abstract static class InputData<T> {

        private T data;

        /**
         * 
         * @param inData
         */
        protected void add(T inData) {
            this.data = inData;
        }

        /**
         * 
         * @return
         */
        public T get() {
            return data;
        }

        protected abstract int getByteSize();

        /**
         * Returns the number of elements, for float data a vec4 will return 4.
         * A float values will return 1
         * An index will return 1
         * 
         * @return
         */
        protected abstract int getElementCount();

        protected static FloatInputData create(float[] inData) {
            return new FloatInputData(inData);
        }

        protected static IndexInputData create(IndexParameter inData) {
            return new IndexInputData(inData);
        }

    }

    public static class FloatInputData extends InputData<float[]> {

        public static final int FLOAT_SIZE_IN_BYTES = Float.BYTES;
        public static final int VEC4_SIZE_IN_BYTES = 4 * Float.BYTES;

        protected FloatInputData(float[] inData) {
            add(inData);
        }

        @Override
        public String toString() {
            return Buffers.toString(get());
        }

        @Override
        protected int getByteSize() {
            return get().length * Float.BYTES;
        }

        @Override
        protected int getElementCount() {
            return get().length;
        }

    }

    public static class IndexInputData extends InputData<IndexParameter> {

        protected IndexInputData(IndexParameter inData) {
            add(inData);
        }

        @Override
        public String toString() {
            IndexParameter index = get();
            return "[" + index.key + ", " + index.index + ", " + index.type + "]";
        }

        @Override
        protected int getByteSize() {
            return Short.BYTES;
        }

        @Override
        protected int getElementCount() {
            return 1;
        }
    }

    /**
     * Returns the key for the operator, this is the json linked list name.
     * 
     * @return
     */
    public String getKey() {
        return listKey;
    }

    /**
     * 
     * @param key
     */
    protected void setKey(String key) {
        if (listKey != null && !listKey.contentEquals(key)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Key is already set to: "
                    + this.listKey);
        }
        listKey = key;
    }

    /**
     * 
     * @return
     */
    public OperatorName getOperator() {
        return operatorName;
    }

    /**
     * 
     * @return
     */
    public Set<String> getParameterKeySet() {
        return parameters != null ? parameters.keySet() : new HashSet<String>();
    }

    /**
     * 
     * @param key
     * @return
     */
    public Parameter getParameter(String key) {
        return parameters.get(key);
    }

    /**
     * Returns the input if declared, otherwise null
     * 
     * @return
     */
    public Reference getInput() {
        Parameter input = parameters.get(INPUT);
        if (input != null) {
            return (Reference) input;
        }
        return null;
    }

    /**
     * Returns true if any of the parameters is a reference
     * 
     * @return
     */
    protected boolean hasReference() {
        for (Parameter p : parameters.values()) {
            if (p instanceof Reference) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the size in bytes for this operators bytecode.
     * 
     * @return
     */
    protected int getOpcodeSize() {
        OpcodeInput[] inputs = getDeclaredInputs();
        return inputs != null ? inputs.length * OpcodeData.OPCODE_INDEXSIZE + OpcodeData.OPCODE_OPSIZE
                : OpcodeData.OPCODE_OPSIZE;
    }

    /**
     * 
     * @param ol
     * @return
     */
    public OperatorRecipe getOperatorQueue(ProceduralImage ol) {
        OperatorRecipe list = new OperatorRecipe(ol.getOffset(), ol.getScale());
        OperatorData data = new OperatorData(this);
        OpcodeInput[] in = getDeclaredInputs();
        ol.refStack.push(listKey);
        for (String key : getParameterKeySet()) {
            Parameter p = getParameter(key);
            OpcodeInput bi = data.getOperator().getOpcodeInput(key);
            if (bi != null) {
                if (p instanceof Reference) {
                    String opReference = ((Reference) p).getOperatorReference();
                    if (ol.refStack.contains(opReference)) {
                        throw new IllegalArgumentException(ErrorMessage.INVALID_STATE.message + "Cyclic reference for "
                                + opReference + " in operator " + listKey);
                    }
                    OperatorRecipe l = ol.getOperatorQueue((Reference) p);
                    IndexParameter outputLink = ol.getOutputLink(opReference);
                    if (outputLink != null) {
                        Logger.i(getClass(), "Reusing outputLink for ref: " + opReference);
                        data.addInput(key, outputLink);
                        list.addLast(l);
                    } else {
                        list.add(l);
                        OperatorData inputData = list.getOperatorByKey(opReference);
                        if (!inputData.getOperator().getKey().contentEquals(opReference)) {
                            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message
                                    + "Misformed list, last output is " + inputData.getOperator().getKey()
                                    + ", but input needs " + opReference);
                        }
                        // The input (key) must point to last output from fetched operator queue (l)
                        OperatorData opOutput = l.getOperatorByKey(opReference);
                        outputLink = new IndexParameter(opReference, opOutput.getDestination().index,
                                IndexType.INDEX_OUTPUT_VEC4);
                        data.addInput(key, outputLink);
                    }

                } else {
                    IndexParameter outputParam = ol.getOutputLink(listKey);
                    if (outputParam == null) {
                        FloatParameter inParam = (FloatParameter) p;
                        IndexType type = inParam.getType();
                        if (type == null) {
                            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "No data for " + key
                                    + " in operator " + data.getOperator().getOperator() + " '" + data
                                            .getOperator().listKey + "'");
                        }
                        IndexParameter indexParam = new IndexParameter(key, ol.getInputIndex(type), type);
                        data.addInput(key, indexParam);
                        ol.incrementInputIndex(inParam);
                        Logger.i(getClass(), "Adding IndexParameter for " + listKey + ", " + key + " : "
                                + indexParam.index);
                    }
                }
            }
        }
        IndexParameter outputParam = ol.getOutputLink(listKey);
        if (outputParam != null) {
            Logger.i(getClass(), "Skipping operator, already output: " + listKey);
        } else {
            outputParam = new IndexParameter(listKey, ol.getOutputIndex(),
                    IndexType.INDEX_OUTPUT_VEC4);
            ol.incrementOutputIndex(1);
            ol.setOutputLink(outputParam, listKey);
            list.setDestination(data, outputParam);
            list.addLast(data);
            Logger.i(getClass(), "Adding operator " + data.getOperator().getOperator() + ", key: " + listKey);
            list.validateInputs(data, ol);
        }
        ol.refStack.pop();
        return list;
    }

    /**
     * Returns the key as FloatParameter, or null if not set or not FloatParameter
     * 
     * @param key
     * @return
     */
    protected FloatParameter getFloatParameter(String key) {
        if (parameters != null) {
            Parameter p = parameters.get(key);
            if (p instanceof FloatParameter) {
                return (FloatParameter) parameters.get(key);
            }
        }
        return null;
    }

    /**
     * 
     * @param key
     * @param indexes
     * @return
     */
    public InputData<?> getInputData(String key, HashMap<String, IndexParameter> indexes) {
        FloatParameter parameter = getFloatParameter(key);
        if (parameter != null) {
            return InputData.create(parameter.getValues());
        } else {
            IndexParameter input = indexes.get(key);
            return InputData.create(input);
        }

    }

    protected abstract void addOpcode(OpcodeData opCodes);

    /**
     * Returns an array with the inputs defined for the operator - not the common inputs.
     * 
     * @return
     */
    protected abstract OpcodeInput[] internalGetOperatorInputs();

    /**
     * 
     * @param name
     * @return
     */
    protected OpcodeInput getOpcodeInput(String name) {
        for (OpcodeInput bi : internalGetOperatorInputs()) {
            if (name.contentEquals(bi.getName())) {
                return bi;
            }
        }
        // Check for special inputs
        for (OpcodeInput bi : DefaultInput.values()) {
            if (name.contentEquals(bi.getName())) {
                return bi;
            }
        }
        return null;
    }

    /**
     * Returns the inputs that are declared in the 'parameters' array - the returned array SHALL be sorted
     * in order of how inputs are consumed.
     * 
     * @return
     */
    public OpcodeInput[] getDeclaredInputs() {
        ArrayList<OpcodeInput> inputs = new ArrayList<>();
        if (parameters != null) {
            OpcodeInput[] opInputs = internalGetOperatorInputs();
            if (opInputs != null) {
                for (OpcodeInput mandatory : opInputs) {
                    inputs.add(mandatory);
                }
            }
            for (OpcodeInput optional : DefaultInput.values()) {
                if (parameters.containsKey(optional.getName())) {
                    inputs.add(optional);
                }
            }
        }
        return inputs.toArray(new OpcodeInput[0]);
    }

    /**
     * 
     * @param opData
     * @param opCodes
     */
    public void getBytecode(OperatorData opData, OpcodeData opCodes) {
        addOpcode(opCodes);
        Collection<String> keys = opData.getIndexKeys();
        opCodes.addOutIndexAndInCount(opData.getDestination(), (byte) keys.size());
        // Find input by looking up keys - keys must be sorted by order of consumption!
        OpcodeInput[] inputs = getDeclaredInputs();
        for (OpcodeInput in : inputs) {
            IndexParameter index = opData.getInputIndex(in.getName());
            if (index == null) {
                // Malformed json
                throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "No parameter data for " + in
                        .getName());
            }
            InputData<?> input = opData.getInputData(index);
            if (input instanceof FloatInputData) {
                if (input.getByteSize() == FloatInputData.FLOAT_SIZE_IN_BYTES) {
                    opCodes.addFloatInput(((FloatInputData) input).get(), index);
                } else if (input.getByteSize() <= FloatInputData.VEC4_SIZE_IN_BYTES) {
                    opCodes.addVec4Input(((FloatInputData) input).get(), index);
                } else {
                    // Array
                    opCodes.addArrayInput(((FloatInputData) input).get(), index);
                }

            }
        }
        if (inputs != null) {
            for (OpcodeInput bi : inputs) {
                IndexParameter inParam = opData.getInputIndex(bi.getName());
                if (inParam == null) {
                    throw new IllegalArgumentException(ErrorMessage.INVALID_STATE.message);
                }
                opCodes.addIndexInput(inParam);
            }
        }
    }

    /**
     * Override as needed to return true for inputs that may be null
     * 
     * @param index
     * @return
     */
    public boolean allowNullInput(IndexParameter index) {
        return false;
    }

}
