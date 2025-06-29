package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.nosql.Template;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>TemplateTest is a sample test class for Template .</p>
 *
 * <p>It uses Quarkus Test framework and Faker library to generate random data for testing.</p>
 */
@QuarkusTest
{#if additional-quarkus-test-annotations}{additional-quarkus-test-annotations}{/if}
public class TemplateTest {

    @Inject
    @Database(DatabaseType.KEY_VALUE)
    Template template;

    @Test
    void shouldInsert() {
        Person person = randomPerson();
        template.insert(person);
        assertThat(template.find(Person.class, person.id()))
                .contains(person);
    }

    // Factory method to create a Person instance
    public static Person randomPerson() {
        var person = new Person(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                List.of(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
                )
        );
        return person;
    }


}
