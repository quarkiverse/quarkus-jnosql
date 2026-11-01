package io.quarkiverse.jnosql.milvus.it;

import java.util.List;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;

@Repository
public interface ArticleRepository extends BasicRepository<Article, String> {

    List<Article> findByCategory(String category);
}
