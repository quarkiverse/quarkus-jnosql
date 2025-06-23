package io.quarkiverse.jnosql.keyvalue.hazelcast.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkiverse.jnosql.hazelcast.it.JNoSQLResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.hazelcast.HazelcastServerTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(JNoSQLResource.class)
@QuarkusTestResource(HazelcastServerTestResource.class)
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
}
