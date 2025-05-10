# Quarkus JNoSQL

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-4-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.jnosql/quarkus-jnosql-core?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.jnosql/quarkus-jnosql-core)

This documentation provides instructions on how to integrate [JNoSQL](https://www.jnosql.org/), an implementation of [Jakarta NoSQL](https://jakarta.ee/specifications/nosql/) and [Jakarta Data](https://jakarta.ee/specifications/data/), into a Quarkus project using the Quarkus JNoSQL Extension. 

This extension supports JNoSQL and facilitates using NoSQL databases in your Quarkus applications.

:information_source: **Recommended Quarkus version: `3.2.2.Final` or higher**

## Getting Started

To begin using JNoSQL with Quarkus, follow these steps:

1. Add the Quarkus JNoSQL Extension to your project's dependencies that fits with your necessities. Here are the supported
   databases by now and their respective NoSQL types:

   | Database Vendor                 | Supported NoSQL Type   | Supports Jakarta Data     | Supports Native Compilation | 
   |---------------------------------|------------------------|---------------------------| -------------------------- |
   | [MongoDB](#mongodb)             | Document               | ✅                         | ✅                          |
   | [Cassandra](#cassandra)         | Column                 | ✅                         | ✅                          |
   | [CouchDB](#couchdb)             | Document               | ✅                         | ✅                          |
   | [ArangoDB](#arangodb)           | Document and Key-Value | ✅                         | ✅                          |
   | [DynamoDB](#dynamodb)           | Key-Value              | ❌                         | ✅                          |
   | [Elasticsearch](#elasticsearch) | Document               | ✅                         | ❌                          | 
   | [Hazelcast](#hazelcast)         | Key-Value              | ❌                         | ✅                          |
   | [Solr](#solr)                   | Document               | ✅                         | ✅                          |
   | [Neo4j](#neo4j)                 | Graph                  | ❌                          | ✅                          |
   | [Oracle NoSQL](#oracle-nosql)   | Document and Key-Value | ✅                          | ✅                          |


2. If you're using **Java 21** or above you should activate explicitly the annotation processor execution by setting `<proc>full</proc>` on the maven-compiler plugin. If you're using any previous Java version, e.g. **Java 17**, you can skip this step.
   
   This is necessary for the **JNoSQL Lite Extension** to work properly. An `pom.xml` configuration example below:

   ```xml
   <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>${compiler-plugin.version}</version>
        <configuration>
            <proc>full</proc>
            <compilerArgs>
                <arg>-parameters</arg>
            </compilerArgs>
        </configuration>
    </plugin>
   ```

3. Define your entities using JNoSQL annotations. Here's an example entity class:

   ```java
   import jakarta.nosql.Column;
   import jakarta.nosql.Entity;
   import jakarta.nosql.Id;

   @Entity
   public class TestEntity {

       @Id
       private String id;

       @Column
       private String testField;
   
       // omitted getters and setters
   }
   ```

4. Use the JNoSQL template to perform CRUD operations on your entities.

   * Here's an example of how to use the `jakarta.nosql.Template`:
   
   ```java
   @Inject
   @Database(DatabaseType.COLUMN)
   protected Template template;

   public void insert(TestEntity entity) {
    template.insert(entity);
   }
   ```
   
   * For implementation that offers Jakarta Data support you may create and inject a **Jakarta Data** Repository. Here's an usage example:

   ```java
   @Repository
   public interface TestEntityRepository extends NoSQLRepository<TestEntity, String> {
   }
   ```

   * With that, you can inject and use the `TestEntityRepository` in your service classes as the example below: 

   ```java
   
   @ApplicationScope
   class TestEntityService {
   
       @Inject
       @Database(DatabaseType.COLUMN)
       TestEntityRepository repository;
   
       public void insert(TestEntity entity) {
          repository.save(entity);
       }
   }
   ```

Next, we will provide specific instructions for each supported database.

## MongoDB

<img src="https://jnosql.github.io/img/logos/mongodb.png" alt="MongoDB Project" align="center" width="25%" height="25%"/>

https://www.mongodb.com/[MongoDB] is a free and open-source cross-platform document-oriented database program.
Classified as a NoSQL database program, MongoDB uses JSON-like documents with schemas.

This driver provides support for the *Document* NoSQL API.

It supports **Jakarta Data**.

Add the MongoDB dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-mongodb</artifactId>
</dependency>
```

To define the **Document** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.document.database=my-database-name
```

For specific configuration details, please refer to the [MongoDB Quarkus extension](https://quarkus.io/guides/mongodb).

## Cassandra

<img src="https://jnosql.github.io/img/logos/cassandra.png" alt="Apache Cassandra Project" align="center" width="25%" height="25%"/>

[Apache Cassandra](https://cassandra.apache.org/) is a free and open-source distributed database management system designed to handle large amounts of data across many commodity servers, providing high availability with no single point of failure.

This driver provides support for the *Column* NoSQL API.

It supports **Jakarta Data**.

Add the Cassandra dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-cassandra</artifactId>
</dependency>
```

To define the **Column** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.column.database=my-database-name
```

Please refer to the [Cassandra Quarkus extension](https://quarkus.io/guides/cassandra) for specific configuration
details.

## ArangoDB

<img src="https://jnosql.github.io/img/logos/ArangoDB.png" alt="ArangoDB Project" align="center" width="25%" height="25%" />

[ArangoDB](https://www.arangodb.com/) is a native multi-model database with flexible data models for documents, graphs,
and key-values.
Build high performance applications using a convenient SQL-like query language or JavaScript extensions.

This extension offers support for **Document** and **Key-Value** types. Also, it provides support for **Jakarta Data** for Document NoSQL Entities.

Add the ArangoDB dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-arangodb</artifactId>
    <version>${quarkus-jnosql.version}</version>
</dependency>
```

To define the **Key-Value** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.keyvalue.database=my-database-name
```

To define the **Document** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.document.database=my-database-name
```

For specific configuration details, please refer to
the [ArangoDB JNoSQL driver](https://github.com/eclipse/jnosql-databases#arangodb).

## DynamoDB

<img src="https://user-images.githubusercontent.com/6509926/70553550-f033b980-1b40-11ea-9192-759b3b1053b3.png" align="center" width="25%" height="25%"/>

[Amazon DynamoDB](https://aws.amazon.com/dynamodb/) is a fully managed, serverless, key-value and document NoSQL database designed to run high-performance applications at any scale. DynamoDB offers built-in security, continuous backups, automated multi-Region replication, in-memory caching, and data import and export tools.

This driver has support for two NoSQL API types: *Key-Value*.

Add the DynamoDB dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-dynamodb</artifactId>
</dependency>
```

To define the **Key-Value** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.keyvalue.database=my-database-name
```

Please refer to
the [DynamoDB Quarkiverse extension](https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/amazon-dynamodb.html)
for specific configuration details.

## Hazelcast

<img src="https://jnosql.github.io/img/logos/hazelcast.svg" alt="Hazelcast Project" align="center" width="25%" height="25%"/>

[Hazelcast](https://hazelcast.com/) is an open source in-memory data grid based on Java.

This driver provides support for the *Key-Value* NoSQL API.

Add the Hazelcast dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-hazelcast</artifactId>
</dependency>
```

To define the **Key-Value** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.keyvalue.database=my-database-name
```

Please refer to the [Quarkus Hazelcast extension](https://github.com/hazelcast/quarkus-hazelcast-client) for specific
configuration details.

## CouchDB

<img src="https://www.jnosql.org/img/logos/couchdb.png" alt="CouchDB" align="center" width="25%" height="25%"/>

The [CouchDB](https://couchdb.apache.org/) driver provides an API integration between Java and the database through a standard communication level.

This driver provides support for the *Document* NoSQL API.

It supports **Jakarta Data**.

Add the CouchDB dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-couchdb</artifactId>
</dependency>
```

To define the **Document** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.document.database=my-database-name
```

For specific configuration details, please refer to
the [CouchDB JNoSQL driver](https://github.com/eclipse/jnosql-databases#couchdb).

## Elasticsearch

<img src="https://jnosql.github.io/img/logos/elastic.svg" alt="Elasticsearch Project" align="center" width="25%" height="25%"/>

[Elasticsearch](https://www.elastic.co/) is a search engine based on Lucene.  
It provides a distributed, multitenant-capable full-text search engine with an HTTP web interface and schema-free JSON documents.  
Elasticsearch is developed in Java and is released as open source under the terms of the Apache License. Elasticsearch is the most popular enterprise search engine followed by Apache Solr, also based on Lucene.

This driver provides support for the *Document* NoSQL API.

It supports **Jakarta Data**.

:information_source: **It does not support native compilation, unfortunately.**

Add the Elasticsearch dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-elasticsearch</artifactId>
</dependency>
```

To define the **Document** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.document.database=my-database-name
```
Please refer to
the [Elasticsearch Quarkus extension](https://quarkus.io/guides/elasticsearch#using-the-elasticsearch-java-client) for
specific configuration details.

## Solr

<img src="https://jnosql.github.io/img/logos/solr.svg" alt="Apache Solr Project" align="center" width="20%" height="20%"/>

[Solr](https://solr.apache.org/) is an open-source enterprise-search platform, written in Java, from the Apache Lucene project.
Its major features include full-text search, hit highlighting, faceted search, real-time indexing, dynamic clustering, database integration, NoSQL features and rich document (e.g., Word, PDF) handling.
Providing distributed search and index replication, Solr is designed for scalability and fault tolerance.
Solr is widely used for enterprise search and analytics use cases and has an active development community and regular releases.

This driver provides support for the *Document* NoSQL API.

It supports **Jakarta Data**.

Add the Quarkus JNoSQL Solr dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-solr</artifactId>
</dependency>
```

For specific configuration details, please refer to
the [Solr JNoSQL driver](https://github.com/eclipse/jnosql-databases#solr).


## Neo4j

<img src="https://jnosql.github.io/img/logos/neo4j.png" alt="Neo4J Project" align="center" width="25%" height="25%"/>

[Neo4J](https://neo4j.com/) is a highly scalable, native graph database designed to manage complex relationships in data. It enables developers to build applications that leverage the power of graph traversal, pattern matching, and high-performance querying using the **Cypher** query language.

This API provides support for **Graph** database operations, including entity persistence, query execution via Cypher, and relationship traversal.

:information_source: This extension is using the **org.eclipse.jnosql.databases:jnosql-neo4j:1.1.7-SNAPSHOT**

Add the Quarkus JNoSQL Neo4j dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-neo4j</artifactId>
</dependency>
```

Now, you can use the `org.eclipse.jnosql.mapping.graph.GraphTemplate`, a `jakarta.nosql.Template` specialized interface, to perform CRUD operations on your entities.

* Here's an example of how to use the `jakarta.nosql.Template`:

  ```java
  @Inject
  @Database(DatabaseType.GRAPH)
  protected GraphTemplate template;

  public void insert(TestEntity entity) {
   template.insert(entity);
  }
  ```

To use the **org.eclipse.jnosql.databases:jnosql-neo4j:1.1.7-SNAPSHOT** API, you need to add the following repositories to your `pom.xml` or to your `$HOME/.m2/settings.xml`:

```xml
    <repositories>
        <repository>
            <id>jakarta.sonatype.org-snapshot</id>
            <url>https://jakarta.oss.sonatype.org/content/repositories/snapshots/</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>oss.sonatype.org-snapshot</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
```


For specific configuration details, please refer to the [Quarkus Neo4j extension](https://docs.quarkiverse.io/quarkus-neo4j/dev/index.html).

## Oracle NoSQL

<img src="https://jnosql.github.io/img/logos/oracle.png" alt="Oracle NoSQL Project" align="center" width="25%" height="25%"/>

[Oracle NoSQL Database](https://www.oracle.com/database/nosql/technologies/nosql/) is a versatile multi-model database offering flexible data models for documents, graphs, and key-value pairs. It empowers developers to build high-performance applications using a user-friendly SQL-like query language or JavaScript extensions.

This API provides support for *Document* and *Key-Value* data types.

It supports **Jakarta Data**.

Add the Quarkus JNoSQL Oracle NoSQL dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-oracle-nosql</artifactId>
</dependency>
```

For specific configuration details, please refer to
the [Oracle NoSQL JNoSQL driver](https://github.com/eclipse/jnosql-databases#oracle-nosql).


## Contributors ✨

Thanks to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)) for their contributions:

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/amoscatelli"><img src="https://avatars.githubusercontent.com/u/16684470?v=4?s=100" width="100px;" alt="amoscatelli"/><br /><sub><b>amoscatelli</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=amoscatelli" title="Code">💻</a> <a href="#maintenance-amoscatelli" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://otaviojava.com/"><img src="https://avatars.githubusercontent.com/u/863011?v=4?s=100" width="100px;" alt="Otávio Santana"/><br /><sub><b>Otávio Santana</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=otaviojava" title="Code">💻</a> <a href="#maintenance-otaviojava" title="Maintenance">🚧</a> <a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=otaviojava" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://link.maxdearruda.com/me"><img src="https://avatars.githubusercontent.com/u/6537623?v=4?s=100" width="100px;" alt="Maximillian Arruda"/><br /><sub><b>Maximillian Arruda</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=dearrudam" title="Code">💻</a> <a href="#maintenance-dearrudam" title="Maintenance">🚧</a> <a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=dearrudam" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gastaldi"><img src="https://avatars.githubusercontent.com/u/54133?v=4?s=100" width="100px;" alt="George Gastaldi"/><br /><sub><b>George Gastaldi</b></sub></a><br /><a href="#infra-gastaldi" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="#maintenance-gastaldi" title="Maintenance">🚧</a> <a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=gastaldi" title="Documentation">📖</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification.
Contributions of any kind are welcome!
