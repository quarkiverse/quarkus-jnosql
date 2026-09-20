package io.quarkiverse.jnosql.influxdb.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(JNoSQLResource.class)
@QuarkusTestResource(InfluxDBTestResource.class)
public class JNoSQLResourceTest {

    @ParameterizedTest
    @CsvSource({
            "/using-timeseries-template,template,42.0",
            "/using-jakarta-data,repository,21.0"
    })
    void test(String path, String source, double value) {
        given()
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .body("source", is(source))
                .body("value", is((float) value));
    }
}
