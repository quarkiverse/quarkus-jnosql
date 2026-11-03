package io.quarkiverse.jnosql.tinkerpop.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.jnosql.databases.tinkerpop.mapping.EdgeEntity;
import org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerpopTemplate;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ArangoDBTestResource.class)
class TinkerpopIntegrationTest {

    @Inject
    Graph graph;

    @Inject
    TinkerpopTemplate template;

    @Inject
    PersonRepository repository;

    private static String uniqueName() {
        return "person-" + UUID.randomUUID();
    }

    @Test
    void shouldInjectApplicationGraph() {
        assertNotNull(graph);
        assertTrue(graph.toString().toLowerCase().contains("arangodb"), "the application-provided graph is the one in use");
    }

    @Test
    void shouldInjectTemplate() {
        assertNotNull(template);
    }

    /**
     * The arangodb-tinkerpop-provider rejects the reserved "_id" property that the JNoSQL driver
     * writes on insert, so vertices are created through the application-owned Graph and read back
     * through the JNoSQL template.
     */
    private String createPerson(String name) {
        Vertex vertex = graph.addVertex(T.label, "Person", "name", name);
        return vertex.id().toString();
    }

    @Test
    void shouldFindEntityCreatedThroughGraph() {
        String name = uniqueName();
        String id = createPerson(name);

        Optional<Person> found = template.find(id);
        assertTrue(found.isPresent());
        assertEquals(name, found.get().getName());
    }

    @Test
    void shouldPersistRelationship() {
        String ada = createPerson(uniqueName());
        String grace = createPerson(uniqueName());
        Person adaEntity = template.<Person, String> find(ada).orElseThrow();
        Person graceEntity = template.<Person, String> find(grace).orElseThrow();

        EdgeEntity edge = template.edge(adaEntity, "knows", graceEntity);

        assertNotNull(edge.id());
        assertEquals("knows", edge.label());
        assertEquals(1, template.edges(adaEntity, Direction.OUT, "knows").size());
        assertEquals(1, template.edges(graceEntity, Direction.IN, "knows").size());
        assertEquals(1, graph.traversal().V(ada).out("knows").toList().size());
    }

    @Test
    void shouldTraverseGraph() {
        String ada = createPerson(uniqueName());
        String graceName = uniqueName();
        String grace = createPerson(graceName);
        graph.vertices(ada).next().addEdge("knows", graph.vertices(grace).next());

        List<Person> friends = template.traversalVertex(ada)
                .out("knows")
                .<Person> result()
                .toList();

        assertEquals(1, friends.size());
        assertEquals(graceName, friends.get(0).getName());
    }

    @Test
    void shouldUseRepository() {
        String name = uniqueName();
        String id = createPerson(name);

        assertEquals(1, repository.findByName(name).size());
    }

    @Test
    void shouldNotCloseApplicationGraph() {
        createPerson(uniqueName());
        assertNotNull(graph.traversal().V().limit(1).toList());
    }
}
