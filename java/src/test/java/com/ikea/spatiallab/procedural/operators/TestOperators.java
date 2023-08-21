/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ikea.digitallabs.dela.FileUtils.FilesystemProperties;
import com.ikea.spatiallab.procedural.Loader;
import com.ikea.spatiallab.procedural.operators.IndexParameter.IndexType;
import com.ikea.spatiallab.procedural.operators.OpcodeData.OpcodeBlob;
import com.ikea.spatiallab.procedural.operators.Operator.OperatorName;
import com.ikea.spatiallab.procedural.operators.OperatorRecipe.OperatorData;
import com.ikea.spatiallab.procedural.operators.ProceduralImage.Target;

/**
 *
 */
public class TestOperators {

    @BeforeAll
    public static void init() {
        System.setProperty(FilesystemProperties.JAVA_TARGET_DIRECTORY.getKey(), "target/test-classes");
        System.setProperty(FilesystemProperties.SOURCE_DIRECTORY.getKey(), "src/test");
    }

    @Test
    public void testBlend() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_blend.oll");
        checkList(pi, Target.RGB, OperatorName.blend);
        checkList(pi, Target.ROUGHNESS, OperatorName.blend);
        checkList(pi, Target.NORMAL, OperatorName.blend);
        OperatorRecipe n1 = pi.getOutputOperatorQueue(Target.RGB);
        assertOperator(n1.getOpCodeData(), OperatorName.blend, new float[] { 0.1f, 0.2f, 0.3f, 1 },
                new float[] { 0.4f, 0.5f, 0.6f, 1 });

