package com.buschmais.jqassistant.plugin.yaml.impl.scanner;

import java.io.File;
import java.util.List;

import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.yaml.api.model.YAMLKeyDescriptor;
import com.buschmais.jqassistant.plugin.yaml.api.model.YAMLValueDescriptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class YAMLFileScannerPluginComplexQueriesIT extends AbstractPluginIT {
    @BeforeEach
    void startTransaction() {
        store.beginTransaction();
    }

    @AfterEach
    void commitTransaction() {
        store.commitTransaction();
    }

    @Test
    void queryValidDropWizardConfigYAMLForNumberOfKeysInFile() {
        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/dropwizard-configuration.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<Long> results = query("MATCH (k:YAML:Key) return count(k) as k").getColumn("k");

        Long count = results.get(0);

        assertThat(count, equalTo(13L));
    }

    @Test
    void queryValidDropWizardConfigYAMLForKeyMaxThreadsByName() {
        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/dropwizard-configuration.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLKeyDescriptor> results = query("MATCH (k:YAML:Key) where k.name = 'maxThreads' return k").getColumn("k");

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getName(), equalTo("maxThreads"));
        assertThat(results.get(0).getValues(), hasSize(1));
        assertThat(results.get(0).getValues().get(0).getValue(), equalTo("1024"));
    }

    @Test
    void queryValidDropWizardConfigYAMLForKeyMaxThreadsByFQN() {
        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/dropwizard-configuration.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLKeyDescriptor> results = query("MATCH (k:YAML:Key) where k.fqn = 'server.maxThreads' return k")
             .getColumn("k");

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getName(), equalTo("maxThreads"));
        assertThat(results.get(0).getValues(), hasSize(1));
        assertThat(results.get(0).getValues().get(0).getValue(), equalTo("1024"));
    }

    @Test
    void queryValidDropWizardConfigYAMLForKeyMaxThreadsByParentChildRelation() {
        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/dropwizard-configuration.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLKeyDescriptor> keyDescriptors =
             query("MATCH (f:YAML:File)-[:CONTAINS_DOCUMENT]->(d:YAML:Document)" +
                   "-[:CONTAINS_KEY]->(server:YAML:Key)-[:CONTAINS_KEY]->(maxThreads:YAML:Key) " +
                   "WHERE f.fileName=~'.*/dropwizard-configuration.yaml' AND " +
                   "maxThreads.name = 'maxThreads' RETURN maxThreads AS m")
                  .getColumn("m");

        assertThat(keyDescriptors, hasSize(1));
    }


    @Test
    void queryValidDropWizardConfigYAMLForTresholdByFullPath() {
        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/dropwizard-configuration.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLKeyDescriptor> keyDescriptors =
             query("MATCH (f:YAML:File)-[:CONTAINS_DOCUMENT]->(d:YAML:Document) " +
                        "-[:CONTAINS_KEY]->(server:Key) " +
                        "-[:CONTAINS_KEY]->(requestLog:Key)" +
                        "-[:CONTAINS_KEY]->(appenders:Key)" +
                        "-[:CONTAINS_KEY]->(threshold:Key)" +
                        "WHERE f.fileName=~'.*/dropwizard-configuration.yaml' AND " +
                        "threshold.name = 'threshold' " +
                        "RETURN threshold AS t")
                  .getColumn("t");

        assertThat(keyDescriptors, hasSize(1));
        assertThat(keyDescriptors.get(0).getName(), equalTo("threshold"));
        assertThat(keyDescriptors.get(0).getValues(), hasSize(1));
        assertThat(keyDescriptors.get(0).getValues().get(0).getValue(), equalTo("OFF"));
    }

    @Test
    void queryValidDropWizardConfigYAMLForValueOFFViaPath() {
        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/dropwizard-configuration.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<?> valueDescriptors =
        query("MATCH (f:YAML:File)-[:CONTAINS_DOCUMENT]->(d:YAML:Document) " +
                   "-[:CONTAINS_KEY]->(server:Key) " +
                   "-[:CONTAINS_KEY]->(requestLog:Key)" +
                   "-[:CONTAINS_KEY]->(appenders:Key)" +
                   "-[:CONTAINS_KEY]->(treshold:Key)" +
                   "-[:CONTAINS_VALUE]->(v:Value)" +
                   "WHERE f.fileName=~'.*/dropwizard-configuration.yaml' AND " +
                   "v.value = 'OFF' RETURN v AS v")
             .getColumn("v");

        assertThat(valueDescriptors, hasSize(1));
    }

    @Test
    void queryValidDropWizardConfigYAMLForValueOFF() {
        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/dropwizard-configuration.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLValueDescriptor> valueDescriptors =
             query("MATCH (f:YAML:File)-[:CONTAINS_DOCUMENT]->(d:YAML:Document) " +
                        "-[*]-> " +
                        "(v:Value) " +
                        "WHERE f.fileName=~'.*/dropwizard-configuration.yaml' AND " +
                        "v.value = 'OFF' RETURN v AS v")
                  .getColumn("v");

        assertThat(valueDescriptors, hasSize(1));
        assertThat(valueDescriptors.get(0).getValue(), equalTo("OFF"));
    }

}
