package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for MagazineRepository.
 * This class contains test methods to verify the functionality of the MagazineRepository.
 */
@QuarkusTest
public class MagazineRepositoryTest {

    @Inject
    // add this one if you're using multiple NoSQL databases
    // @Database(DatabaseType.GRAPH)
    private MagazineRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.findAll().forEach(repository::delete);
    }

    @Test
    void shouldSave() {
        Magazine magazine = new Magazine(null, "Effective Java", 1);
        assertThat(repository.save(magazine))
                .isNotNull();
    }

    @Test
    void shouldFindAll() {
        for (int index = 0; index < 5; index++) {
            Magazine magazine = repository.save(new Magazine(null, "Effective Java", index));
            assertThat(magazine).isNotNull();
        }
        var result = repository.findAll().toList();
        SoftAssertions.assertSoftly(soft -> {
            assertThat(result).isNotNull();
            assertThat(result).hasSize(5);
        });
    }

    @Test
    void shouldFindByName() {
        for (int index = 0; index < 5; index++) {
            Magazine magazine = repository.save(new Magazine(null, "Effective Java", index));
            assertThat(magazine).isNotNull();
        }
        var result = repository.findByTitle("Effective Java");
        SoftAssertions.assertSoftly(soft -> {
            assertThat(result).isNotNull();
            assertThat(result).hasSize(5);
        });
    }
}
