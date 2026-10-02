package io.quarkiverse.jnosql.keyvalue.infinispan.codestart;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.devtools.codestarts.quarkus.QuarkusCodestartCatalog.Language;
import io.quarkus.devtools.testing.codestarts.QuarkusCodestartTest;

public class CodestartTest {

    public static final String CODESTART_ARTIFACT = "io.quarkiverse.jnosql:quarkus-jnosql-infinispan";
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
        codestartTest.assertThatGeneratedFile(Language.JAVA, "src/main/resources/infinispan.xml")
                .content().contains("<local-cache name=\"people\"");
        codestartTest.assertThatGeneratedFile(Language.JAVA, "src/main/resources/application.properties")
                .content().contains("jnosql.infinispan.config=infinispan.xml", "jnosql.keyvalue.database=people");
        codestartTest.assertThatGeneratedFile(Language.JAVA, "pom.xml").content()
                .contains("<artifactId>infinispan-bom</artifactId>",
                        "<infinispan.version>" + artifactVersion("org.infinispan", "infinispan-core")
                                + "</infinispan.version>",
                        "<infinispan.protostream.version>" + artifactVersion("org.infinispan.protostream", "protostream")
                                + "</infinispan.protostream.version>");
    }

    @Test
    void testBuild() throws Exception {
        codestartTest.buildAllProjects();
    }

    private static String artifactVersion(String groupId, String artifactId) throws IOException {
        String path = "/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
        try (var input = CodestartTest.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IOException("Missing dependency version metadata: " + path);
            }
            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty("version");
        }
    }

}
