package io.quarkiverse.jnosql.scylladb.codestart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.devtools.codestarts.quarkus.QuarkusCodestartCatalog.Language;
import io.quarkus.devtools.testing.codestarts.QuarkusCodestartTest;

public class CodestartTest {

    public static final String CODESTART_ARTIFACT = "io.quarkiverse.jnosql:quarkus-jnosql-scylladb";
    @RegisterExtension
    public static QuarkusCodestartTest codestartTest = QuarkusCodestartTest.builder()
            .languages(Language.JAVA)
            .setupStandaloneExtensionTest(CODESTART_ARTIFACT)
            .build();

    @Test
    void testContent() throws Throwable {
        codestartTest.checkGeneratedSource("org.acme.Car");
        codestartTest.checkGeneratedSource("org.acme.Garage");
        codestartTest.checkGeneratedTestSource("org.acme.GarageTest");
        codestartTest.assertThatGeneratedFile(Language.JAVA, "src/test/java/ilove/quark/us/ScyllaDBTestResource.java")
                .content().contains("scylladb/scylla:2026.3.1", "getContactPoint().getPort()")
                .doesNotContain("com.datastax.oss.quarkus");
    }

    @Test
    void buildAllProjects() throws Throwable {
        codestartTest.buildAllProjects();
    }
}