/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ikea.digitallabs.dela.Constants;
import com.ikea.digitallabs.dela.ErrorMessage;
import com.ikea.spatiallab.procedural.operators.IndexParameter.IndexType;
import com.ikea.spatiallab.procedural.operators.OperatorImplementation.FloatInputData;
import com.ikea.spatiallab.procedural.operators.OperatorImplementation.InputData;
import com.ikea.spatiallab.procedural.operators.OperatorImplementation.OpcodeInput;
import com.ikea.spatiallab.procedural.operators.ProceduralImage.Target;

/**
 * Resolves an operatorlist with references into a queue/list that can be traversed
 */
public class OperatorRecipe {

    private final LinkedList<OperatorData> list = new LinkedList<OperatorData>();
    private HashMap<Integer, IndexParameter> destinations = new HashMap<Integer, IndexParameter>();
    private float[] offset;
    private float[] scale;
    private float zoom = 1;

    OperatorRecipe(float[] offset, float[] scale) {
        this.offset = offset;
        this.scale = scale;
    }

    /**
     * The data needed to resolve operators from JSON list with references.
     */
    public static class OperatorData {

        private static int nextId = 0x0800;

        private final OperatorImplementation operator;
        private HashMap<String, IndexParameter> indexes = new HashMap<String, IndexParameter>();
        private IndexParameter destination;
        public final int id;

        protected OperatorData(OperatorImplementation operator) {
            this.operator = operator;
            this.id = nextId++;
        }

        /**
         * Adds an input index, throws exception if key is already set
         * 
         * @param key
         * @param index
         */
        protected void addInput(String key, IndexParameter index) {
            if (indexes.containsKey(key)) {
                throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Input with key " + key
                        + " is already set");
            }
            this.indexes.put(key, index);
        }

        /**
         * 
         * @return
         */
        public OperatorImplementation getOperator() {
            return operator;
        }

        /**
         * 
         * @param key
         * @return
         */
        public IndexParameter getInputIndex(String key) {
            return indexes.get(key);
        }

        /**
         * Returns the set of keys for input indexes - use to fetch index parameter by
         * calling {@link #getInputIndex(String)}
         * 
         * @return
         */
        protected Collection<String> getIndexKeys() {
            return indexes.keySet();
        }

        /**
         * 
         * @param index
         * @return
         */
        protected InputData<?> getInputData(IndexParameter index) {
            return operator.getInputData(index.key, indexes);
        }

        /**
         * Returns the inputdata for the opcode input
         * 
         * @param input
         * @return
         */
        public InputData<?> getInputData(OpcodeInput input) {
            return operator.getInputData(input.getName(), indexes);
        }

        /**
         * Returns the inputdata for the parametername
         * 
         * @param parameterName
         * @return
         */
        public InputData<?> getInputData(String parameterName) {
            return operator.getInputData(parameterName, indexes);
        }

