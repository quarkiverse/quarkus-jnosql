package io.quarkiverse.jnosql.column.cassandra.it;

import com.datastax.oss.quarkus.test.CassandraTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
@QuarkusTestResource(CassandraTestResource.class)
public class JNoSQLResourceIT extends JNoSQLResourceTest {
}
