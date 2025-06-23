package io.quarkiverse.jnosql.keyvalue.hazelcast.codestart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.devtools.codestarts.quarkus.QuarkusCodestartCatalog.Language;
import io.quarkus.devtools.testing.codestarts.QuarkusCodestartTest;

public class CodestartTest {

    public static final String CODESTART_ARTIFACT = "io.quarkiverse.jnosql:quarkus-jnosql-hazelcast";
    @RegisterExtension
    public static QuarkusCodestartTest codestartTest = QuarkusCodestartTest.builder()
            .languages(Language.JAVA)
            .setupStandaloneExtensionTest(CODESTART_ARTIFACT)
            .build();

    @Test
    void testContent() throws Throwable {
        codestartTest.checkGeneratedSource("org.acme.Person");
        codestartTest.checkGeneratedTestSource("org.acme.TemplateTest");
        codestartTest.checkGeneratedTestSource("org.acme.BucketManagerTest");
    }

    @Test
    void buildAllProjects() throws Throwable {
        codestartTest.buildAllProjects();
    }
}
