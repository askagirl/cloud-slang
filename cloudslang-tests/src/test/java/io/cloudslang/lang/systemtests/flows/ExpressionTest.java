/**
 * ****************************************************************************
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 * <p/>
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * *****************************************************************************
 */
package io.cloudslang.lang.systemtests.flows;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.cloudslang.lang.compiler.SlangSource;
import io.cloudslang.lang.entities.CompilationArtifact;
import io.cloudslang.lang.entities.ScoreLangConstants;
import io.cloudslang.lang.systemtests.StepData;
import io.cloudslang.lang.systemtests.SystemsTestsParent;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Bonczidai Levente
 * @since 11/6/2015
 */
public class ExpressionTest extends SystemsTestsParent {

    @Test
    public void testValuesInFlow() throws Exception {
        // compile
        URI resource = getClass().getResource("/yaml/formats/values_flow.sl").toURI();
        URI operation = getClass().getResource("/yaml/noop.sl").toURI();

        SlangSource dep = SlangSource.fromFile(operation);
        Set<SlangSource> path = Sets.newHashSet(dep);
        CompilationArtifact compilationArtifact = slang.compile(SlangSource.fromFile(resource), path);

        // trigger
        Map<String, Serializable> userInputs = new HashMap<>();
        userInputs.put("input_no_expression", "hello world");
        userInputs.put("input_overridable", "i_should_not_be_assigned");
        Map<String, Serializable> systemProperties = new HashMap<>();
        systemProperties.put("user.sys.props.host", "localhost");

        Map<String, StepData> steps = triggerWithData(compilationArtifact, userInputs, systemProperties).getTasks();

        // verify
        StepData flowData = steps.get(EXEC_START_PATH);
        StepData taskData = steps.get(FIRST_STEP_PATH);

        verifyFlowInputs(flowData);
        verifyTaskInputs(taskData);
        Assert.assertEquals(ScoreLangConstants.SUCCESS_RESULT, flowData.getResult());
    }

    private void verifyFlowInputs(StepData flowData) {
        Map<String, Serializable> expectedInputs = new HashMap<>();

        // properties
        expectedInputs.put("input_no_expression", "hello world");
        expectedInputs.put("input_no_expression_not_required", null);
        expectedInputs.put("input_system_property", "localhost");
        expectedInputs.put("input_not_overridable", 25);

        // loaded by Yaml
        expectedInputs.put("input_int", 22);
        expectedInputs.put("input_str_no_quotes", "Hi");
        expectedInputs.put("input_str_single", "Hi");
        expectedInputs.put("input_str_double", "Hi");
        expectedInputs.put("input_yaml_list", Lists.newArrayList(1, 2, 3));
        expectedInputs.put("input_properties_yaml_map_folded", "medium");

        // evaluated via Python
        expectedInputs.put("input_python_null", null);
        expectedInputs.put("input_python_list",  Lists.newArrayList(1, 2, 3));
        HashMap<String, Serializable> expectedInputPythonMap = new HashMap<>();
        expectedInputPythonMap.put("key1", "value1");
        expectedInputPythonMap.put("key2", "value2");
        expectedInputPythonMap.put("key3", "value3");
        expectedInputs.put("input_python_map", expectedInputPythonMap);
        HashMap<String, Serializable> expectedInputPythonMapQuotes = new HashMap<>();
        expectedInputPythonMapQuotes.put("value", 2);
        expectedInputs.put("input_python_map_quotes", expectedInputPythonMapQuotes);
        expectedInputs.put("b", "b");
        expectedInputs.put("input_concat_1", "ab");
        expectedInputs.put("input_concat_2_one_liner", "prefix_ab_suffix");
        expectedInputs.put("input_concat_2_folded", "prefix_ab_suffix");

        Assert.assertEquals("Flow inputs not bound correctly", expectedInputs, flowData.getInputs());
    }

    private void verifyTaskInputs(StepData taskData) {
        Map<String, Serializable> expectedTaskArguments = new HashMap<>();

        // loaded by Yaml
        expectedTaskArguments.put("input_int", 22);
        expectedTaskArguments.put("input_str_no_quotes", "Hi");
        expectedTaskArguments.put("input_str_single", "Hi");
        expectedTaskArguments.put("input_str_double", "Hi");
        expectedTaskArguments.put("input_yaml_list", Lists.newArrayList(1, 2, 3));
        HashMap<String, Serializable> expectedYamlMapFolded = new HashMap<>();
        expectedYamlMapFolded.put("key1", "medium");
        expectedYamlMapFolded.put("key2", false);
        expectedTaskArguments.put("input_yaml_map_folded", expectedYamlMapFolded);

        // evaluated via Python
        expectedTaskArguments.put("input_python_null", null);
        expectedTaskArguments.put("input_python_list", Lists.newArrayList(1, 2, 3));
        HashMap<String, Serializable> expectedInputPythonMap = new HashMap<>();
        expectedInputPythonMap.put("key1", "value1");
        expectedInputPythonMap.put("key2", "value2");
        expectedInputPythonMap.put("key3", "value3");
        expectedTaskArguments.put("input_python_map", expectedInputPythonMap);
        HashMap<String, Serializable> expectedInputPythonMapQuotes = new HashMap<>();
        expectedInputPythonMapQuotes.put("value", 2);
        expectedTaskArguments.put("input_python_map_quotes", expectedInputPythonMapQuotes);
        expectedTaskArguments.put("b", "b");
        expectedTaskArguments.put("input_concat_1", "ab");
        expectedTaskArguments.put("input_concat_2_one_liner", "prefix_ab_suffix");
        expectedTaskArguments.put("input_concat_2_folded", "prefix_ab_suffix");

        Assert.assertEquals("Task arguments not bound correctly", expectedTaskArguments, taskData.getInputs());
    }

}