        /**
         * Sets the destination index for this operator - throws exception if destination already set or
         * if index type is not output
         * 
         * @param destination
         * @throws IllegalArgumentException if destination has already been set or index type is not output
         */
        private void setDestination(IndexParameter destination) {
            if (!destination.isOutput()) {
                throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Index is not type output.");
            }
            if (this.destination != null) {
                throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Destination already set: "
                        + this.destination.key);
            }
            this.destination = destination;
        }

        /**
         * 
         * @return
         */
        public IndexParameter getDestination() {
            return destination;
        }

        /**
         * Returns the min and max value for the parameter
         * 
         * @return
         */
        public float[] getMinMax() {
            return null;
        }

    }

    /**
     * 
     * @param op
     * @param destination
     */
    protected void setDestination(OperatorData op, IndexParameter destination) {
        if (destinations.containsKey(destination.index)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Already added destination index "
                    + destination.index);
        }
        op.setDestination(destination);
        destinations.put(destination.index, destination);
    }

    /**
     * Sets the final destination target
     * 
     * @param outputTarget
     */
    protected void setDestination(Target outputTarget) {
        OperatorData output = list.getLast();
        if (output.getDestination() != null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Destination already set");
        }
        setDestination(output, new IndexParameter(outputTarget.name(), -1, IndexType.INDEX_OUTPUT_VEC4));
    }

    /**
     * Returns the sorted list of operator data
     * 
     * @return
     */
    public List<OperatorData> getList() {
        return list;
    }

    /**
     * Returns the number of operators in the list.
     * 
     * @return
     */
    public int getOperatorCount() {
        return list.size();
    }

    /**
     * 
     * @param index
     * @return
     */
    public OperatorData getOperatorData(int index) {
        return index >= 0 && index < list.size() ? list.get(index) : null;
    }

    /**
     * 
     * @param data
     */
    protected void addFirst(OperatorData data) {
        list.add(0, data);
    }

    /**
     * 
     * @param data
     */
    protected void addLast(OperatorData data) {
        list.add(data);
    }

    /**
     * 
     * @return
     */
    protected int getTotalOpcodeSize() {
        int bytesize = OpcodeData.OPCODE_HEADER_SIZE;
        for (OperatorData od : list) {
            bytesize += od.operator.getOpcodeSize();
        }
        return bytesize;
    }

    /**
     * 
     * @return
     */
    protected int getFloatInputSize() {
        int inputsize = 0;
        for (OperatorData od : list) {
            Collection<String> keys = od.getIndexKeys();
            for (String key : keys) {
                IndexParameter index = od.getInputIndex(key);
                InputData<?> input = od.getInputData(index);
                if (input == null && !od.getOperator().allowNullInput(index)) {
                    throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Input is null for " + od
                            .getOperator().getKey() + ", " + key);
                } else {
                    if (index.type == IndexType.INDEX_INPUT_FLOAT || index.type == IndexType.INDEX_INPUT_ARRAY) {
                        inputsize += input.getByteSize();
                    }
                }
            }
        }
        return inputsize;
    }

    /**
     * 
     * @return
     */
    protected int getVec4InputSize() {
        int inputsize = 0;
        for (OperatorData od : list) {
            Collection<String> keys = od.getIndexKeys();
            for (String key : keys) {
                IndexParameter index = od.getInputIndex(key);
                InputData<?> input = od.getInputData(index);
                if (input == null) {
                    throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Input is null");
                }
                if (index.type == IndexType.INDEX_INPUT_VEC4 || index.type == IndexType.INDEX_INPUT_VEC3) {
                    inputsize += FloatInputData.VEC4_SIZE_IN_BYTES;
                }
            }
        }
        return inputsize;
    }

    /**
     * 
     * @param data
     */
    protected void validateInputs(OperatorData data, ProceduralImage pi) {
        Set<String> keySet = data.operator.getParameterKeySet();
        for (String key : keySet) {
            Parameter param = data.operator.getParameter(key);
            if (param instanceof Reference) {
                IndexParameter index = data.getInputIndex(key);
                if (index == null) {
                    if (param instanceof Reference) {
                        // Means that operator is referencing an operator that has not been resolved yet.
                        OperatorImplementation ref = pi.getOperator((Reference) param);
                        if (ref == null) {
                            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "No ref for key: "
                                    + key
                                    + " in operator " + data.operator.getOperator());
                        }
                    } else {
                        throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "No data for " + key
                                + " in operator " + data.operator.getOperator());
                    }
                } else {
                    IndexParameter link = pi.getOutputLink(data.operator.getKey());
                    if (link == null) {
                        // Need to find matching input in list.
                        int listIndex = list.indexOf(data);
                        if (listIndex == Constants.NO_VALUE) {
                            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Data not in list");
                        }
                        if (listIndex == 0) {
                            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message
                                    + "Only last data item in list");
                        }
                        boolean found = false;
                        for (int i = listIndex - 1; i >= 0; i--) {
                            if (list.get(i).getDestination().index == index.index) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            throw new IllegalArgumentException(ErrorMessage.INVALID_STATE.message
                                    + "Input reference not found in list");
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * @param source
     */
    protected void add(OperatorRecipe source) {
        list.addAll(0, source.getList());
    }

    /**
     * 
     * @param source
     */
    protected void addLast(OperatorRecipe source) {
        list.addAll(list.size(), source.getList());
    }

    /**
     * Returns the operator for the key
     * 
     * @return
     */
    public OperatorData getOperatorByKey(String key) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).operator.getKey().contentEquals(key)) {
                return list.get(i);
            }
        }
        return null;
    }

    /**
     * Returns an array with opcodes that are using the 'key' opcode as input, or null if none
     * 
     * @param key
     * @param boolean true to include dependencies in search
     * @return
     */
    public OperatorData[] getOpcodesUsingOutput(String key, boolean includeDependencies) {
        ArrayList<OperatorData> result = new ArrayList<OperatorData>();
        ArrayList<String> keyList = new ArrayList<String>();
        keyList.add(key);
        for (OperatorData opData : list) {
            OperatorData match = null;
            for (String keyStr : keyList) {
                Set<String> parameters = opData.getOperator().getParameterKeySet();
                for (String p : parameters) {
                    Parameter param = opData.getOperator().getParameter(p);
                    if (param instanceof Reference) {
                        String name = ((Reference) param).getOperatorReference();
                        if (keyStr.contentEquals(name)) {
                            result.add(opData);
                            match = opData;
                            break;
                        }
                    }
                }
                if (match != null) {
                    break;
                }
            }
            if (match != null && includeDependencies) {
                keyList.add(match.getOperator().getKey());
            }
        }
        return result.size() > 0 ? result.toArray(new OperatorData[0]) : null;
    }

    /**
     * 
     */
    public OpcodeData getOpCodeData() {
        OpcodeData opCodes = new OpcodeData(getTotalOpcodeSize(), getFloatInputSize(), getVec4InputSize());
        opCodes.addHeader((byte) list.size());
        for (OperatorData od : list) {
            od.operator.getBytecode(od, opCodes);
        }
        if (!opCodes.validate()) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Opcode data is invalid");
        }
        opCodes.getOpcodeBuffer().position(0);
        return opCodes;
    }

    /**
     * Sets the zoom factor
     * 
     * @param zoomFactor
     */
    public void setZoom(float zoomFactor) {
        this.zoom = zoomFactor;
    }

    /**
     * 
     * @return
     */
    public float[] getOffset() {
        float deltaX = 1f * scale[0];
        float midX = offset[0] + (deltaX / 2);
        float deltaY = 1f * scale[1];
        float midY = offset[1] + (deltaY / 2);
        float deltaZ = 1f * scale[2];
        float midZ = offset[2] + (deltaZ / 2);
        return new float[] { midX - (deltaX / zoom) / 2, midY - (deltaY / zoom) / 2, midZ - (deltaZ / zoom) / 2 };
    }

    /**
     * 
     * @return
     */
    public float[] getScale() {
        return new float[] { scale[0] / zoom, scale[1] / zoom, scale[2] / zoom };
    }

}
