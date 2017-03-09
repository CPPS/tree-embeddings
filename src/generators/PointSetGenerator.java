package generators;

import geometry.Point;

import java.util.Collection;
import java.util.Iterator;

public interface PointSetGenerator {
    /**
     * @return An iterator of which every returned value is a point set.
     *         Elements returned by this iterator may become invalid after the
     *         next {@link Iterator#next()} call.
     */
    Iterator<Collection<Point>> generate();
}
