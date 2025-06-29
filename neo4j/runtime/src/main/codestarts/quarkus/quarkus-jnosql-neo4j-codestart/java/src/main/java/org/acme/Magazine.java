package org.acme;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

/**
 * A magazine entity that represents a magazine in the Graph database.
 * @param id
 * @param title
 * @param edition
 */
@Entity
public record Magazine(
        @Id String id,
        @Column("title") String title,
        @Column("edition") int edition) {
}