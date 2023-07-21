/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ikea.digitallabs.dela.FileUtils.FilesystemProperties;
import com.ikea.spatiallab.procedural.Loader;
import com.ikea.spatiallab.procedural.operators.IndexParameter.IndexType;
import com.ikea.spatiallab.procedural.operators.Operator.OperatorName;
import com.ikea.spatiallab.procedural.operators.OperatorImplementation.InputData;
import com.ikea.spatiallab.procedural.operators.OperatorListResolver.OperatorData;
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
        Reference ref = new Reference();
        ref.setOperatorRef("n4");
        pi.resetIndexes();
        OperatorListResolver testOperators = pi.getOperatorQueue(ref);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();

    }

    @Test
    public void testUniformColor() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_uniform_color.oll");
        checkList(pi, Target.RGB, OperatorName.uniform_color);
        checkList(pi, Target.ROUGHNESS, OperatorName.uniform_color);
        checkList(pi, Target.NORMAL, OperatorName.uniform_color);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testLevels() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_levels.oll");
        checkList(pi, Target.RGB, OperatorName.levels);
        checkList(pi, Target.ROUGHNESS, OperatorName.levels);
        checkList(pi, Target.NORMAL, OperatorName.levels);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testNormalMap() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_normalmap.oll");
        checkList(pi, Target.RGB, OperatorName.normal_map);
        checkList(pi, Target.ROUGHNESS, OperatorName.normal_map);
        checkList(pi, Target.NORMAL, OperatorName.normal_map);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testCurve() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_curve.oll");
        checkList(pi, Target.RGB, OperatorName.curve);
        checkList(pi, Target.ROUGHNESS, OperatorName.curve);
        checkList(pi, Target.NORMAL, OperatorName.curve);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        CurveOperator curve = (CurveOperator) pi.getOperator("n1");
        OpcodeData opCodes = testOperators.getOpCodeData();
        OperatorListResolver resolver = pi.getOperatorQueue(new Reference(Reference.OPERATOR_REF + "n1"));
        OperatorData opData = resolver.getOperatorByKey("n1");
        IndexParameter redIndex = opData.getInputIndex("red");
        InputData<?> data = opData.getInputData(redIndex);

        opCodes.debugOpcodeData();
    }

    @Test
    public void testPosition() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_position.oll");
        checkList(pi, Target.RGB, OperatorName.position);
        checkList(pi, Target.ROUGHNESS, OperatorName.position);
        checkList(pi, Target.NORMAL, OperatorName.position);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testMultiply() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_multiply.oll");
        checkList(pi, Target.RGB, OperatorName.multiply);
        checkList(pi, Target.ROUGHNESS, OperatorName.multiply);
        checkList(pi, Target.NORMAL, OperatorName.multiply);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testGrayscaleConversion() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_grayscale_conversion.oll");
        checkList(pi, Target.RGB, OperatorName.grayscale_conversion);
        checkList(pi, Target.ROUGHNESS, OperatorName.grayscale_conversion);
        checkList(pi, Target.NORMAL, OperatorName.grayscale_conversion);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testNoise() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "test_noise.oll");
        checkList(pi, Target.RGB, OperatorName.noise);
        checkList(pi, Target.ROUGHNESS, OperatorName.noise);
        checkList(pi, Target.NORMAL, OperatorName.noise);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    @Test
    public void testCeramicRecipe() throws FileNotFoundException, UnsupportedEncodingException {
        ProceduralImage pi = Loader.load("assets/testcases/", "ceramic.json.oll");
        checkList(pi, Target.RGB, null);
        // checkList(pi, Target.ROUGHNESS, null);
        // checkList(pi, Target.NORMAL, null);
        OperatorListResolver testOperators = pi.getOutputOperatorQueue(Target.RGB);
        OpcodeData opCodes = testOperators.getOpCodeData();
        opCodes.debugOpcodeData();
    }

    private void checkList(ProceduralImage list, Target target, OperatorName opName) {
        OperatorListResolver resolver = list.getOutputOperatorQueue(target);
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

}
