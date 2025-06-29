package ilove.quark.us;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.nosql.Template;
import jakarta.inject.Inject;
import net.datafaker.Faker;
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
@io.quarkus.test.common.QuarkusTestResource(ArangodbTestResource.class)
public class TemplateTest {

    @Inject
    // As this extension supports DOCUMENT and KEY_VALUE databases,
    // we should define which one we want to use explicitly,
    // otherwise an ambiguous issue may occur.
    @Database(DatabaseType.KEY_VALUE)
    Template template;

    Faker faker = new Faker();

    @Test
    void shouldInsert() {
        Person person = new Person(
                UUID.randomUUID().toString(),
                faker.name().fullName(),
                List.of(
                        faker.phoneNumber().phoneNumber(),
                        faker.phoneNumber().phoneNumber(),
                        faker.phoneNumber().phoneNumber()
                )
        );
        template.insert(person);
        assertThat(template.find(Person.class, person.id()))
                .contains(person);
    }

}
