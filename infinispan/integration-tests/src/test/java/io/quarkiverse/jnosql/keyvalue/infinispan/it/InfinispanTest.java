package io.quarkiverse.jnosql.keyvalue.infinispan.it;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.nosql.Template;

import org.eclipse.jnosql.communication.keyvalue.BucketManager;
import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;
import org.eclipse.jnosql.communication.keyvalue.KeyValueEntity;
import org.infinispan.Cache;
import org.junit.jupiter.api.Test;

import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;
import io.quarkiverse.jnosql.infinispan.it.PersonRecord;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class InfinispanTest {

    @Inject
    BucketManager manager;

    @Inject
    BucketManagerFactory factory;

    @Inject
    KeyValueConfiguration configuration;

    @Inject
    Template template;

    @Test
    void shouldShareTheConfiguredCache() {
        assertSame(configuration.apply(new MicroProfileSettings()), factory);
        assertEquals("test", manager.name());
        Cache<String, String> cache = (Cache<String, String>) factory.getMap("test", String.class, String.class);
        assertEquals(60000, cache.getCacheConfiguration().expiration().lifespan());
        String key = UUID.randomUUID().toString();
        manager.put(key, "first");
        assertEquals("first", factory.apply("test").get(key).orElseThrow().get(String.class));
        manager.put(key, "updated");
        assertEquals("updated", manager.get(key).orElseThrow().get(String.class));
        manager.delete(key);
        assertTrue(manager.get(key).isEmpty());
    }

    @Test
    void shouldExpireEntries() throws InterruptedException {
        String key = UUID.randomUUID().toString();
        manager.put(KeyValueEntity.of(key, "expires"), Duration.ofMillis(100));
        long deadline = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        while (manager.get(key).isPresent() && System.nanoTime() < deadline) {
            Thread.sleep(20);
        }
        assertTrue(manager.get(key).isEmpty());
    }

    @Test
    void shouldMapAndDeleteRecords() {
        PersonRecord person = PersonRecord.randomPerson();
        template.insert(person);
        assertEquals(person, template.find(PersonRecord.class, person.id()).orElseThrow());
        PersonRecord updated = new PersonRecord(person.id(), "updated", person.phones());
        template.update(updated);
        assertEquals(updated, template.find(PersonRecord.class, person.id()).orElseThrow());
        template.delete(PersonRecord.class, person.id());
        assertTrue(template.find(PersonRecord.class, person.id()).isEmpty());
    }
}
