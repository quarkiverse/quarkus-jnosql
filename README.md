# Quarkus JNoSQL
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.jnosql/quarkus-jnosql?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.jnosql/quarkus-jnosql)

The Quarkus JNoSql Extension adds support for 
[JNoSQL](http://www.jnosql.org/), an implementation of [Jakarta NoSQL](https://jakarta.ee/specifications/nosql/).

:information_source: **Recommended Quarkus version: `3.0.0.Alpha4` or higher**

## Document

### Configuration

The JNoSql Document Quarkus extension supports the following configuration:

 | Name  | Type  | Default  |
 |---|---|---|
 | `jnosql.document.database`<br>The database's name. | string  | |

### Usage

```java
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class TestDocumentEntity {
    
    @Id
    private String id;
    
    @Column
    private String testField;
}
```

```java
@Inject
private DocumentTemplate template;

public void insert(TestDocumentEntity entity) {
    template.insert(entity);
}
```

### MongoDB

Add the dependency to the target project:

```xml
<dependency>
    <groupId>io.quarkiverse.jnosql</groupId>
    <artifactId>quarkus-jnosql-document-mongodb</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Please refer to the [MongoDB Quarkus extension](https://quarkus.io/guides/mongodb) for specific configuration.

## Contributors ✨____

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):
<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/amoscatelli"><img src="https://avatars.githubusercontent.com/u/16684470?v=4?s=100" width="100px;" alt="amoscatelli"/><br /><sub><b>amoscatelli</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jnosql/commits?author=amoscatelli" title="Code">💻</a> <a href="#maintenance-amoscatelli" title="Maintenance">🚧</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
