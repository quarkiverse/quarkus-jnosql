package ilove.quark.us;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

/**
 * Represents a room with a number and an embedded guest entity.
 * This class is used as an entity in the NoSQL database.
 *
 * <p>Eclipse JNoSQL supports Java Records as entities, but you can
 * use regular classes as well. The choice depends on your
 * application's requirements and design preferences.</p>
 * </p>
 * <p></p>
 * <p>Note: The &#64Id annotation is used to specify which field
 * should be used as the identifier for the entity, and the
 * &#64Column annotation is used to specify that the fields
 * should be mapped to columns in the NoSQL database.</p>
 */
@Entity
public record Room(
        @Id
        String number,
        @Column
        Guest guest
) {
}
