package io.quarkiverse.jnosql.scylladb.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ScyllaDBTestResource.class)
@TestHTTPEndpoint(JNoSQLResource.class)
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
                .body("id", is(not(empty())))
                .body("name", is(not(empty())))
                .body("phones", hasSize(3));
    }

    @ParameterizedTest
    @CsvSource({ "/column-crud", "/template-crud", "/repository-crud" })
    void shouldCreateReadUpdateAndDelete(String path) {
        given().when().get(path).then().statusCode(200).body(is("updated:true"));
    }
}
