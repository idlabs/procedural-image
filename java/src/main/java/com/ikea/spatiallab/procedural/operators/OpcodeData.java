/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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

    public static final int OPCODE_HEADER_SIZE = 4;
    public static final String OPCODE_MAGIC = "PI";

    private ByteBuffer opCodes;
    private ByteBuffer floatInputs;
    private ByteBuffer vec4Inputs;
    private int floatInputCount = 0;
    private int indexInputCount = 0;
    private int vec4InputCount = 0;
    private int opCodeCount = 0;

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
    protected void addOpcode(byte opCode) {
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
    protected void addOpcode(byte opCode, byte param) {
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
     * Adds an opcode and parameter
     * 
     * @param opCode
     * @param param
     * @param outputIndex
     */
    protected void addOpcode(byte opCode, byte param, IndexParameter outputIndex, byte inputCount) {
        if (!outputIndex.isOutput()) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_VALUE.message + "index parameter is not output");
        }
        opCodes.put(opCode);
        opCodes.put(param);
        opCodes.put((byte) outputIndex.index);
        opCodes.put(inputCount);
        opCodeCount++;
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
     * Returns the number of index inputs
     * 
     * @return
     */
    protected int getIndexInputCount() {
        return indexInputCount;
    }

    /**
     * Returns the position in the opcode buffer in bytes
     * 
     * @return
     */
    protected int getOpcodeBufferPosition() {
        return opCodes.position();
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
     * Returns the opcode buffer at position .
     * 
     * @return
     */
    protected ByteBuffer getOpcodeBuffer(int position) {
        return opCodes.position(position);
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
        return (floatInputCount * Float.BYTES == floatInputs.capacity())
                & (vec4InputCount * 4 * Float.BYTES == vec4Inputs.capacity());
    }

    /**
     * 
     */
    public void debugOpcodeData() {
        StringBuffer data = new StringBuffer();
        ByteBuffer opBuffer = getOpcodeBuffer();
        data.append("HEADER: " + new String(new char[] { (char) opBuffer.get(), (char) opBuffer.get() }) + "\n");
        int opCount = opBuffer.get();
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
