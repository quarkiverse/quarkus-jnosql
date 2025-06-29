package org.acme;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.By;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;

import java.util.List;

/**
 * Repository interface for managing Magazine entities.
 * This interface extends BasicRepository to provide basic CRUD operations
 * and custom query methods for Magazine entities.
 */
@Repository
public interface MagazineRepository extends BasicRepository<Magazine, String> {

    @Find
    List<Magazine> findByTitle(@By("title") String title);

}