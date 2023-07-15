# Quarkus JNoSQL

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.jnosql/quarkus-jnosql-core?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.jnosql/quarkus-jnosql-core)

The Quarkus JNoSql Extension adds support for
[JNoSQL](http://www.jnosql.org/), an implementation of [Jakarta NoSQL](https://jakarta.ee/specifications/nosql/).

:information_source: **Recommended Quarkus version: `3.1.0.Final` or higher**

### Jakarta NoSQL Entity Mapping Sample

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

### Required Configuration

The JNoSql KeyValue Quarkus extension supports the following configuration:

| Name                       | Description         | Type   | Default |
|----------------------------|---------------------|--------|---------|
| `jnosql.keyvalue.database` | The database's name | string |         |

### Usage

```java
@Inject
private KeyValueTemplate template;

public void insert(TestEntity entity){
        template.insert(entity);
        }
```

### ArangoDB

Add the dependency to the target project:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-keyvalue-arangodb</artifactId>
</dependency>
```

Please refer to the [AranboDB JNoSQL driver](https://github.com/eclipse/jnosql-databases#arangodb) for specific
configuration.

### DynamoDB

Add the dependency to the target project:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-keyvalue-dynamodb</artifactId>
</dependency>
```

Please refer to
the [DynamoDB Quarkiverse extension](https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/amazon-dynamodb.html)
for specific configuration.

### Redis

Add the dependency to the target project:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-keyvalue-redis</artifactId>
</dependency>
```

Please refer to the [Redis Quarkus extension](https://quarkus.io/guides/redis-reference) for specific configuration.

### Couchbase

Add the dependency to the target project:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-keyvalue-couchbase</artifactId>
</dependency>
```

#### Required Configuration

This JNoSql KeyValue Quarkus extension for Couchbase supports the following configuration:

| Name                        | Description           | Type   | Default |
|-----------------------------|-----------------------|--------|---------|
| `jnosql.couchbase.host`     | the connection string | string |         |
| `jnosql.couchbase.user`     | The username [^0]     | string |         |
| `jnosql.couchbase.password` | The password [^0]     | string |         |
| `jnosql.couchbase.password` | The password [^0]     | string |         |

Please, take a look at the [Eclipse JNoSQL Database API for Couchbase documentation](https://github.com/eclipse/jnosql-databases#configuration-2) to learn more about all supported properties.

## Document

### Configuration

The JNoSql Document Quarkus extension supports the following configuration:

| Name                                               | Type   | Default |
 |----------------------------------------------------|--------|---------|
| `jnosql.document.database`<br>The database's name. | string |         |

### Usage

```java
@Inject
private DocumentTemplate template;

public void insert(TestDocumentEntity entity){
        template.insert(entity);
        }
```

### ArangoDB

Add the dependency to the target project:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-arangodb</artifactId>
</dependency>
```

Please refer to the [AranboDB JNoSQL driver](https://github.com/eclipse/jnosql-databases#arangodb) for specific
configuration.

### Elasticsearch

Add the dependency to the target project:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-elasticsearch</artifactId>
</dependency>
```

Please refer to
the [Elasticsearch Quarkus extension](https://quarkus.io/guides/elasticsearch#using-the-elasticsearch-java-client) for
specific configuration.

### MongoDB

Add the dependency to the target project:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-mongodb</artifactId>
</dependency>
```

Please refer to the [MongoDB Quarkus extension](https://quarkus.io/guides/mongodb) for specific configuration.

## Column

### Configuration

The JNoSql Column Quarkus extension supports the following configuration:

| Name                                             | Type   | Default |
 |--------------------------------------------------|--------|---------|
| `jnosql.column.database`<br>The database's name. | string |         |

### Cassandra

Add the dependency to the target project:

```xml

<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-column-cassandra</artifactId>
</dependency>
```

Please refer to the [Cassandra Quarkus extension](https://quarkus.io/guides/cassandra) for specific configuration.

## Contributors ✨____

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):
<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/amoscatelli"><img src="https://avatars.githubusercontent.com/u/16684470?v=4?s=100" width="100px;" alt="amoscatelli"/><br /><sub><b>amoscatelli</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=amoscatelli" title="Code">💻</a> <a href="#maintenance-amoscatelli" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/dearrudam"><img src="https://avatars.githubusercontent.com/u/6537623?v=4" width="100px;" alt="dearrudam"/><br /><sub><b>dearrudam</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=dearrudam" title="Code">💻</a> <a href="#maintenance-dearrudam" title="Maintenance">🚧</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification.
Contributions of any kind welcome!

[^0]: Pay attention to the way the sensitive variables are stored in order to avoid data leakage and security flaws.