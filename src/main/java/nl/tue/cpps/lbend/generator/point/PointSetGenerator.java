package nl.tue.cpps.lbend.generator.point;

import java.util.Iterator;
import java.util.List;

import nl.tue.cpps.lbend.geometry.Point;

public interface PointSetGenerator {
    /**
     * @return An iterator of which every returned value is a point set.
     *         Elements returned by this iterator may become invalid after the
     *         next {@link Iterator#next()} call.
     */
    Iterator<List<Point>> generate();

    List<Iterator<List<Point>>> splitGenerator(int nThreads);
}
