package org.acme;

import java.util.List;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;

/**
 * Repository interface for managing Article entities.
 * It extends BasicRepository for the basic operations and adds a derived query.
 */
@Repository
public interface ArticleRepository extends BasicRepository<Article, String> {

    List<Article> findByCategory(String category);
}
