/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Objects;

import com.ikea.digitallabs.dela.Buffers;
import com.ikea.digitallabs.dela.ErrorMessage;
import com.ikea.digitallabs.dela.logger.Logger;
import com.ikea.spatiallab.procedural.operators.IndexParameter.IndexType;
import com.ikea.spatiallab.procedural.operators.Operator.OperatorName;
import com.ikea.spatiallab.procedural.operators.OperatorImplementation.FloatInputData;

/**
 * The binary representation of procedural image operators
 * Contains the data needed to render out a procedural image
 */
public class OpcodeData {

    public static class OpcodeBlob {

        private OpcodeBlob(ByteBuffer opCodes) {
            ShortBuffer opCodeShort = opCodes.asShortBuffer();
            int pos = opCodes.position();

            short val = opCodeShort.get();
            opcode = (byte) (val & 0x0ff);
            param = (byte) ((val >>> 8) & 0x0ff);
            val = opCodeShort.get();
            outputIndex = (byte) (val & 0x0ff);
            inputCount = (byte) ((val >>> 8) & 0x0ff);
            inputIndexes = new short[inputCount];
            for (int i = 0; i < inputCount; i++) {
                inputIndexes[i] = opCodeShort.get();
            }
            opCodes.position(pos + opCodeShort.position() * Short.BYTES);
        }

        byte opcode;
        byte param;
        byte outputIndex;
        byte inputCount;
        short[] inputIndexes;

        public short[] getInputIndexes() {
            return inputIndexes;
        }

    }

    public static final int OPCODE_HEADER_SIZE = 4;
    public static final int OPCODE_OPSIZE = 4; // Size per operator
    public static final int OPCODE_INDEXSIZE = Short.BYTES;
    public static final String OPCODE_MAGIC = "PI";

    private ByteBuffer opCodes;
    private ByteBuffer floatInputs;
    private ByteBuffer vec4Inputs;
    private int floatInputCount = 0;
    private int indexInputCount = 0;
    private int vec4InputCount = 0;
    private int opCodeCount = 0;
    private HashMap<String, OpcodeBag> opcodeOffsets = new HashMap<String, OpcodeBag>();

    public class OpcodeBag {

        public final int offset;
        public final int index;
        public final String key;
        private int drawCount = -1; // Number of opcodes to draw

        private OpcodeBag(String key, int index, int offset) {
            this.key = key;
            this.index = index;
            this.offset = offset;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Objects.hash(key);
            return result;
        }

        /**
         * Sets the number of opcodes to draw
         * 
         * @param count
         */
        public void setDrawCount(int count) {
            this.drawCount = count;
        }

        /**
         * Returns the number of opcodes to draw
         * 
         * @return
         */
        public int getDrawCount() {
            return drawCount;
        }

    }

