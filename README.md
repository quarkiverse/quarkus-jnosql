# Quarkus JNoSQL

## Table of Contents

- [Getting Started](#getting-started)
- [Supported NoSQL Databases](#supported-nosql-databases)
- [Create your Quarkus JNoSQL Project using Extension Codestarts](#create-your-quarkus-jnosql-project-using-extension-codestarts)
- [Manually adding Quarkus JNoSQL Extension to your project](#manually-adding-quarkus-jnosql-extension-to-your-project)
- [Enabling the JNoSQL Mapping Lite Annotation Processor](#enabling-the-jnosql-mapping-lite-annotation-processor)
- [Using Jakarta NoSQL and Jakarta Data with Quarkus JNoSQL](#using-jakarta-nosql-and-jakarta-data-with-quarkus-jnosql)
- [Document Databases](#document-databases)
  - [MongoDB](#mongodb)
  - [CouchDB](#couchdb)
  - [Elasticsearch](#elasticsearch)
  - [Solr](#solr)
- [Column Databases](#column-databases)
  - [Cassandra](#cassandra)
  - [ScyllaDB](#scylladb)
- [Key-Value Databases](#key-value-databases)
  - [DynamoDB](#dynamodb)
  - [Hazelcast](#hazelcast)
  - [Redis](#redis)
  - [Infinispan](#infinispan)
  - [Memcached](#memcached)
  - [Valkey](#valkey)
- [Graph Databases](#graph-databases)
  - [Neo4j](#neo4j)
- [Time Series Databases](#time-series-databases)
  - [InfluxDB](#influxdb)
  - [Apache IoTDB](#apache-iotdb)
  - [QuestDB](#questdb)
- [Multi-model Databases](#multi-model-databases)
  - [ArangoDB](#arangodb)
  - [Oracle NoSQL](#oracle-nosql)
- [Contributors](#contributors-)

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-4-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.jnosql/quarkus-jnosql-core?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.jnosql/quarkus-jnosql-core)

This documentation provides instructions on how to integrate [JNoSQL](https://www.jnosql.org/), an implementation
of [Jakarta NoSQL](https://jakarta.ee/specifications/nosql/)
and [Jakarta Data](https://jakarta.ee/specifications/data/), into a Quarkus project using the Quarkus JNoSQL Extension.

This extension supports JNoSQL and facilitates using NoSQL databases in your Quarkus applications.

:information_source: **Recommended Quarkus version: `3.2.2.Final` or higher**.

## Getting Started

To use this extension, add the Quarkus JNoSQL Database extension that you need to your build file.For instance, with
Maven, include the following dependency in your POM file:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-[DATABASE]</artifactId>
    <version>{project-version}</version>
</dependency>
```

Replace `[DATABASE]` with the specific database you want to use. Let see the supported NoSQL databases in the next
section.

And replace `{project-version}` with the latest stable version of the Quarkus JNoSQL Extension.

## Supported NoSQL Databases

The Quarkus JNoSQL extension supports a variety of NoSQL databases, grouped by database model to make it easier to find the integration that best fits your application.

### Document

| Database Vendor                 | Supports Jakarta Data | Provides Codestart | Supports Native Compilation |
|---------------------------------|-----------------------|--------------------|-----------------------------|
| [MongoDB](#mongodb)             | ✅                     | ✅                  | ✅                           |
| [CouchDB](#couchdb)             | ✅                     | ✅                  | ✅                           |
| [Elasticsearch](#elasticsearch) | ✅                     | ✅                  | ❌                           |
| [Solr](#solr)                   | ✅                     | ✅                  | ✅                           |

### Column

| Database Vendor             | Supports Jakarta Data | Provides Codestart | Supports Native Compilation |
|-----------------------------|-----------------------|--------------------|-----------------------------|
| [Cassandra](#cassandra)     | ✅                     | ✅                  | ✅                           |
| [ScyllaDB](#scylladb)       | ✅                     | ✅                  | ✅                           |

### Key-Value

| Database Vendor             | Supports Jakarta Data | Provides Codestart | Supports Native Compilation |
|-----------------------------|-----------------------|--------------------|-----------------------------|
| [DynamoDB](#dynamodb)       | ❌                     | ✅                  | ✅                           |
| [Hazelcast](#hazelcast)     | ❌                     | ✅                  | ✅                           |
| [Redis](#redis)             | ❌                     | ✅                  | ✅                           |
| [Infinispan](#infinispan)   | ❌                     | ✅                  | Not verified                 |
| [Memcached](#memcached)     | ❌                     | ✅                  | CRUD verified               |
| [Valkey](#valkey)           | ❌                     | ✅                  | ✅                           |

### Graph

| Database Vendor         | Supports Jakarta Data | Provides Codestart | Supports Native Compilation |
|-------------------------|-----------------------|--------------------|-----------------------------|
| [Neo4j](#neo4j)         | ✅                     | ✅                  | ✅                           |

### Time Series

| Database Vendor             | Supports Jakarta Data | Provides Codestart | Supports Native Compilation |
|-----------------------------|-----------------------|--------------------|-----------------------------|
| [InfluxDB](#influxdb)       | ✅                     | ✅                  | ✅                           |
| [Apache IoTDB](#apache-iotdb) | ✅                   | ✅                  | ❌                           |
| [QuestDB](#questdb)         | ✅                     | ✅                  | ❌                           |

### Multi-model

| Database Vendor                 | Supported NoSQL Type        | Supports Jakarta Data | Provides Codestart | Supports Native Compilation |
|---------------------------------|-----------------------------|-----------------------|--------------------|-----------------------------|
| [ArangoDB](#arangodb)           | Document and Key-Value      | ✅                     | ✅                  | ✅                           |
| [Oracle NoSQL](#oracle-nosql)   | Document and Key-Value      | ✅                     | ✅                  | ✅                           |

## Create your Quarkus JNoSQL Project using Extension Codestarts

The easiest way to get started with Quarkus JNoSQL Extension is by using the code start provided by the extension. This
code start will generate a Quarkus project with the necessary dependencies and configurations for using JNoSQL with your
chosen NoSQL database.

You can use the Quarkus CLI to create a new project passing the Quarkus JNoSQL Extension that you want. It'll create the
project with all required dependencies and configurations automatically. For example, to create a project using Quarkus
JNoSQL MongoDB Extension, you can run:

```bash
quarkus create app --extensions=jnosql-mongodb
```

Here are the available Quarkus JNoSQL Extensions that you can use with the `quarkus create app` command, grouped by database model:

### Document

| Database Vendor                 | Command                                                |
|---------------------------------|--------------------------------------------------------|
| [MongoDB](#mongodb)             | `quarkus create app --extensions=jnosql-mongodb`       |
| [CouchDB](#couchdb)             | `quarkus create app --extensions=jnosql-couchdb`       |
| [Elasticsearch](#elasticsearch) | `quarkus create app --extensions=jnosql-elasticsearch` |
| [Solr](#solr)                   | `quarkus create app --extensions=jnosql-solr`          |

### Column

| Database Vendor             | Command                                                |
|-----------------------------|--------------------------------------------------------|
| [Cassandra](#cassandra)     | `quarkus create app --extensions=jnosql-cassandra`     |
| [ScyllaDB](#scylladb)       | `quarkus create app --extensions=jnosql-scylladb`      |

### Key-Value

| Database Vendor             | Command                                                |
|-----------------------------|--------------------------------------------------------|
| [DynamoDB](#dynamodb)       | `quarkus create app --extensions=jnosql-dynamodb`      |
| [Hazelcast](#hazelcast)     | `quarkus create app --extensions=jnosql-hazelcast`     |
| [Redis](#redis)             | `quarkus create app --extensions=jnosql-redis`         |
| [Infinispan](#infinispan)   | `quarkus create app --extensions=jnosql-infinispan`    |
| [Memcached](#memcached)     | `quarkus create app --extensions=jnosql-memcached`     |
| [Valkey](#valkey)           | `quarkus create app --extensions=jnosql-valkey`        |

### Graph

| Database Vendor         | Command                                                |
|-------------------------|--------------------------------------------------------|
| [Neo4j](#neo4j)         | `quarkus create app --extensions=jnosql-neo4j`         |

### Time Series

| Database Vendor             | Command                                                |
|-----------------------------|--------------------------------------------------------|
| [InfluxDB](#influxdb)       | `quarkus create app --extensions=jnosql-influxdb`      |
| [Apache IoTDB](#apache-iotdb) | `quarkus create app --extensions=jnosql-iotdb`       |
| [QuestDB](#questdb)         | `quarkus create app --extensions=jnosql-questdb`       |

### Multi-model

| Database Vendor               | Command                                                 |
|-------------------------------|---------------------------------------------------------|
| [ArangoDB](#arangodb)         | `quarkus create app --extensions=jnosql-arangodb`       |
| [Oracle NoSQL](#oracle-nosql) | `quarkus create app --extensions=jnosql-oracle-nosql`   |


Or you could create it on by downloading the scaffolding project from the [code.quarkus.io](https://code.quarkus.io/?extension-search=jnosql) and selecting the JNoSQL extension for your desired NoSQL database.

## Manually adding Quarkus JNoSQL Extension to your project

There are a variety of NoSQL databases, each with its own unique features and capabilities. See the section [Supported NoSQL Databases](#supported-nosql-databases), select the one that fits your needs, and then follow the instructions below to manually add the Quarkus JNoSQL Extension to your project.

> :memo: **IMPORTANT:** If you are using **Java 21** or above, make sure to [enable the annotation processor execution](#enabling-the-jnosql-mapping-lite-annotation-processor). If you are using **Java 17** or below, you can skip this step and go ahead to implement your entities and repositories.

## Enabling the JNoSQL Mapping Lite Annotation Processor

The Quarkus JNoSQL Extensions use the annotation processor provided by the `org.eclipse.jnosql.lite:mapping-lite-processor` dependency.

This annotation processor generates the required implementation classes used by CDI during build-time processing and AOT compilation.

Although the processor dependency may be included transitively by a Quarkus JNoSQL extension, declaring it explicitly in the compiler configuration makes the build more predictable. This is especially useful when the project uses explicit annotation processor configuration.

Before setting the processor version, check the latest [org.eclipse.jnosql.lite:mapping-lite-processor` version on Maven Central](https://search.maven.org/artifact/org.eclipse.jnosql.lite/mapping-lite-processor).

To ensure that the annotation processor is executed during the build process, you need to configure your build tool ([Maven](#maven) or [Gradle](#gradle)) accordingly.

### Maven

When using Maven, configure the `maven-compiler-plugin` with the JNoSQL Lite annotation processors explicitly.

The following example enables parameter metadata, sets the Java release version, and registers both the entity and repository processors:

```xml
<project>
  <!-- skipping other elements -->
  <build>
      <plugins>
          <!-- skipping other plugins -->
          <plugin>
              <artifactId>maven-compiler-plugin</artifactId>
              <version>${compiler-plugin.version}</version>
              <configuration>
                  <release>${maven.compiler.release}</release>
                  <parameters>true</parameters>
                  <annotationProcessors>
                      <annotationProcessor>org.eclipse.jnosql.lite.mapping.EntityProcessor</annotationProcessor>
                      <annotationProcessor>org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor</annotationProcessor>
                  </annotationProcessors>
                  <annotationProcessorPaths>
                      <path>
                          <groupId>org.eclipse.jnosql.lite</groupId>
                          <artifactId>mapping-lite-processor</artifactId>
                          <version>${jnosql-lite-processor.version}</version>
                      </path>
                  </annotationProcessorPaths>
              </configuration>
          </plugin>
          <!-- skipping other plugins -->
      </plugins>
  </build>
  <!-- skipping other elements -->
</project>
```

The properties used in this example, such as `${compiler-plugin.version}`, `${maven.compiler.release}`, and `${jnosql-lite-processor.version}`, should be defined according to your project setup.

Replace `${jnosql-lite-processor.version}` with the latest available version from Maven Central when configuring your project.

### Gradle

The target build tool for the Quarkus JNoSQL Extension is Maven. However, if you are using Gradle, you should also make sure that the JNoSQL Lite annotation processor is available during compilation.

The processor is provided by the `org.eclipse.jnosql.lite:mapping-lite-processor` dependency.

Before setting the processor version, check the latest available [`org.eclipse.jnosql.lite:mapping-lite-processor` on Maven Central](https://central.sonatype.com/artifact/org.eclipse.jnosql.lite/mapping-lite-processor).

> :memo: **IMPORTANT:** When using the JNoSQL Lite annotation processor with Gradle, exclude the `jnosql-mapping-reflection` module to prevent CDI bean conflicts. In extension versions **3.4.15 and earlier**, this module is pulled in transitively, causing both the reflection-based and Lite `EntitiesMetadata` implementations to be registered as CDI beans. Without this exclusion, the application fails with an `AmbiguousResolutionException`.
> For additional details and the current status of this issue, see GitHub issue [#423](https://github.com/quarkiverse/quarkus-jnosql/issues/423).

#### Groovy DSL (build.gradle):
```groovy
dependencies {
  annotationProcessor "org.eclipse.jnosql.lite:mapping-lite-processor:${jnosqlLiteProcessorVersion}"
}

// Exclude the reflection-based metadata implementation when using JNoSQL Lite.
// Otherwise, both reflection and Lite implementations become CDI beans,
// resulting in an AmbiguousResolutionException.
configurations.configureEach {
  exclude group: 'org.eclipse.jnosql.mapping', module: 'jnosql-mapping-reflection'
}

tasks.withType(JavaCompile).configureEach {
  options.compilerArgs += [
          "-processor",
          "org.eclipse.jnosql.lite.mapping.EntityProcessor,org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor"
  ]
}
```

#### Kotlin DSL (build.gradle.kts):
```kotlin
dependencies {
  annotationProcessor("org.eclipse.jnosql.lite:mapping-lite-processor:${jnosqlLiteProcessorVersion}")
}

// Exclude the reflection-based metadata implementation when using JNoSQL Lite.
// Otherwise, both reflection and Lite implementations become CDI beans,
// resulting in an AmbiguousResolutionException.
configurations.configureEach {
  exclude(
    group = "org.eclipse.jnosql.mapping",
    module = "jnosql-mapping-reflection"
  )
}

tasks.withType < JavaCompile > ().configureEach {
  options.compilerArgs.addAll(
    listOf(
      "-processor",
      "org.eclipse.jnosql.lite.mapping.EntityProcessor,org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor"
    )
  )
}
```

The `jnosqlLiteProcessorVersion` variable should be defined according to your project setup.

Replace it with the latest available version from Maven Central when configuring your project.

## Using Jakarta NoSQL and Jakarta Data with Quarkus JNoSQL

Once you have added the Quarkus JNoSQL Extension to your project, you can start using it to interact with your NoSQL database. The following steps outline how to create entities and repositories in your Quarkus application:

### Creating entities using JNoSQL annotations

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

### Using Jakarta NoSQL Template

Example using `jakarta.nosql.Template`:

```java
@Inject
// If your NoSQL database supports multiple types,
// you can specify the type using the @Database annotation.

// @Database(DatabaseType.DOCUMENT) for Document databases
// @Database(DatabaseType.COLUMN) for Column databases
// @Database(DatabaseType.GRAPH) for Graph databases
// @Database(DatabaseType.KEY_VALUE) for Key-Value databases
// @Database(DatabaseType.TIMESERIES) for Time Series databases
private Template template;

public void insert(TestEntity entity) {
    template.insert(entity);
}
```

### Using Jakarta Data Repository

For implementations that offer Jakarta Data support, you may create and inject a Jakarta Data Repository:

```java
@Repository
public interface TestEntityRepository extends NoSQLRepository<TestEntity, String> {
}
```

Jakarta Data repositories provide a powerful way to interact with your NoSQL database using a repository pattern.

The interface `org.eclipse.jnosql.mapping.NoSQLRepository` used above extends the `jakarta.data.repository.BasicRepository`, which is a Jakarta Data Repository interface that brings a specialization for NoSQL useful operations, allowing developers to use pre-defined methods.

Also,you can define custom queries using method names or annotations, and the framework will handle the implementation for you. More information about Jakarta Data can be found in the [Jakarta Data Specification](https://jakarta.ee/specifications/data/1.0/).

### Using the Jakarta Data Repositories or Jakarta NoSQL Templates in your service

```java
@ApplicationScope
class TestEntityService {

    @Inject
    // If your NoSQL database supports multiple types,
    // you can specify the type using the @Database annotation.

    // @Database(DatabaseType.DOCUMENT) for Document databases
    // @Database(DatabaseType.COLUMN) for Column databases
    // @Database(DatabaseType.GRAPH) for Graph databases
    // @Database(DatabaseType.KEY_VALUE) for Key-Value databases
    // @Database(DatabaseType.TIMESERIES) for Time Series databases
    Template template;

    @Inject
    // If your NoSQL database supports multiple types,
    // you can specify the type using the @Database annotation.

    // @Database(DatabaseType.DOCUMENT) for Document databases
    // @Database(DatabaseType.COLUMN) for Column databases
    // @Database(DatabaseType.GRAPH) for Graph databases
    // @Database(DatabaseType.KEY_VALUE) for Key-Value databases
    // @Database(DatabaseType.TIMESERIES) for Time Series databases
    TestEntityRepository repository;

    public void insertViaRepository(TestEntity entity) {
        repository.save(entity);
    }

    public void insertViaTemplate(TestEntity entity) {
        template.insert(entity);
    }

}
```

Now, you can use the `TestEntityService` to perform CRUD operations on your `TestEntity` objects using the repository.

That's it! You have successfully set up a Quarkus application with the Quarkus JNoSQL Extension, allowing you to interact with your NoSQL database using Jakarta NoSQL and Jakarta Data.

Next, we provide more instructions for each supported database.

## Document Databases

### MongoDB

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

### CouchDB

<img src="https://www.jnosql.org/img/logos/couchdb.png" alt="CouchDB" align="center" width="25%" height="25%"/>

The [CouchDB](https://couchdb.apache.org/) driver provides an API integration between Java and the database through a
standard communication level.

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

### Elasticsearch

<img src="https://jnosql.github.io/img/logos/elastic.svg" alt="Elasticsearch Project" align="center" width="25%" height="25%"/>

[Elasticsearch](https://www.elastic.co/) is a search engine based on Lucene.  
It provides a distributed, multitenant-capable full-text search engine with an HTTP web interface and schema-free JSON
documents.  
Elasticsearch is developed in Java and is released as open source under the terms of the Apache License. Elasticsearch
is the most popular enterprise search engine followed by Apache Solr, also based on Lucene.

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

### Solr

<img src="https://jnosql.github.io/img/logos/solr.svg" alt="Apache Solr Project" align="center" width="20%" height="20%"/>

[Solr](https://solr.apache.org/) is an open-source enterprise-search platform, written in Java, from the Apache Lucene
project.
Its major features include full-text search, hit highlighting, faceted search, real-time indexing, dynamic clustering,
database integration, NoSQL features and rich document (e.g., Word, PDF) handling.
Providing distributed search and index replication, Solr is designed for scalability and fault tolerance.
Solr is widely used for enterprise search and analytics use cases and has an active development community and regular
releases.

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

## Column Databases

### Cassandra

<img src="https://jnosql.github.io/img/logos/cassandra.png" alt="Apache Cassandra Project" align="center" width="25%" height="25%"/>

[Apache Cassandra](https://cassandra.apache.org/) is a free and open-source distributed database management system
designed to handle large amounts of data across many commodity servers, providing high availability with no single point
of failure.

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

### ScyllaDB

[ScyllaDB](https://www.scylladb.com/) provides the **Column** API, Jakarta NoSQL `Template`,
and generated **Jakarta Data** repositories for POJOs and records.

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-scylladb</artifactId>
</dependency>
```

The extension uses `org.eclipse.jnosql.databases:jnosql-scylladb` at the project's managed
JNoSQL version, currently 1.1.19. This driver depends on `com.scylladb:java-driver-core`
and `com.scylladb:java-driver-query-builder:4.19.2.1`, not the Cassandra Quarkus client.
The ScyllaDB Java driver retains the `com.datastax.oss.driver` Java package names.
Do not add the Cassandra extension/client alongside it: the two clients contain overlapping classes.

Configure the keyspace and native driver's connection settings in `application.properties`:

```properties
jnosql.column.database=developers
jnosql.scylladb.host.1=localhost
jnosql.scylladb.port=9042
jnosql.scylladb.data.center=datacenter1
```

Additional contact points use `jnosql.scylladb.host.2`, `.3`, and so on; the configured port
applies to all of them. The port defaults to `9042` and the local data center to `datacenter1`.
Optional settings are `jnosql.scylladb.name` (application name), `jnosql.scylladb.user`,
and `jnosql.scylladb.password`. Configure the data center to match your cluster.

Provision keyspaces and tables before accessing them. Alternatively, the JNoSQL driver
executes `jnosql.scylladb.query.*` startup statements, sorted lexicographically by key:

```properties
jnosql.scylladb.query.000=CREATE KEYSPACE IF NOT EXISTS developers WITH replication = {'class': 'NetworkTopologyStrategy', 'replication_factor': 1};
jnosql.scylladb.query.001=CREATE TABLE IF NOT EXISTS developers.developer ("_id" text PRIMARY KEY, name text, language text);
```

This single-node replication example is for development; choose production replication
according to your topology. Mapping does not generate the schema. Use the standard JNoSQL
mapping-lite annotation processor described above for entities and Jakarta Data repositories.
This does not imply support for the driver's separate portable-CDI-extension repository API.

The extension delegates connection, query, serialization and retry behavior to JNoSQL and
the ScyllaDB client. CDI shares one configured factory and one Column manager/session;
shutdown closes the managed session. Inject `ScyllaDBColumnManager` with
`@Database(DatabaseType.COLUMN)` for low-level operations. Managers created manually through
`ScyllaDBColumnManagerFactory.apply(keyspace)` must be closed by their caller.

A Java codestart is available with `quarkus create app --extensions=jnosql-scylladb`.
Its tests and the extension tests use the upstream JNoSQL 1.1.19 image
`scylladb/scylla:2026.3.1`, one shard, 1 GB of memory, and Testcontainers' dynamically mapped
host and CQL port. Docker is required; neither test setup substitutes Cassandra for ScyllaDB.

Native compilation and Column, Template and repository CRUD have been verified with
Mandrel 25.0.4.1 in a Linux container. The extension uses the ScyllaDB client's bundled
native metadata and defers `MetadataManager` initialization so its default contact point
is resolved at runtime. It does not copy Cassandra-specific native configuration.

## Key-Value Databases

### DynamoDB

<img src="https://user-images.githubusercontent.com/6509926/70553550-f033b980-1b40-11ea-9192-759b3b1053b3.png" align="center" width="25%" height="25%"/>

[Amazon DynamoDB](https://aws.amazon.com/dynamodb/) is a fully managed, serverless, key-value and document NoSQL
database designed to run high-performance applications at any scale. DynamoDB offers built-in security, continuous
backups, automated multi-Region replication, in-memory caching, and data import and export tools.

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

### Hazelcast

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

### Redis

<img src="https://www.jnosql.org/img/logos/redis.png" alt="Redis Project" align="center" width="25%" height="25%"/>

[Redis](https://redis.io/) is an open source, in-memory data structure store used as a database, cache, and message
broker.

This driver provides support for the *Key-Value* NoSQL API.

The extension uses the Jedis-based JNoSQL Redis driver directly, sharing a single connection pool across the whole
application.

Add the Redis dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-redis</artifactId>
</dependency>
```

To define the **Key-Value** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.keyvalue.database=my-database-name
```

The connection and the connection pool can be configured through the `jnosql.redis.*` properties, for example:

```properties
jnosql.redis.host=localhost
jnosql.redis.port=6379
jnosql.redis.max.total=50
```

Please refer to
the [JNoSQL Redis driver](https://github.com/eclipse-jnosql/jnosql-databases/?tab=readme-ov-file#redis) for the
complete list of configuration properties.

### Infinispan

[Infinispan](https://infinispan.org/) supports the JNoSQL **Key-Value** API through
`org.eclipse.jnosql.databases:jnosql-infinispan`. The extension provides CDI injection of
`BucketManager`, `BucketManagerFactory`, and Jakarta NoSQL `Template`, including POJO and record mapping.
Jakarta Data repositories are not supported by this Key-Value integration.

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-infinispan</artifactId>
</dependency>
```

For an embedded, local cache, add these settings to `application.properties`:

```properties
jnosql.keyvalue.database=people
jnosql.infinispan.config=infinispan.xml
```

Create `src/main/resources/infinispan.xml` with a cache whose name matches the database:

```xml
<infinispan xmlns="urn:infinispan:config:16.0">
    <cache-container name="jnosql">
        <local-cache name="people"/>
    </cache-container>
</infinispan>
```

The codestart includes this configuration and runs without an external server or Docker.
Infinispan loads the XML from the classpath first, then from the filesystem; an absolute path such as
`jnosql.infinispan.config=/etc/my-app/infinispan.xml` can be used for external configuration.
Missing or invalid XML fails instead of silently creating a default cache.
No Quarkus-specific XML parser or cache implementation is introduced.

The property is passed to the driver's actual configuration API:

```java
Settings settings = Settings.builder()
        .put(InfinispanConfigurations.CONFIG, "infinispan.xml")
        .build();
```

Here `Settings` is `org.eclipse.jnosql.communication.Settings` and `InfinispanConfigurations` is
`org.eclipse.jnosql.databases.infinispan.communication.InfinispanConfigurations`.
For normal Quarkus use, configure properties and inject the manager or template rather than creating another factory.

The driver's `jnosql.infinispan.host.*` (or generic `jnosql.host.*`) settings select Hot Rod and
**take precedence over XML**. Do not set host properties for local XML mode.
Without hosts or XML, the driver creates its default embedded container; it does not define the application's named caches.
Remote mode is delegated unchanged to JNoSQL, but is not covered by this extension's local-cache tests.

Embedded core is aligned with the Quarkus-managed Infinispan libraries (16.0.15), and the driver's obsolete
shaded `infinispan-embedded` 9.x dependency is excluded. The extension shares one driver factory.
It delegates shutdown to `factory.close()`, which is a **no-op in JNoSQL 1.1.19**: embedded container shutdown
and dev-mode reload cleanup remain upstream limitations. Native-image support is not verified.

See the [JNoSQL Infinispan driver](https://github.com/eclipse-jnosql/jnosql-databases/tree/1.1.19/jnosql-infinispan)
for the underlying configuration API.

### Memcached

[Memcached](https://memcached.org/) is an in-memory, cache-oriented **Key-Value** store.
The extension uses `org.eclipse.jnosql.databases:jnosql-memcached` and the existing JNoSQL
`BucketManager` and Jakarta NoSQL `Template` programming model.

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-memcached</artifactId>
</dependency>
```

A Java codestart is provided:

```bash
quarkus create app --extensions=jnosql-memcached
```

Configure a logical bucket name and the numbered host list in `application.properties`:

```properties
jnosql.keyvalue.database=people
jnosql.memcached.host.1=localhost:11211
```

Additional servers use `jnosql.memcached.host.2`, `.3`, and so on, each with a `host:port` value.
These are the host-prefix entries read by the JNoSQL driver's `MemcachedConfigurations.HOST`.
The bucket name namespaces keys; it does not create a durable database.
The codestart tests use `memcached:latest`, port `11211`, and Testcontainers' default wait strategy,
overriding the host entry with the container's dynamically mapped address.

The driver's default object serialization requires stored entities to implement `java.io.Serializable`.
The extension adapts the client's deserialization class lookup to Quarkus's application class loader;
the driver's serialization format is unchanged.
Use key-based store, retrieve, and delete operations; do not assume durable persistence,
queries, ordering, secondary indexes, or transactions. Jakarta Data repositories are not provided
by this extension.

Native compilation and entity CRUD through `Template` and `BucketManager` have been verified
with Mandrel 25.0.4.1 in a Linux container. The extension includes spymemcached's optional
`com.codahale.metrics:metrics-core:3.0.1` dependency for native linking and initializes its
random-seed holder at runtime.

The extension registers entity hierarchies, `String`, and `ArrayList` for native Java serialization.
Additional concrete types stored through `BucketManager`, or used in polymorphic fields and
other collection implementations, may require explicit
`@RegisterForReflection(serialization = true, targets = {...})` registration.

**Driver limitation:** in JNoSQL 1.1.18, the Memcached factory's `close()` is a no-op.
The extension shares one factory and delegates shutdown to the driver, but the driver does not
shut down its client. This can retain client threads across application reloads.

### Valkey

[Valkey](https://valkey.io/) is an open source, in-memory data structure store used as a database, cache, and message
broker. It is wire-compatible with Redis.

This driver provides support for the *Key-Value* NoSQL API.

The extension uses the JNoSQL Valkey driver directly, sharing a single connection pool across the whole application.

Add the Valkey dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-valkey</artifactId>
</dependency>
```

To define the **Key-Value** database's name, you need to add the following info in your `application.properties`:

```properties
jnosql.keyvalue.database=my-database-name
```

The connection and the connection pool can be configured through the `jnosql.valkey.*` properties, for example:

```properties
jnosql.valkey.host=localhost
jnosql.valkey.port=6379
jnosql.valkey.max.total=50
```

Please refer to
the [JNoSQL Valkey driver](https://github.com/eclipse-jnosql/jnosql-databases/?tab=readme-ov-file#valkey) for the
complete list of configuration properties.

## Graph Databases

### Neo4j

<img src="https://jnosql.github.io/img/logos/neo4j.png" alt="Neo4J Project" align="center" width="25%" height="25%"/>

[Neo4J](https://neo4j.com/) is a highly scalable, native graph database designed to manage complex relationships in
data. It enables developers to build applications that leverage the power of graph traversal, pattern matching, and
high-performance querying using the **Cypher** query language.

This API provides support for **Graph** database operations, including entity persistence, query execution via Cypher,
and relationship traversal.

:information_source: This extension is using the **org.eclipse.jnosql.databases:jnosql-neo4j:1.1.7-SNAPSHOT**

Add the Quarkus JNoSQL Neo4j dependency to your project's `pom.xml`:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-neo4j</artifactId>
</dependency>
```

Now, you can use the `org.eclipse.jnosql.mapping.graph.GraphTemplate`, a `jakarta.nosql.Template` specialized interface,
to perform CRUD operations on your entities.

* Here's an example of how to use the `jakarta.nosql.Template`:

  ```java
  @Inject
  @Database(DatabaseType.GRAPH)
  protected GraphTemplate template;

  public void insert(TestEntity entity) {
   template.insert(entity);
  }
  ```

For specific configuration details, please refer to
the [Quarkus Neo4j extension](https://docs.quarkiverse.io/quarkus-neo4j/dev/index.html).

## Time Series Databases

### InfluxDB

InfluxDB is a time-series database designed to store and query data that changes over time.

This extension provides support for the Eclipse JNoSQL *Time Series* Mapping API and **Jakarta Data**.

Add the InfluxDB dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-influxdb</artifactId>
</dependency>
```

Configure the Time Series database and InfluxDB connection in `application.properties`:

```properties
jnosql.timeseries.database=metrics
jnosql.influxdb.url=http://localhost:8181
jnosql.influxdb.token=jnosql-influxdb-test-token
```

### Apache IoTDB

Apache IoTDB is a time-series database designed for IoT data management and analysis.

This extension provides support for the Eclipse JNoSQL *Time Series* Mapping API and **Jakarta Data**.

Add the Apache IoTDB dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-iotdb</artifactId>
</dependency>
```

Configure the Time Series database and IoTDB connection in `application.properties`:

```properties
jnosql.timeseries.database=jnosql
jnosql.iotdb.host=localhost
jnosql.iotdb.port=6667
jnosql.iotdb.username=root
jnosql.iotdb.password=root
jnosql.iotdb.enable.redirection=false
```

### QuestDB

QuestDB is a high-performance time-series database designed for fast ingestion and SQL analytics over time-oriented data.

This extension provides support for the Eclipse JNoSQL *Time Series* Mapping API and **Jakarta Data**.

Add the QuestDB dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-questdb</artifactId>
</dependency>
```

Configure the Time Series database and QuestDB connection in `application.properties`:

```properties
jnosql.timeseries.database=qdb
jnosql.questdb.url=ws::addr=localhost:9000;
```

## Multi-model Databases

### ArangoDB

<img src="https://jnosql.github.io/img/logos/ArangoDB.png" alt="ArangoDB Project" align="center" width="25%" height="25%" />

[ArangoDB](https://www.arangodb.com/) is a native multi-model database with flexible data models for documents, graphs,
and key-values.
Build high performance applications using a convenient SQL-like query language or JavaScript extensions.

This extension offers support for **Document** and **Key-Value** types. Also, it provides support for **Jakarta Data**
for Document NoSQL Entities.

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

### Oracle NoSQL

<img src="https://jnosql.github.io/img/logos/oracle.png" alt="Oracle NoSQL Project" align="center" width="25%" height="25%"/>

[Oracle NoSQL Database](https://www.oracle.com/database/nosql/technologies/nosql/) is a versatile multi-model database
offering flexible data models for documents, graphs, and key-value pairs. It empowers developers to build
high-performance applications using a user-friendly SQL-like query language or JavaScript extensions.

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
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/amoscatelli"><img src="https://avatars.githubusercontent.com/u/16684470?v=4?s=100" width="100px;" alt="amoscatelli"/><br /><sub><b>amoscatelli</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=amoscatelli" title="Code">💻</a> <a href="#maintenance-amoscatelli" title="Maintenance">🚧</a> <a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=amoscatelli" title="Documentation">📖</a></td>
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
