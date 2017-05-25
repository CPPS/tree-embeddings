package nl.tue.cpps.lbend.generator.point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.AbstractIterator;

import lombok.RequiredArgsConstructor;
import nl.tue.cpps.lbend.generator.IntQuickPerm;
import nl.tue.cpps.lbend.geometry.MutablePoint;
import nl.tue.cpps.lbend.geometry.Point;

@RequiredArgsConstructor
public class PermutedPointGenerator implements PointSetGenerator {
    public static interface PointGenerator extends Iterator<List<Point>> {
        IntQuickPerm getPermuter();
    }

    private final int n;

    @Override
    public PointGenerator generate() {
        // Array of Y coordinates
        // Points are represented as [x] = y
        final int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            y[i] = i;
        }

        final IntQuickPerm Q = new IntQuickPerm(y);

        // Prepare output list
        final MutablePoint[] points = new MutablePoint[n];
        for (int x = 0; x < n; x++) {
            points[x] = new MutablePoint(x, -1);
        }
        final List<Point> out = Collections.<Point>unmodifiableList(Arrays.<MutablePoint>asList(points));

        return new PointGenerator() {
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

            @Override
            public IntQuickPerm getPermuter() {
                return Q;
            }
        };
    }

    public List<Iterator<List<Point>>> splitGenerator(int nSplit) {
        // Array of Y coordinates
        // Points are represented as [x] = y
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            y[i] = i;
        }

        return splitGenerator(new IntQuickPerm(y), nSplit);
    }

    public List<Iterator<List<Point>>> splitGenerator(
            final IntQuickPerm Q, final int nSplit) {
        final int[] y = Q.buf();

        class SplitIterator extends AbstractIterator<List<Point>> {

            final MutablePoint[] points;
            final List<Point> out;

            SplitIterator() {
                points = new MutablePoint[n];
                // Prepare output list
                for (int x = 0; x < n; x++) {
                    points[x] = new MutablePoint(x, -1);
                }
                out = Collections.<Point>unmodifiableList(Arrays.asList(points));
            }

            @Override
            protected List<Point> computeNext() {

                // TODO: Lock free!
                synchronized (Q) {
                    do {
                        if (!Q.hasNext()) {
                            return endOfData();
                        }

                        Q.next();

                        // Transform the output
                        for (int x = 0; x < n; x++) {
                            points[x].setY(y[x]);
                        }
                    } while (points[n - 1].getY() >= n / 2);
                }
                return out;
            }
        }

        List<Iterator<List<Point>>> its = new ArrayList<>(nSplit);
        for (int i = 0; i < nSplit; i++) {
            its.add(new SplitIterator());
        }
        return its;
    }
}
