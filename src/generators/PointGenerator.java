package generators;

import geometry.Point;

import java.util.Iterator;

public interface PointGenerator {
    Iterator<Point> generate();
}
