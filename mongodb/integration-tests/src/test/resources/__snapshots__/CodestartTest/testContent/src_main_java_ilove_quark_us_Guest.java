package ilove.quark.us;

import jakarta.nosql.Column;
import jakarta.nosql.Embeddable;

@Embeddable
public record Guest(
        @Column String document,
        @Column String name) {
}

