package io.quarkiverse.jnosql.qdrant.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(JNoSQLResource.class)
@QuarkusTestResource(QdrantTestResource.class)
public class JNoSQLResourceTest {

    @Test
    void shouldPersistFindAndSearch() {
        given().when().post("/seed").then().statusCode(200);

        given().when().get("/articles/2").then().statusCode(200)
                .body("title", is("Qdrant"))
                .body("category", is("database"));

        given().when().get("/search").then().statusCode(200)
                .body("", contains("1", "3"));
    }
}
