package nl.tue.cpps.lbend.geometry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class FixedPoint extends AbstractPoint {
    private final int x, y;

    public static FixedPoint of(Point p) {
        if (p instanceof FixedPoint) {
            return (FixedPoint) p;
        } else {
            return new FixedPoint(p.getX(), p.getY());
        }
    }
}
