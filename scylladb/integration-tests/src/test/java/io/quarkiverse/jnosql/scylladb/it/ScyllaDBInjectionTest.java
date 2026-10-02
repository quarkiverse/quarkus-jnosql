package io.quarkiverse.jnosql.scylladb.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import jakarta.inject.Inject;

import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.eclipse.jnosql.databases.scylladb.communication.ScyllaDBColumnManager;
import org.eclipse.jnosql.databases.scylladb.communication.ScyllaDBColumnManagerFactory;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.junit.jupiter.api.Test;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ScyllaDBTestResource.class)
class ScyllaDBInjectionTest {

    @Inject
    ScyllaDBColumnManager unqualified;

    @Inject
    @Database(DatabaseType.COLUMN)
    ScyllaDBColumnManager qualified;

    @Inject
    ScyllaDBColumnManagerFactory factory;

    @Inject
    DatabaseConfiguration configuration;

    @Test
    void shouldShareTheManagedManagerAndFactory() {
        assertSame(unqualified, qualified);
        assertEquals("test", qualified.name());
        assertSame(factory, configuration.apply(new MicroProfileSettings()));
    }
}
