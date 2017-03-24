package nl.tue.cpps.lbend.generator.point;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import nl.tue.cpps.lbend.generator.IntQuickPerm;
import nl.tue.cpps.lbend.geometry.MutablePoint;
import nl.tue.cpps.lbend.geometry.Point;

@RequiredArgsConstructor
public class PermutedPointGenerator implements PointSetGenerator {
    private final int n;

    @Override
    public Iterator<List<Point>> generate() {
        // Array of Y coordinates
        // Points are represented as [x] = y
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            y[i] = i;
        }

        IntQuickPerm Q = new IntQuickPerm(y);

        // Prepare output list
        MutablePoint[] points = new MutablePoint[n];
        for (int x = 0; x < n; x++) {
            points[x] = new MutablePoint(x, -1);
        }
        List<Point> out = Collections.unmodifiableList(Arrays.asList(points));

        return new Iterator<List<Point>>() {
            @Override
            public boolean hasNext() {
                return Q.hasNext();
            }

            @Override
            public List<Point> next() {
                Q.next();

                // Transform the output
                for (int x = 0; x < n; x++) {
                    points[x].setY(y[x]);
                }

                return out;
            }
        };
    }
}