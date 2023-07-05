package io.quarkiverse.jnosql.column.cassandra.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

import com.datastax.oss.quarkus.test.CassandraTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
@TestHTTPEndpoint(JNoSQLResource.class)
public class JNoSQLResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get()
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }
}
