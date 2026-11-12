### Configuration

- Oracle NoSQL is multi-model: configure BOTH `jnosql.document.database` and `jnosql.keyvalue.database` for the respective stores.
- Connection: `jnosql.oracle.nosql.host`, `jnosql.oracle.nosql.user`, `jnosql.oracle.nosql.password`, `jnosql.oracle.nosql.tenant.id`, `jnosql.oracle.nosql.compartment`, `jnosql.oracle.nosql.namespace`, `jnosql.oracle.nosql.deployment`, plus `jnosql.oracle.nosql.config.file`, `jnosql.oracle.nosql.fingerprint`, `jnosql.oracle.nosql.private.key` for wallet/TLS setups.
- Every injection point MUST carry `@Database(DatabaseType.DOCUMENT)` or `@Database(DatabaseType.KEY_VALUE)` — both types are produced and unqualified injection is ambiguous.

### Data access

- Document: inject `DocumentTemplate`/`Template` with `@Database(DOCUMENT)`; `OracleNoSQLDocumentManager` for the low-level API; `@Repository` interfaces extending `NoSQLRepository<Entity, IdType>`.
- Key-value: inject `Template` with `@Database(KEY_VALUE)`; `OracleNoSQLBucketManager` for key-based access; `BasicRepository` for key-based CRUD only.

### Testing

- Use `@QuarkusTest` with `OracleNoSQLTestResource` (Testcontainers) returning the `jnosql.oracle.nosql.*` properties.

### Common Pitfalls

- Do NOT forget the `@Database` qualifier — with both types on the classpath, unqualified `Template`/repository injection fails.
- Wallet-based connections need the `config.file`/`fingerprint`/`private.key` properties — plain user/password applies to non-wallet setups.
