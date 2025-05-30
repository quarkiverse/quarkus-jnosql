package ilove.quark.us;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public record Room(
        @Id
        String number,
        @Column
        Guest guest
) {
}
