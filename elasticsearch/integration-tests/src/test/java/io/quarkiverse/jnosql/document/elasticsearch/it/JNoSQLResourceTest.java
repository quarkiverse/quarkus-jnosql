package io.quarkiverse.jnosql.document.elasticsearch.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkiverse.jnosql.elasticsearch.it.JNoSQLResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(JNoSQLResource.class)
@QuarkusTestResource(ElasticsearchTestResource.class)
public class JNoSQLResourceTest {

    @ParameterizedTest
    @CsvSource({
            "/using-jakarta-nosql",
            "/using-jakarta-nosql-record",
            "/using-jakarta-data",
            "/using-jakarta-data-record",
    })
    public void test(String path) {
        given()
                .when()
                .get(path)
                .then()
                .log().all()
                .statusCode(200)
                .body(is(not(empty())));
    }
}
