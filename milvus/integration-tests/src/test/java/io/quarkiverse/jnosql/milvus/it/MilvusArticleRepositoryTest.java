package io.quarkiverse.jnosql.milvus.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.eclipse.jnosql.mapping.vector.DenseVector;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(MilvusTestResource.class)
class MilvusArticleRepositoryTest {

    @Inject
    Instance<ArticleRepository> repositories;

    @Inject
    ArticleRepository repository;

    @Test
    void shouldResolveGeneratedRepository() {
        assertTrue(repositories.isResolvable());
        assertNotNull(repository);
    }

    @Test
    void shouldSaveFindUpdateAndDelete() {
        repository.save(new Article("r-1", "Hello", "repo-a", DenseVector.of(1f, 0f, 0f, 0f)));

        Article found = repository.findById("r-1").orElseThrow();
        assertEquals("Hello", found.getTitle());
        assertEquals("repo-a", found.getCategory());
        assertEquals(4, found.getEmbedding().values().length);

        repository.save(new Article("r-1", "Updated", "repo-a", DenseVector.of(1f, 0f, 0f, 0f)));
        assertEquals("Updated", repository.findById("r-1").orElseThrow().getTitle());

        repository.deleteById("r-1");
        assertTrue(repository.findById("r-1").isEmpty());
    }

    @Test
    void shouldQueryByCategory() {
        repository.save(new Article("q-1", "One", "repo-q", DenseVector.of(0f, 1f, 0f, 0f)));
        repository.save(new Article("q-2", "Two", "repo-q", DenseVector.of(0.5f, 0.5f, 0.5f, 0.5f)));
        repository.save(new Article("q-3", "Three", "repo-other", DenseVector.of(0f, 0f, 0f, 1f)));

        List<Article> result = repository.findByCategory("repo-q");
        assertEquals(Set.of("q-1", "q-2"), result.stream().map(Article::getId).collect(Collectors.toSet()));
    }
}
