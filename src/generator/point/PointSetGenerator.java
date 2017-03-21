package generator.point;

import geometry.Point;

import java.util.Iterator;
import java.util.List;

public interface PointSetGenerator {
    /**
     * @return An iterator of which every returned value is a point set.
     *         Elements returned by this iterator may become invalid after the
     *         next {@link Iterator#next()} call.
     */
    Iterator<List<Point>> generate();
}
