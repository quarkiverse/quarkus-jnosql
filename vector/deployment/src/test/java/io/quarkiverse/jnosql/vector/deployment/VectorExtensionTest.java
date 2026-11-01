package io.quarkiverse.jnosql.vector.deployment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.inject.Inject;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.vector.VectorManager;
import org.eclipse.jnosql.mapping.vector.VectorTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class VectorExtensionTest {

    static final AtomicInteger MANAGERS_CREATED = counter("MANAGERS_CREATED");
    static final AtomicInteger MANAGERS_CLOSED = counter("MANAGERS_CLOSED");
    static final AtomicInteger FACTORIES_CLOSED = counter("FACTORIES_CLOSED");
    static volatile String databaseName;

    // The application runs in its own classloader, so counters are shared through System properties.
    static AtomicInteger counter(String name) {
        return (AtomicInteger) System.getProperties().computeIfAbsent("vector.test." + name,
                k -> new AtomicInteger());
    }

    @RegisterExtension
    static final QuarkusUnitTest CONFIG = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar.addClasses(FakeConfiguration.class, EntitiesMetadataStub.class))
            .overrideConfigKey("jnosql.vector.database", "articles")
            .overrideConfigKey("jnosql.vector.provider", FakeConfiguration.class.getName())
            .setAfterUndeployListener(() -> {
                assertEquals(1, MANAGERS_CLOSED.get(), "the VectorManager must be closed on shutdown");
                assertEquals(1, FACTORIES_CLOSED.get(), "the factory must be closed on shutdown");
            });

    @Inject
    @Database(DatabaseType.VECTOR)
    VectorTemplate qualifiedTemplate;

    @Inject
    VectorTemplate defaultTemplate;

    @Inject
    @Database(DatabaseType.VECTOR)
    VectorManager manager;

    @Test
    void shouldInjectVectorBeansWithoutAmbiguity() {
        assertNotNull(qualifiedTemplate);
        assertNotNull(defaultTemplate);
        assertNotNull(manager);
    }

    @Test
    void shouldUseTheConfiguredProviderAndDatabase() {
        assertTrue(manager.toString().length() > 0);
        assertEquals("articles", databaseName);
        assertEquals(1, MANAGERS_CREATED.get(), "the manager is a single application scoped instance");
    }

    @jakarta.inject.Singleton
    public static class EntitiesMetadataStub {

        @jakarta.enterprise.inject.Produces
        org.eclipse.jnosql.mapping.metadata.EntitiesMetadata entities() {
            return (org.eclipse.jnosql.mapping.metadata.EntitiesMetadata) Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class<?>[] { org.eclipse.jnosql.mapping.metadata.EntitiesMetadata.class },
                    (proxy, method, args) -> null);
        }
    }

    public static class FakeConfiguration implements DatabaseConfiguration {

        @Override
        public DatabaseManagerFactory apply(Settings settings) {
            return new DatabaseManagerFactory() {
                @Override
                public DatabaseManager apply(String database) {
                    databaseName = database;
                    MANAGERS_CREATED.incrementAndGet();
                    return (VectorManager) Proxy.newProxyInstance(getClass().getClassLoader(),
                            new Class<?>[] { VectorManager.class },
                            (proxy, method, args) -> {
                                switch (method.getName()) {
                                    case "close" -> MANAGERS_CLOSED.incrementAndGet();
                                    case "toString" -> {
                                        return "fake-vector-manager";
                                    }
                                    case "hashCode" -> {
                                        return System.identityHashCode(proxy);
                                    }
                                    case "equals" -> {
                                        return proxy == args[0];
                                    }
                                    default -> {
                                    }
                                }
                                return null;
                            });
                }

                @Override
                public void close() {
                    FACTORIES_CLOSED.incrementAndGet();
                }
            };
        }
    }
}
