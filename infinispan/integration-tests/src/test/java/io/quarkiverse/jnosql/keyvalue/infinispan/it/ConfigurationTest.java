package io.quarkiverse.jnosql.keyvalue.infinispan.it;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.databases.infinispan.communication.InfinispanConfigurations;
import org.eclipse.jnosql.databases.infinispan.communication.QuarkusInfinispanKeyValueConfiguration;
import org.infinispan.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigurationTest {

    @TempDir
    Path directory;

    @Test
    void shouldLoadAnExternalXmlFile() throws IOException {
        Path xml = directory.resolve("external.xml");
        Files.writeString(xml, """
                <infinispan xmlns="urn:infinispan:config:16.0">
                    <cache-container name="external">
                        <local-cache name="external-cache"/>
                    </cache-container>
                </infinispan>
                """);
        var settings = Settings.builder().put(InfinispanConfigurations.CONFIG, xml.toString()).build();
        var configuration = new QuarkusInfinispanKeyValueConfiguration();
        var factory = configuration.apply(settings);
        Cache<String, String> cache = (Cache<String, String>) factory.getMap("external-cache", String.class, String.class);
        try {
            assertSame(factory, configuration.apply(settings));
            var manager = factory.apply("external-cache");
            manager.put("key", "value");
            assertEquals("value", manager.get("key").orElseThrow().get(String.class));
        } finally {
            // JNoSQL 1.1.19's factory.close() does not stop the embedded container.
            cache.getCacheManager().stop();
        }
    }

    @Test
    void shouldRejectAMissingXmlFile() {
        var configuration = new QuarkusInfinispanKeyValueConfiguration();
        var settings = Settings.builder()
                .put(InfinispanConfigurations.CONFIG, directory.resolve("missing.xml").toString())
                .build();
        assertThrows(RuntimeException.class, () -> configuration.apply(settings));
    }

    @Test
    void shouldRejectInvalidXml() throws IOException {
        Path xml = directory.resolve("invalid.xml");
        Files.writeString(xml, "<not-infinispan/>");
        var configuration = new QuarkusInfinispanKeyValueConfiguration();
        var settings = Settings.builder().put(InfinispanConfigurations.CONFIG, xml.toString()).build();
        assertThrows(RuntimeException.class, () -> configuration.apply(settings));
    }
}
