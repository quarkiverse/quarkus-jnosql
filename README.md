# Quarkus Jnosql

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.jnosql/quarkus-jnosql?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.jnosql/quarkus-jnosql)

The Quarkus JNoSql Extension adds support for 
[JNoSql](http://www.jnosql.org/), an implementation of [Jakarta NoSql](https://jakarta.ee/specifications/nosql/).

## Document

### Configuration

The JNoSql Document Quarkus extension supports the following configuration:

 | Name  | Type  | Default  |
 |---|---|---|
 | `jnosql.document.database`<br>The collection's name. | string  | |

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
  <tr>
    <td align="center"><a href="https://www.linkedin.com/in/alessandromoscatelli/"><img src="https://avatars.githubusercontent.com/amoscatelli" width="100px;" alt=""/><br /><sub><b>Alessandro Moscatelli</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkiverse-jberet/commits?author=amoscatelli" title="Code">💻</a> <a title="Maintenance">🚧</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!