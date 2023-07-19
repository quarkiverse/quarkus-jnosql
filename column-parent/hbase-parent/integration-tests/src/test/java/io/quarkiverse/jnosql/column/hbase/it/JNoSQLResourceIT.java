package io.quarkiverse.jnosql.column.hbase.it;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
@QuarkusTestResource(HBaseTestResource.class)
public class JNoSQLResourceIT extends JNoSQLResourceTest {
}
