package io.quarkiverse.jnosql.keyvalue.memcached.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkiverse.jnosql.memcached.it.JNoSQLResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(JNoSQLResource.class)
@QuarkusTestResource(MemcachedTestResource.class)
public class JNoSQLResourceTest {

    @ParameterizedTest
    @ValueSource(strings = { "/template", "/keyvalue" })
    void shouldStoreRetrieveAndDelete(String api) {
        String id = UUID.randomUUID().toString();
        String path = api + "/" + id;
        List<String> phones = List.of("123456789");
        Map<String, Object> person = Map.of("id", id, "name", "Ada", "phones", phones);

        given().when().get(path).then().statusCode(404);

        given()
                .contentType(ContentType.JSON)
                .body(person)
                .when().put(api)
                .then().statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Ada"))
                .body("phones", equalTo(phones));

        given().when().get(path)
                .then().statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Ada"))
                .body("phones", equalTo(phones));

        given().when().delete(path).then().statusCode(204);
        given().when().get(path).then().statusCode(404);
    }
}
