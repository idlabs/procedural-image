/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

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
import com.ikea.spatiallab.procedural.operators.ProceduralImage.Target;

/**
 * Resolves an operatorlist with references into a queue/list that can be traversed
 */
public class OperatorListResolver {

    private final LinkedList<OperatorData> list = new LinkedList<OperatorData>();
    private HashMap<Integer, IndexParameter> destinations = new HashMap<Integer, IndexParameter>();

    OperatorListResolver() {
    }

    /**
     * The data needed to resolve operators from JSON list with references.
     */
    public static class OperatorData {

        private final OperatorImplementation operator;
        private HashMap<String, IndexParameter> indexes = new HashMap<String, IndexParameter>();
        private IndexParameter destination;

        protected OperatorData(OperatorImplementation operator) {
            this.operator = operator;
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
        protected IndexParameter getInputIndex(String key) {
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
         * @return
         */
        public InputData<?>[] getInputData() {
            return operator.getInputData(indexes);
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
         * Returns the size of input data, in bytes - this will only count input data that is included,
         * not referenced outputs.
         * 
         * @return
         */
        protected int getInputSize() {
            int inputSize = 0;
            InputData<?>[] inputs = getInputData();
            for (InputData<?> input : inputs) {
                if (input instanceof FloatInputData) {
                    inputSize += ((FloatInputData) input).getByteSize();
                }
            }
            return inputSize;
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
     * 
     * @return
     */
    List<OperatorData> getList() {
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
    protected OperatorData getOperatorData(int index) {
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
                if (input == null) {
                    throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Input is null");
                }
                if (index.type == IndexType.INDEX_INPUT_FLOAT || index.type == IndexType.INDEX_INPUT_ARRAY) {
                    inputsize += input.getByteSize();
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
                    inputsize += input.getByteSize();
                }
            }
        }
        return inputsize;
    }

    /**
     * 
     * @param data
     */
    protected void validateInputs(OperatorData data) {
        Set<String> keySet = data.operator.getParameterKeySet();
        for (String key : keySet) {
            Parameter param = data.operator.getParameter(key);
            if (param instanceof Reference) {
                IndexParameter index = data.getInputIndex(key);
                if (index == null) {
                    throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "No index for key: " + key);
                }
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

    /**
     * 
     * @param source
     */
    protected void add(OperatorListResolver source) {
        list.addAll(0, source.getList());
    }

    /**
     * Returns the operator for the key
     * 
     * @return
     */
    protected OperatorData getOperatorByKey(String key) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).operator.getKey().contentEquals(key)) {
                return list.get(i);
            }
        }
        return null;
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
        return opCodes;
    }

}
