/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.SerializedName;
import com.ikea.digitallabs.dela.ErrorMessage;
import com.ikea.spatiallab.procedural.operators.IndexParameter.IndexType;
import com.ikea.spatiallab.procedural.operators.Operator.OperatorName;
import com.ikea.spatiallab.procedural.operators.OperatorListResolver.OperatorData;

/**
 *
 */
public abstract class OperatorImplementation {

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

    interface OpcodeInput {

        String getName();

        byte getIndex();
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
            StringBuffer str = new StringBuffer("[");
            for (float d : get()) {
                str.append((str.length() > 1 ? "," : "") + Float.toString(d));
            }
            str.append("]");
            return str.toString();
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
     * 
     * @return
     */
    protected String getKey() {
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
        OpcodeInput[] inputs = getInputs();
        return inputs != null ? inputs.length * Short.BYTES + 4 : 4;
    }

    /**
     * 
     * @param ol
     * @return
     */
    public OperatorListResolver getOperatorQueue(ProceduralImage ol) {
        OperatorListResolver list = new OperatorListResolver();
        OperatorData data = new OperatorData(this);
        for (String key : getParameterKeySet()) {
            Parameter p = getParameter(key);
            OpcodeInput bi = data.getOperator().getOpcodeInput(key);
            if (bi != null) {
                if (p instanceof Reference) {
                    OperatorListResolver l = ol.getOperatorQueue((Reference) p);
                    list.add(l);
                    String opReference = ((Reference) p).getOperatorReference();
                    OperatorData inputData = list.getOperatorByKey(opReference);
                    if (!inputData.getOperator().getKey().contentEquals(opReference)) {
                        throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message
                                + "Misformed list, last output is " + inputData.getOperator().getKey()
                                + ", but input needs " + opReference);
                    }
                    // The input (key) must point to last output from fetched operator queue (l)
                    OperatorData opOutput = l.getOperatorByKey(opReference);
                    IndexParameter outputLink = new IndexParameter(opReference, opOutput.getDestination().index,
                            IndexType.INDEX_OUTPUT_VEC4);
                    data.addInput(key, outputLink);

                } else {
                    FloatParameter inParam = (FloatParameter) p;
                    IndexParameter indexParam = new IndexParameter(key, ol.getInputIndex(inParam.getType()), inParam
                            .getType());
                    data.addInput(key, indexParam);
                    ol.incrementInputIndex(inParam);
                }
            }
        }
        IndexParameter outputParam = new IndexParameter(IndexType.INDEX_OUTPUT_VEC4.name(), ol.getOutputIndex(),
                IndexType.INDEX_OUTPUT_VEC4);
        ol.incrementOutputIndex(1);
        list.setDestination(data, outputParam);
        list.addLast(data);
        list.validateInputs(data);
        return list;

    }

    /**
     * 
     * @param indexes
     * @return
     */
    public InputData<?>[] getInputData(HashMap<String, IndexParameter> indexes) {
        OpcodeInput[] inputs = getInputs();
        if (inputs != null) {
            InputData<?>[] result = new InputData<?>[inputs.length];
            for (OpcodeInput in : inputs) {
                result[in.getIndex()] = getInputData(in.getName(), indexes);
            }
            return result;
        }
        return new InputData<?>[0];
    }

    /**
     * Returns the key as FloatParameter, or null if not set or not FloatParameter
     * 
     * @param key
     * @return
     */
    protected FloatParameter getFloatParameter(String key) {
        Parameter p = parameters.get(key);
        if (p instanceof FloatParameter) {
            return (FloatParameter) parameters.get(key);
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
     * Returns an array with inputs - or null if no inputs for the operator.
     * 
     * @return
     */
    protected abstract OpcodeInput[] getInputs();

    /**
     * 
     * @param name
     * @return
     */
    protected OpcodeInput getOpcodeInput(String name) {
        for (OpcodeInput bi : getInputs()) {
            if (name.contentEquals(bi.getName())) {
                return bi;
            }
        }
        return null;
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
        // Find input by looking up keys
        for (String key : opData.getIndexKeys()) {
            IndexParameter index = opData.getInputIndex(key);
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
        OpcodeInput[] inputs = getInputs();
        if (inputs != null) {
            for (OpcodeInput bi : inputs) {
                IndexParameter inParam = opData.getInputIndex(bi.getName());
                opCodes.addIndexInput(inParam);
            }
        }
    }

}
