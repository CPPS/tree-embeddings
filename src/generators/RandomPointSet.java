package generators;

import geometry.Point;
import math.Interval;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Preconditions;

public class RandomPointSet implements PointSet {
    protected final Random random = new Random();

    protected int n;
    protected final Interval xRange;
    protected final Interval yRange;

    public RandomPointSet(int n, int xMin, int xMax, int yMin, int yMax) {
        this(n, new Interval(xMin, xMax), new Interval(yMin, yMax));
    }

    public RandomPointSet(int n, Interval xRange, Interval yRange) {
        Preconditions.checkArgument(xRange.getLength() >= n);
        Preconditions.checkArgument(yRange.getLength() >= n);

        this.n = n;
        this.xRange = xRange;
        this.yRange = yRange;
    }

    protected int randomCoordinate(Interval range) {
        return random.nextInt(range.getLength()) + range.getMin();
    }

    protected Set<Integer> generateCoordinates(Interval range) {
        Set<Integer> coordinates = new HashSet<>();

        while (coordinates.size() < n) {
            int coordinate = randomCoordinate(range);
            coordinates.add(coordinate);
        }

        return coordinates;
    }

    @Override
    public Set<Point> generate() {
        Set<Point> points = new HashSet<>();
        Set<Integer> x_coordinates = generateCoordinates(xRange);
        Set<Integer> y_coordinates = generateCoordinates(yRange);

        Iterator<Integer> x_generator = x_coordinates.iterator();
        Iterator<Integer> y_generator = y_coordinates.iterator();

        for (int i = 0; i < n; i++) {
            int x = x_generator.next();
            int y = y_generator.next();

            Point point = new Point(x, y);
            points.add(point);
        }

        return points;
    }
}
