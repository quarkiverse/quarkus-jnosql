package ilove.quark.us;

import jakarta.nosql.Column;
import jakarta.nosql.Embeddable;

/**
 * Represents a guest with a document and name.
 * This class is used as an embeddable object in the Room entity.
 *
 * <p>Eclipse JNoSQL supports Java Records as embeddable object,
 * but you can use regular classes as well. The choice depends on your
 * application's requirements and design preferences.</p>
 *
 * <p>Note: The @Column annotation is used to specify that the fields
 * should be mapped to columns in the NoSQL database.</p>
 */
@Embeddable
public record Guest(
        @Column String document,
        @Column String name) {
}

