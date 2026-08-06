package io.quarkiverse.jnosql.keyvalue.redis.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkiverse.jnosql.redis.it.JNoSQLResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(JNoSQLResource.class)
@QuarkusTestResource(RedisTestResource.class)
public class JNoSQLResourceTest {

    @ParameterizedTest
    @CsvSource({
            "/template/using-pojo",
            "/keyvalue/using-pojo",
            "/template/using-record",
            "/keyvalue/using-record",
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

    @Test
    public void shouldShareTheConnectionPoolAmongConcurrentOperations() {
        given()
                .when()
                .get("/keyvalue/concurrent")
                .then()
                .log().all()
                .statusCode(200)
                .body(is("64"));
    }
}
