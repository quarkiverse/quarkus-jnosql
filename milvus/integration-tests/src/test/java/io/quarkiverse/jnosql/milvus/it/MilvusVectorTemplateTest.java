package io.quarkiverse.jnosql.milvus.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import jakarta.data.Limit;
import jakarta.inject.Inject;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.vector.DenseVector;
import org.eclipse.jnosql.mapping.vector.VectorManager;
import org.eclipse.jnosql.mapping.vector.VectorTemplate;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(MilvusTestResource.class)
class MilvusVectorTemplateTest {

    @Inject
    @Database(DatabaseType.VECTOR)
    VectorTemplate template;

    @Inject
    @Database(DatabaseType.VECTOR)
    VectorManager manager;

    @Test
    void shouldInjectBeans() {
        assertNotNull(template);
        assertNotNull(manager);
        assertEquals("articles", manager.name());
    }

    @Test
    void shouldMapPayloadAndFindById() {
        template.insert(new Article("a-10", "Hello", "news", DenseVector.of(0.5f, 0.5f, 0f, 0f)));

        Article found = template.find(Article.class, "a-10").orElseThrow();
        System.out.println(
                "DBG " + found.getId() + "|" + found.getTitle() + "|" + found.getCategory() + "|" + found.getEmbedding());
        assertEquals("Hello", found.getTitle());
        assertEquals("news", found.getCategory());
        assertEquals(4, found.getEmbedding().values().length);
    }

    @Test
    void shouldSearchBySimilarityWithPayloadFilter() {
        template.insert(new Article("s-1", "A", "x", DenseVector.of(0f, 0f, 1f, 0f)));
        template.insert(new Article("s-2", "B", "y", DenseVector.of(0f, 0f, 0.9f, 0.1f)));
        template.insert(new Article("s-3", "C", "x", DenseVector.of(0f, 0f, 0f, 1f)));

        List<Article> nearest = template.search(Article.class)
                .vector(DenseVector.of(0f, 0f, 1f, 0f))
                .limit(Limit.of(1))
                .result();
        assertEquals("s-1", nearest.get(0).getId());

        List<Article> filtered = template.search(Article.class)
                .vector(DenseVector.of(0f, 0f, 1f, 0f))
                .where("category").eq("y")
                .limit(Limit.of(5))
                .result();
        assertTrue(filtered.stream().allMatch(a -> "y".equals(a.getCategory())));
        assertEquals("s-2", filtered.get(0).getId());
    }
}