    protected OpcodeData(int opCodeByteSize, int floatInputByteSize, int vec4InputSize) {
        opCodes = ByteBuffer.allocateDirect(opCodeByteSize).order(ByteOrder.LITTLE_ENDIAN);
        floatInputs = ByteBuffer.allocateDirect(floatInputByteSize).order(ByteOrder.LITTLE_ENDIAN);
        vec4Inputs = ByteBuffer.allocateDirect(vec4InputSize).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * 
     * @param opcodeCount
     */
    protected void addHeader(byte opcodeCount) {
        opCodes.put((byte) OPCODE_MAGIC.charAt(0));
        opCodes.put((byte) OPCODE_MAGIC.charAt(1));
        opCodes.put(opcodeCount);
        opCodes.put((byte) -1);
    }

    /**
     * Adds an opcode, will pad opcode with -1 byte to align.
     * 
     * @param opCode
     */
    protected void addOpcode(String key, byte opCode) {
        opcodeOffsets.put(key, new OpcodeBag(key, opCodeCount, opCodes.position()));
        opCodes.put(opCode);
        opCodes.put((byte) -1);
        opCodeCount++;
    }

    /**
     * Adds an opcode and parameter
     * 
     * @param opCode
     * @param param
     */
    protected void addOpcode(String key, byte opCode, byte param) {
        opcodeOffsets.put(key, new OpcodeBag(key, opCodeCount, opCodes.position()));
        opCodes.put(opCode);
        opCodes.put(param);
        opCodeCount++;
    }

    /**
     * Adds output index and input count
     * 
     * @param outputIndex
     * @param inputCount
     */
    protected void addOutIndexAndInCount(IndexParameter outputIndex, byte inputCount) {
        opCodes.put((byte) outputIndex.index);
        opCodes.put(inputCount);
    }

    /**
     * Adds one float input value
     * 
     * @param floatValue
     */
    protected void addFloatInput(float[] floatValue, IndexParameter index) {
        if (floatValue.length != 1) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Input must be 1 float");
        }
        if (index.isOutput() || index.type != IndexType.INDEX_INPUT_FLOAT) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Index must be type float input");
        }
        floatInputs.position(index.index * Float.BYTES);
        floatInputs.asFloatBuffer().put(floatValue);
        floatInputCount++;
    }

    /**
     * 
     * @param array
     * @param index
     */
    protected void addArrayInput(float[] array, IndexParameter index) {
        floatInputs.position(index.index * FloatInputData.FLOAT_SIZE_IN_BYTES);
        floatInputs.asFloatBuffer().put(array);
        floatInputCount += array.length;
    }

    /**
     * 
     * @param vec4
     */
    protected void addVec4Input(float[] vec4, IndexParameter index) {
        if (vec4.length < 3) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Input must be 3 or 4 floats");
        }
        if (index.isOutput() || (index.type != IndexType.INDEX_INPUT_VEC4 && index.type
                != IndexType.INDEX_INPUT_VEC3)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "Index must be type vec4 input");
        }
        vec4Inputs.position(index.index * FloatInputData.VEC4_SIZE_IN_BYTES);
        vec4Inputs.asFloatBuffer().put(vec4);
        vec4InputCount++;
    }

    /**
     * Adds an input index opcode
     * 
     * @param indexOpcode
     */
    protected void addIndexInput(IndexParameter inParam) {
        int pos = opCodes.position();
        opCodes.asShortBuffer().put(inParam.getOpcode());
        opCodes.position(pos + Short.BYTES);
        indexInputCount++;
    }

    /**
     * 
     * @return
     */
    public OpcodeBlob getBlob() {
        synchronized (opCodes) {
            if (opCodes.position() < OPCODE_HEADER_SIZE) {
                opCodes.position(OPCODE_HEADER_SIZE);
            }
            if (opCodes.position() == opCodes.capacity()) {
                return null;
            }
            return new OpcodeBlob(opCodes);
        }
    }

    /**
     * Returns the number of index inputs
     * 
     * @return
     */
    protected int getIndexInputCount() {
        return indexInputCount;
    }

    /**
     * Returns the number of floats in inputdata
     * 
     * @return
     */
    public int getFloatInputs() {
        return floatInputCount;
    }

    /**
     * Returns the number of opcode float inputs.
     * 
     * @return
     */
    public int getOpcodeCount() {
        return opCodeCount;
    }

    /**
     * Returns the opcode buffer at position 0
     * 
     * @return
     */
    public ByteBuffer getOpcodeBuffer() {
        return opCodes.position(0);
    }

    /**
     * Returns the opcode buffer at the operator with the key, returns null if not found
     * 
     * This method is not threadsafe
     * 
     * @return
     */
    public ByteBuffer getOpcodeBuffer(String operatorKey) {
        OpcodeBag op = opcodeOffsets.get(operatorKey);
        return op == null ? null : opCodes.position(op.offset);
    }

    /**
     * Returns the opcode bag for the key, returns null if not found
     * 
     * @return
     */
    public OpcodeBag getOpcodeOffset(String operatorKey) {
        return opcodeOffsets.get(operatorKey);
    }

    /**
     * Returns the buffer holding input vec4 at position 0.
     * 
     * @return
     */
    public ByteBuffer getVec4DataBuffer() {
        return vec4Inputs.position(0);
    }

    /**
     * Returns an array with 4 floats for the input data index.
     * 
     * @param inputIndex
     * @return
     */
    public float[] getVec4(short inputIndex) {
        if (IndexParameter.isOutput(inputIndex)) {
            return null;
        }
        float[] result = new float[4];
        vec4Inputs.position(inputIndex * FloatInputData.VEC4_SIZE_IN_BYTES);
        vec4Inputs.asFloatBuffer().get(result);
        return result;
    }

    /**
     * Returns the buffer holding float input data at position 0.
     * 
     * @return
     */
    public ByteBuffer getFloatDataBuffer() {
        return floatInputs.position(0);
    }

    /**
     * Returns true if the capacity matches input count for all buffers, call this after all opcode and data
     * has been set to verify.
     * 
     * @return
     */
    public boolean validate() {
        return ((opCodeCount * OpcodeData.OPCODE_OPSIZE + indexInputCount * OpcodeData.OPCODE_INDEXSIZE
                + OpcodeData.OPCODE_HEADER_SIZE) == opCodes.capacity())
                & (floatInputCount * Float.BYTES == floatInputs.capacity())
                & (vec4InputCount * 4 * Float.BYTES == vec4Inputs.capacity());
    }

    /**
     * 
     */
    public void debugOpcodeData() {
        StringBuffer data = new StringBuffer();
        ByteBuffer opBuffer = getOpcodeBuffer();
        data.append("HEADER: " + new String(new char[] { (char) opBuffer.get(), (char) opBuffer.get() }) + "\n");
        int opCount = (opBuffer.get() & 0x0ff);
        data.append("OPCODECOUNT: " + opCount + "\n");
        opBuffer.get();
        for (int loop = 0; loop < opCount; loop++) {
            byte opCode = opBuffer.get();
            byte param = opBuffer.get();
            data.append("[" + OperatorName.get(opCode) + " : " + param + " -> " + opBuffer.get() + "]" + "\n");
            byte inputCount = opBuffer.get();
            ShortBuffer indexBuf = opBuffer.asShortBuffer();
            data.append(inputCount + " inputs:\n");
            for (int i = 0; i < inputCount; i++) {
                Short slot = indexBuf.get();
                String type = slot < 0 ? "output" : "input";
                data.append("InputData " + i + " : " + type + " " + (slot & 0x7fff) + "\n");
            }
            opBuffer.position(opBuffer.position() + inputCount * Short.BYTES);
        }
        FloatBuffer vec4 = getVec4DataBuffer().asFloatBuffer();
        if (vec4.capacity() > 0) {
            data.append("Vec4 input data:\n" + Buffers.toString(vec4, 0, vec4.capacity(), 4));
        } else {
            data.append("No Vec4 input data\n");
        }
        FloatBuffer floats = getFloatDataBuffer().asFloatBuffer();
        if (floats.capacity() > 0) {
            data.append("Float input data:\n" + Buffers.toString(floats, 0, floats.capacity(), 1));
        } else {
            data.append("No float input data\n");
        }
        Logger.d(getClass(), "\n" + data.toString());
    }

}
