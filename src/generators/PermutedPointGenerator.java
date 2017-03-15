package generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import geometry.Point;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PermutedPointGenerator implements PointSetGenerator {
    private final int n;

    @Override
    public Iterator<Collection<Point>> generate() {
        // Array of Y coordinates
        // Points are represented as [x] = y
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            y[i] = i;
        }

        IntQuickPerm Q = new IntQuickPerm(y);
        List<Point> points = new ArrayList<>(n);

        return new Iterator<Collection<Point>>() {
            @Override
            public boolean hasNext() {
                return Q.hasNext();
            }

            @Override
            public Collection<Point> next() {
                Q.next();

                // Transform the output
                points.clear();
                for (int x = 0; x < n; x++) {
                    points.add(new Point(x, y[x]));
                }

                return points;
            }
        };
    }
}
