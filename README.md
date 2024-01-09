# Quarkus JNoSQL
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

This documentation provides instructions on how to integrate JNoSQL, an implementation of [Jakarta NoSQL](https://jakarta.ee/specifications/nosql/), into a Quarkus project using the Quarkus JNoSQL Extension. This extension supports JNoSQL and facilitates using NoSQL databases in your Quarkus applications.


:information_source: **Recommended Quarkus version: `3.2.2.Final` or higher**

## Getting Started

To begin using JNoSQL with Quarkus, follow these steps:

1. Add the Quarkus JNoSQL Extension to your project's dependencies. You can find the latest version on Maven Central:

   [![Version](https://img.shields.io/maven-central/v/io.quarkiverse.jnosql/quarkus-jnosql-core?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.jnosql/quarkus-jnosql-core)

2. Define your entities using JNoSQL annotations. Here's an example entity class:

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
   }
   ```

## KeyValue

### Configuration

Configure the JNoSql KeyValue Quarkus extension by specifying the database's name in your `application.properties`:

```properties
jnosql.keyvalue.database=my-database-name
```

### Usage

Inject the `KeyValueTemplate` into your class and use it to interact with the KeyValue database:

```java
import jakarta.inject.Inject;
import org.jnosql.artemis.key.KeyValueTemplate;

@Inject
private KeyValueTemplate template;

public void insert(TestEntity entity) {
    template.insert(entity);
}
```

### Database Specific Configuration

#### ArangoDB

Add the ArangoDB dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-keyvalue-arangodb</artifactId>
</dependency>
```

For specific configuration details, please refer to the [AranboDB JNoSQL driver](https://github.com/eclipse/jnosql-databases#arangodb).

#### DynamoDB

Add the DynamoDB dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-keyvalue-dynamodb</artifactId>
</dependency>
```

Please refer to the [DynamoDB Quarkiverse extension](https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/amazon-dynamodb.html) for specific configuration details.

#### Hazelcast

Add the Hazelcast dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-keyvalue-hazelcast</artifactId>
</dependency>
```

Please refer to the [Quarkus Hazelcast extension](https://github.com/hazelcast/quarkus-hazelcast-client) for specific configuration details.

#### Redis

Add the Redis dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-keyvalue-redis</artifactId>
</dependency>
```

For specific configuration details, please refer to the [Redis Quarkus extension](https://quarkus.io/guides/redis-reference).

## Document

### Configuration

Configure the JNoSql Document Quarkus extension by specifying the database's name in your `application.properties`:

```properties
jnosql.document.database=my-database-name
```

### Usage

Inject the `DocumentTemplate` into your class and use it to interact with the Document database:

```java
import jakarta.inject.Inject;
import org.jnosql.artemis.document.DocumentTemplate;

@Inject
private DocumentTemplate template;

public void insert(TestDocumentEntity entity) {
    template.insert(entity);
}
```

### Database Specific Configuration

#### ArangoDB

Add the ArangoDB dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-arangodb</artifactId>
</dependency>
```

For specific configuration details, please refer to the [AranboDB JNoSQL driver](https://github.com/eclipse/jnosql-databases#arangodb).

#### CouchDB

Add the CouchDB dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-couchdb</artifactId>
</dependency>
```

For specific configuration details, please refer to the [CouchDB JNoSQL driver](https://github.com/eclipse/jnosql-databases#couchdb).

#### Elasticsearch

Add the Elasticsearch dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-elasticsearch</artifactId>
</dependency>
```

Please refer to the [Elasticsearch Quarkus extension](https://quarkus.io/guides/elasticsearch#using-the-elasticsearch-java-client) for specific configuration details.

#### MongoDB

Add the MongoDB dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-mongodb</artifactId>
</dependency>
```

For specific configuration details, please refer to the [MongoDB Quarkus extension](https://quarkus.io/guides/mongodb).

#### Solr

Add the Solr dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-solr</artifactId>
</dependency>
```

For specific configuration details, please refer to the [Solr JNoSQL driver](https://github.com/eclipse/jnosql-databases#solr).

## Column

### Configuration

Configure the JNoSql Column Quarkus extension by specifying the database's name in your `application.properties`:

```properties
jnosql.column.database=my-database-name
```

### Usage

Inject the `ColumnTemplate` into your class and use it to interact with the Column database:

```java
import jakarta.inject.Inject;
import org.jnosql.artemis.column.ColumnTemplate;

@Inject
private ColumnTemplate template;

public void insert(TestColumnEntity entity) {
    template.insert(entity);
}
```

### Database Specific Configuration

#### Cassandra

Add the Cassandra dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.j

nosql</groupId>
    <artifactId>quarkus-jnosql-column-cassandra</artifactId>
</dependency>
```

Please refer to the [Cassandra Quarkus extension](https://quarkus.io/guides/cassandra) for specific configuration details.

## Contributors ✨

Thanks to these wonderful people for their contributions:

- [amoscatelli](https://github.com/amoscatelli) 💻 🚧
- [dearrudam](https://github.com/dearrudam) 💻 🚧

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind are welcome!