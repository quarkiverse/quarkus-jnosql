package ilove.quark.us;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.ValueUtil;
import org.eclipse.jnosql.communication.keyvalue.BucketManager;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>TemplateTest is a sample test class for Template .</p>
 *
 * <p>It uses Quarkus Test framework and Faker library to generate random data for testing.</p>
 */
@QuarkusTest
@io.quarkus.test.common.QuarkusTestResource(ArangodbTestResource.class)
public class BucketManagerTest {

    @Inject
    BucketManager bucketManager;

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
        bucketManager.put(person.id(), person);
        assertThat(bucketManager.get(person.id())
                .map(v -> v.get(Person.class)))
                .contains(person);
    }

}
