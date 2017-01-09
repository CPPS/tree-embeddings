package generators;

import geometry.Point;
import math.Interval;

import java.util.Iterator;
import java.util.Random;
import java.util.function.IntSupplier;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.koloboke.collect.set.hash.HashIntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class RandomPointGenerator implements PointGenerator {
    private final Random random = new Random();

    private final int n;
    private final @Nonnull Interval xRange;
    private final @Nonnull Interval yRange;

    public RandomPointGenerator(int n, Interval xRange, Interval yRange) {
        Preconditions.checkArgument(xRange.getLength() >= n);
        Preconditions.checkArgument(yRange.getLength() >= n);

        this.n = n;
        this.xRange = xRange;
        this.yRange = yRange;
    }

    protected int randomCoordinate(Interval range) {
        return random.nextInt(range.getLength()) + range.getMin();
    }

    protected IntSupplier generateCoordinates(Interval range) {
        HashIntSet coordinates = HashIntSets.newMutableSet(n);

        return () -> {
            int coordinate;
            do {
                // Find a random coordinate that has not been generated before
                coordinate = randomCoordinate(range);
            } while (!coordinates.add(coordinate));
            return coordinate;
        };
    }

    @Override
    public Iterator<Point> generate() {
        IntSupplier xGenerator = generateCoordinates(xRange);
        IntSupplier yGenerator = generateCoordinates(yRange);

        return new Iterator<Point>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < n;
            }

            @Override
            public Point next() {
                int x = xGenerator.getAsInt();
                int y = yGenerator.getAsInt();

                i++;
                return new Point(x, y);
            }
        };
    }
}
