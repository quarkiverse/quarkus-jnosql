package org.acme;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import jakarta.inject.Inject;

import org.eclipse.jnosql.mapping.vector.DenseVector;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test class for ArticleRepository.
 */
@QuarkusTest
@QuarkusTestResource(QdrantTestResource.class)
public class ArticleRepositoryTest {

    @Inject
    ArticleRepository repository;

    @Test
    void shouldSaveFindAndDelete() {
        repository.save(new Article("a-1", "Effective Java", "books", DenseVector.of(1f, 0f, 0f, 0f)));

        Article article = repository.findById("a-1").orElseThrow();
        assertThat(article.getTitle()).isEqualTo("Effective Java");
        assertThat(article.getCategory()).isEqualTo("books");
        assertThat(article.getEmbedding().values()).hasSize(4);

        repository.deleteById("a-1");
        assertThat(repository.findById("a-1")).isEmpty();
    }

    @Test
    void shouldFindByCategory() {
        repository.save(new Article("c-1", "One", "music", DenseVector.of(0f, 1f, 0f, 0f)));
        repository.save(new Article("c-2", "Two", "music", DenseVector.of(0f, 0f, 1f, 0f)));
        repository.save(new Article("c-3", "Three", "movies", DenseVector.of(0f, 0f, 0f, 1f)));

        List<Article> result = repository.findByCategory("music");
        assertThat(result).extracting(Article::getId).containsExactlyInAnyOrder("c-1", "c-2");
    }
}
