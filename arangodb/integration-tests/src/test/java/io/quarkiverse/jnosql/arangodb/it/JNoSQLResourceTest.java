package io.quarkiverse.jnosql.arangodb.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ArangodbTestResource.class)
@TestHTTPEndpoint(JNoSQLResource.class)
public class JNoSQLResourceTest {

    @ParameterizedTest
    @CsvSource({
            "/document/using-jakarta-nosql",
            "/document/using-jakarta-nosql-record",
            "/document/using-jakarta-data",
            "/document/using-jakarta-data-record",
            "/keyvalue/using-pojo",
            "/keyvalue/using-record",
    })
    public void test(String path) {
        given()
                .when()
                .get(path)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("name", not(emptyOrNullString()))
                .body("phones", hasSize(3));
    }
}
