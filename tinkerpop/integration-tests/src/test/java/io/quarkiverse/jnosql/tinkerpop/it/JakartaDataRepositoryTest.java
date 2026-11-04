package io.quarkiverse.jnosql.tinkerpop.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import jakarta.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Jakarta Data repository backed by the application-owned Graph. Vertices are created through the Graph because the
 * arangodb-tinkerpop-provider rejects the reserved "_id" property that the JNoSQL driver writes on insert and
 * matches on in id-based lookups, so id-based operations cannot be exercised with this provider.
 */
@QuarkusTest
@QuarkusTestResource(ArangoDBTestResource.class)
class JakartaDataRepositoryTest {

    @Inject
    Graph graph;

    @Inject
    PersonDataRepository repository;

    private String createPerson(String name) {
        return graph.addVertex(T.label, "Person", "name", name).id().toString();
    }

    @Test
    void shouldFindByDerivedQuery() {
        String name = "person-" + UUID.randomUUID();
        String id = createPerson(name);

        List<Person> found = repository.findByName(name);

        assertEquals(1, found.size());
        assertEquals(id, found.get(0).getId());
        assertEquals(name, found.get(0).getName());
        assertTrue(repository.findByName("missing-" + UUID.randomUUID()).isEmpty());
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertTrue(repository.findById("Person/" + UUID.randomUUID()).isEmpty());
    }
}
