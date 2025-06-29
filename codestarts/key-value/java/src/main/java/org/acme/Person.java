package org.acme;

import java.util.List;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;


/**
 * Represents a person with an id, name, and phone numbers.
 * This class is used as an entity in the NoSQL database.
 *
 * <p>Eclipse JNoSQL supports Java Records as entities, but you can
 * use regular classes as well. The choice depends on your
 * application's requirements and design preferences.</p>
 *
 * <p>Note: The &#64Id annotation is used to specify which field
 * should be used as the identifier for the entity, and the
 * &#64Column annotation is used to specify that the fields
 * should be mapped to columns in the NoSQL database.</p>
 *
 * @param id
 * @param name
 * @param phones
 */

@Entity
public record Person(
        @Id String id,
        @Column String name,
        /**
         * Java Records are immutable by default, but we
         * should be careful with mutable attributes usage
         */
        @Column List<String> phones
) {
}