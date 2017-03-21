package geometry;

import lombok.Data;

@Data
public final class FixedPoint implements Point {
    private final int x, y;
}
