package geometry;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class FixedPoint implements Point {
    private final int x, y;

    public static FixedPoint of(Point p) {
        if (p instanceof FixedPoint) {
            return (FixedPoint) p;
        } else {
            return new FixedPoint(p.getX(), p.getY());
        }
    }
}
