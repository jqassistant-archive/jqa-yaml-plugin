package com.buschmais.jqassistant.plugin.yaml.impl.scanner;

import java.io.IOException;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class YAMLFileScannerPluginTest {

    private YAMLFileScannerPlugin plugin = new YAMLFileScannerPlugin();

    @Test
    public void acceptsYamlAndYml() throws IOException {
        assertThat(plugin.accepts(mock(FileResource.class), "/test.yaml", DefaultScope.NONE), equalTo(true));
        assertThat(plugin.accepts(mock(FileResource.class), "/test.yml", DefaultScope.NONE), equalTo(true));
        assertThat(plugin.accepts(mock(FileResource.class), "/test.xml", DefaultScope.NONE), equalTo(false));
    }

}