        pi.resetIndexes();
        OperatorRecipe testOperators = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF + "n4"));
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
        assertOutputs(opCodes);
        opCodes.getOpcodeBuffer(); // Reset position
        assertOperator(opCodes, OperatorName.blend, new float[] { 0.1f, 0.2f, 0.3f, 1 },
                new float[] { 0.4f, 0.5f, 0.6f, 1 });
    }

    @Test
    public void testUniformColor() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_uniform_color.oll");
        checkList(pi, Target.RGB, OperatorName.uniform_color);
        checkList(pi, Target.ROUGHNESS, OperatorName.uniform_color);
        checkList(pi, Target.NORMAL, OperatorName.uniform_color);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testLevels() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_levels.oll");
        checkList(pi, Target.RGB, OperatorName.levels);
        checkList(pi, Target.ROUGHNESS, OperatorName.levels);
        checkList(pi, Target.NORMAL, OperatorName.levels);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testNormalMap() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_normalmap.oll");
        checkList(pi, Target.RGB, OperatorName.normal_map);
        checkList(pi, Target.ROUGHNESS, OperatorName.normal_map);
        checkList(pi, Target.NORMAL, OperatorName.normal_map);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testCurve() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_curve.oll");
        checkList(pi, Target.RGB, OperatorName.curve);
        checkList(pi, Target.ROUGHNESS, OperatorName.curve);
        checkList(pi, Target.NORMAL, OperatorName.curve);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        assertOperator(opCodes, OperatorName.curve);

    }

    @Test
    public void testPosition() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_position.oll");
        checkList(pi, Target.RGB, OperatorName.position);
        checkList(pi, Target.ROUGHNESS, OperatorName.position);
        checkList(pi, Target.NORMAL, OperatorName.position);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();

        testOperators = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF + "positiontrs"));
        opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
        assertNotNull(testOperators.getList().get(0).getInputData("offset"));
        assertNotNull(testOperators.getList().get(0).getInputData("rotate"));
        assertNotNull(testOperators.getList().get(0).getInputData("scale"));

    }

    @Test
    public void testL2Norm() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_l2_norm.oll");
        checkList(pi, Target.RGB, OperatorName.l2_norm);
        checkList(pi, Target.ROUGHNESS, OperatorName.l2_norm);
        checkList(pi, Target.NORMAL, OperatorName.l2_norm);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testSawtoothWave() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_sawtooth_wave.oll");
        checkList(pi, Target.RGB, OperatorName.sawtooth_wave);
        checkList(pi, Target.ROUGHNESS, OperatorName.sawtooth_wave);
        checkList(pi, Target.NORMAL, OperatorName.sawtooth_wave);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testMultiply() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_multiply.oll");
        checkList(pi, Target.RGB, OperatorName.multiply);
        checkList(pi, Target.ROUGHNESS, OperatorName.multiply);
        checkList(pi, Target.NORMAL, OperatorName.multiply);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testGrayscaleConversion() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_grayscale_conversion.oll");
        checkList(pi, Target.RGB, OperatorName.grayscale_conversion);
        checkList(pi, Target.ROUGHNESS, OperatorName.grayscale_conversion);
        checkList(pi, Target.NORMAL, OperatorName.grayscale_conversion);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testNoise() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_noise.oll");
        checkList(pi, Target.RGB, OperatorName.noise);
        checkList(pi, Target.ROUGHNESS, OperatorName.noise);
        checkList(pi, Target.NORMAL, OperatorName.noise);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testAdd() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_add.oll");
        checkList(pi, Target.RGB, OperatorName.add);
        checkList(pi, Target.ROUGHNESS, OperatorName.add);
        checkList(pi, Target.NORMAL, OperatorName.add);
        pi.resetIndexes();
        OperatorRecipe testOperators = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF + "n1"));
        OpcodeData opCodes = testOperators.getOpCodeData();
        assertOperator(opCodes, OperatorName.add, new float[] { 0.1f, 0.2f, 0.3f, 0 }, new float[] { 0.5f, 0.55f, 0.6f,
                0 });
        pi.resetIndexes();
        testOperators = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF + "n2"));
        opCodes = testOperators.getOpCodeData();
        assertOperator(opCodes, OperatorName.add, new float[] { 0.11f, 0.12f, 0.13f, 0.14f }, new float[] { 0.6f, 0.61f,
                0.62f, 0.63f });

    }

    @Test
    public void testDivide() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_divide.oll");
        checkList(pi, Target.RGB, OperatorName.divide);
        checkList(pi, Target.ROUGHNESS, OperatorName.divide);
        checkList(pi, Target.NORMAL, OperatorName.divide);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        assertOperator(opCodes, OperatorName.divide, new float[] { 1, 1, 1, 0 }, new float[] { 4, 4, 4, 0 });
    }

    @Test
    public void testSubtract() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_subtract.oll");
        checkList(pi, Target.RGB, OperatorName.subtract);
        checkList(pi, Target.ROUGHNESS, OperatorName.subtract);
        checkList(pi, Target.NORMAL, OperatorName.subtract);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        assertOperator(opCodes, OperatorName.subtract, new float[] { 0.5f, 0.55f, 0.6f, 0 }, new float[] { 0.1f, 0.2f,
                0.3f, 0 });
    }

    @Test
    public void testCombine() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_combine.oll");
        checkList(pi, Target.RGB, OperatorName.combine);
        checkList(pi, Target.ROUGHNESS, OperatorName.combine);
        checkList(pi, Target.NORMAL, OperatorName.combine);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testInputVec4() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_inputs.oll");
        OperatorRecipe testOperators = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF + "add"));
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
        // Reset opcodes position to header
        opCodes.getOpcodeBuffer().position(OpcodeData.OPCODE_HEADER_SIZE);
        // First operator is position
        assertOperator(opCodes, OperatorName.position);
        // Second is add
        OpcodeBlob blob = assertOperator(opCodes, OperatorName.add, new float[] { 0.1f, 0.2f, 0.3f, 0.0f });
        assertTrue(IndexParameter.isOutput(blob.inputIndexes[1]));
    }

    @Test
    public void testInputWithTRS() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_inputs.oll");
        OperatorRecipe testOperators = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF
                + "inputwithtrs_voronoi"));
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
        // Reset opcodes position to header
        opCodes.getOpcodeBuffer().position(OpcodeData.OPCODE_HEADER_SIZE);
        // First operator is position
        assertOperator(opCodes, OperatorName.position);
        // second operator is voronoi with offsets
        assertOperator(opCodes, 2, OperatorName.voronoi, new float[] { 0.4f, 0.5f, 0.6f, 0 }, new float[] { 0.1f, 0.2f,
                0.3f, 0 }, new float[] { 0.7f, 0.8f, 0.9f, 0 });
    }

    private float[] getVec4(OpcodeData opCodes, OpcodeBlob blob, int inputIndex) {
        return opCodes.getVec4(blob.inputIndexes[inputIndex]);
    }

    @Test
    public void testInputSame() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_inputs.oll");
        OperatorRecipe testOperators = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF + "references"));
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
        assertTrue(opCodes.getOpcodeCount() == 5);
        // Reset opcodes position to header
        opCodes.getOpcodeBuffer().position(OpcodeData.OPCODE_HEADER_SIZE);
        // Assert that output is produced before it is consumed
        assertOutputs(opCodes);
    }

    @Test
    public void testInputCyclic() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_inputs.oll");
        try {
            OperatorRecipe testOperators = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF + "cyclic"));
            assertFalse(true);
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testCeramicRecipe() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "ceramic.oll");
        checkList(pi, Target.RGB, null);
        // checkList(pi, Target.ROUGHNESS, null);
        // checkList(pi, Target.NORMAL, null);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testConcreteRecipe() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "concrete_raw_grey.oll");
        checkList(pi, Target.RGB, null);
        // checkList(pi, Target.ROUGHNESS, null);
        // checkList(pi, Target.NORMAL, null);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testLeatherRecipe() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "natural_half_croupon_leather.oll");
        checkList(pi, Target.RGB, null);
        OperatorRecipe testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    private void checkList(ProceduralImage list, Target target, OperatorName opName) {
        OperatorRecipe resolver = list.getOutputOperatorQueue(target);
        int count = resolver.getOperatorCount();
        for (int i = 0; i < count; i++) {
            OperatorData opData = resolver.getOperatorData(i);
            IndexParameter destination = opData.getDestination();
            assertTrue(destination.type == IndexType.INDEX_OUTPUT_VEC4 || destination.type
                    == IndexType.INDEX_OUTPUT_FLOAT);
            if (opName != null) {
                assertTrue(opData.getOperator().getOperator() == opName);
            }
        }
    }

    private OpcodeBlob assertOperator(OpcodeData opCodes, OperatorName operator,
            float[]... inputs) {
        return assertOperator(opCodes, 0, operator, inputs);
    }

    private OpcodeBlob assertOperator(OpcodeData opCodes, int indexOffset, OperatorName operator,
            float[]... inputs) {
        OpcodeBlob blob = opCodes.getBlob();
        assertTrue(blob.opcode == operator.opCode);
        switch (operator) {
            case position:
                assertTrue(blob.inputCount == 0);
                break;
            case add:
            case divide:
            case subtract:
                assertTrue(blob.inputCount == 2);
                break;
            case blend:
                assertTrue(blob.inputCount == 3);
                break;
            case curve:
                assertInputWithTRS(blob.inputCount, (byte) 5);
                break;
            case voronoi:
                assertInputWithTRS(blob.inputCount, (byte) 2);
                break;
            default:
                throw new IllegalArgumentException("Not implemented");
        }
        if (inputs != null && inputs.length > 0) {
            for (int i = 0; i < inputs.length; i++) {
                assertArrayEquals(inputs[i], getVec4(opCodes, blob, indexOffset + i));
            }
        }
        return blob;
    }

    private void assertInputWithTRS(byte inputCount, byte expected) {
        if (inputCount > expected) {
            assertTrue(inputCount == expected + 3);
        } else {
            assertTrue(inputCount == expected);
        }
    }

    private void assertOutputs(OpcodeData opCodes) {
        OpcodeBlob blob = null;
        HashSet<Short> outputs = new HashSet<Short>();
        while ((blob = opCodes.getBlob()) != null) {
            for (int i = 0; i < blob.inputCount; i++) {
                short input = blob.inputIndexes[i];
                if (IndexParameter.isOutput(input)) {
                    assertTrue(outputs.contains(input));
                }
            }
            outputs.add((short) (blob.outputIndex | 0x08000));
        }
    }

}
