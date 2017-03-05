package geometry;

import lombok.Data;

import java.util.*;

/**
 * Created by s148327 on 5-3-2017.
 */
@Data
public class LBend {

    private final Line horizontal, vertical;

    public boolean intersectsWith(LBend other) {
        return MappingValidator2SAT.intersects(horizontal, other.vertical)
                || MappingValidator2SAT.intersects(other.horizontal, vertical);
    }

    public static LBend getBend(Point p1, Point p2, boolean complement) {

        // |- false
        // -| true

        Line horizontal = complement
                ? new Line(p1, new Point(p2.getX(), p1.getY()))
                : new Line(p2, new Point(p1.getX(), p2.getY()));

        Line vertical = complement
                ? new Line(p2, new Point(p2.getX(), p1.getY()))
                : new Line(p1, new Point(p1.getX(), p2.getY()));

        return new LBend(horizontal, vertical);
    }
}