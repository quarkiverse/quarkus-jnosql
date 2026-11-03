package io.quarkiverse.jnosql.tinkerpop.deployment;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class MissingGraphTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
            .assertException(t -> {
                Throwable current = t;
                boolean found = false;
                while (current != null && !found) {
                    found = String.valueOf(current.getMessage()).contains(Processor.MISSING_GRAPH_MESSAGE);
                    current = current.getCause();
                }
                assertTrue(found, "Expected the missing Graph message but got: " + t);
            });

    @Test
    void shouldFailWithoutGraphProducer() {
        // the application must fail during build, so this is never executed
    }
}
