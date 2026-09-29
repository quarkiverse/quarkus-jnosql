package io.quarkiverse.jnosql.arangodb.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.nosql.Template;

import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;
import org.eclipse.jnosql.databases.arangodb.communication.ArangoDBBucketManager;
import org.eclipse.jnosql.databases.arangodb.communication.ArangoDBBucketManagerFactory;
import org.eclipse.jnosql.databases.arangodb.communication.ArangoDBDocumentManager;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ArangodbTestResource.class)
class ArangoDBManagerTest {

    @Inject
    @Database(DatabaseType.DOCUMENT)
    Instance<ArangoDBDocumentManager> documentManagers;

    @Inject
    Instance<ArangoDBBucketManager> bucketManagers;

    @Inject
    ArangoDBBucketManagerFactory bucketManagerFactory;

    @Inject
    BucketManagerFactory genericBucketManagerFactory;

    @Inject
    @Database(DatabaseType.DOCUMENT)
    Template documentTemplate;

    @Inject
    @Database(DatabaseType.KEY_VALUE)
    Template keyValueTemplate;

    @Test
    void shouldShareManagedInstances() {
        assertSame(documentManagers.get(), documentManagers.get());
        assertSame(bucketManagers.get(), bucketManagers.get());
        assertSame(bucketManagerFactory, genericBucketManagerFactory);
    }

    @ParameterizedTest
    @EnumSource(value = DatabaseType.class, names = { "DOCUMENT", "KEY_VALUE" })
    void shouldRoundTripAndDeleteEntities(DatabaseType type) {
        Template template = type == DatabaseType.DOCUMENT ? documentTemplate : keyValueTemplate;
        PersonRecord person = new PersonRecord(UUID.randomUUID().toString(), "Ada", List.of("123", "456"));
        PersonRecord inserted = template.insert(person);

        try {
            assertEquals(person.name(), inserted.name());
            assertEquals(person.phones(), inserted.phones());
            assertEquals(Optional.of(inserted), template.find(PersonRecord.class, inserted.id()));
        } finally {
            template.delete(PersonRecord.class, inserted.id());
        }
        assertTrue(template.find(PersonRecord.class, inserted.id()).isEmpty());
    }

    @Test
    void shouldUseDriverSpecificBucketFactory() {
        String key = UUID.randomUUID().toString();
        try (ArangoDBBucketManager manager = bucketManagerFactory.getBucketManager("test", "custom")) {
            manager.put(key, "value");
            assertEquals("value", manager.get(key).orElseThrow().get(String.class));
            manager.delete(key);
            assertTrue(manager.get(key).isEmpty());
        }
    }
}
