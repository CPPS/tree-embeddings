package nl.tue.cpps.lbend.generator.point;

import nl.tue.cpps.lbend.geometry.FixedPoint;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.math.Interval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.IntSupplier;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.koloboke.collect.set.hash.HashIntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class RandomPointGenerator implements PointSetGenerator {
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
    public Iterator<List<Point>> generate() {
        IntSupplier xGenerator = generateCoordinates(xRange);
        IntSupplier yGenerator = generateCoordinates(yRange);

        List<Point> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int x = xGenerator.getAsInt();
            int y = yGenerator.getAsInt();

            out.add(new FixedPoint(x, y));
        }

        return ImmutableList.of(out).iterator();
    }

    @Override
    public List<Iterator<List<Point>>> splitGenerator(int nThreads) {
        throw new UnsupportedOperationException();
    }
}
