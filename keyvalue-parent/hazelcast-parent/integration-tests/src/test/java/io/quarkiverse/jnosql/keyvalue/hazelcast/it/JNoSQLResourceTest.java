package io.quarkiverse.jnosql.keyvalue.hazelcast.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.hazelcast.HazelcastServerTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(JNoSQLResource.class)
@QuarkusTestResource(HazelcastServerTestResource.class)
public class JNoSQLResourceTest {

    @Disabled
    @Test
    public void testHelloEndpoint() {
        given()
                .when().get()
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }
}
