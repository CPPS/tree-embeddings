package geometry;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import generators.BitsPerm;

public class MappingValidator {
    public boolean validate(Tree t, int[] mapping, List<Point> p) {
        int maxX = 0, maxY = 0, nEdges = 0;

        // Make sure all edges are L-bends
        {
            Iterator<Edge> it = t.edgeIterator();
            while (it.hasNext()) {
                Edge e = it.next();
                Point from = p.get(mapping[e.getFrom()]);
                Point to = p.get(mapping[e.getTo()]);

                nEdges++;

                maxX = Math.max(maxX, Math.max(from.getX(), to.getX()));
                maxY = Math.max(maxY, Math.max(from.getY(), to.getY()));

                if (from.getX() == to.getX() || from.getY() == to.getY()) {
                    System.out.println("Not an L bend");
                    return false;
                }
            }
        }

        // Make sure there are no overlapping edges.

        // There are 4 configurations for every edge:
        // ._____________ (toX, toY)
        // |.............
        // |.............
        // |.............
        // |.............
        // |.............
        // (fromX, fromY)

        // (fromX, toY)
        // |.............
        // |.............
        // |.............
        // |.............
        // |.............
        // |_____________ (toX, fromY)

        // (fromX, toY)
        // _____________.
        // .............|
        // .............|
        // .............|
        // .............|
        // .............|
        // .............(toX, fromY)

        // .............| (toX, toY)
        // .............|
        // .............|
        // .............|
        // .............|
        // .............|
        // _____________|
        // (fromX, fromY)

        // TODO: Make this asymptotically faster
        // Compute every graph configuration in order to find one without
        // overlapping edges
        BitsPerm directionsPermuter = new BitsPerm(nEdges * 2);
        while (directionsPermuter.hasNext()) {
            BitSet configuration = directionsPermuter.next();

            BitSet grid = new BitSet((maxX + 1) * (maxY + 1));
            int edgeIdx = 0;
            Iterator<Edge> it = t.edgeIterator();
            boolean valid = true;
            while (valid && it.hasNext()) {
                Edge e = it.next();
                Point from = p.get(mapping[e.getFrom()]);
                Point to = p.get(mapping[e.getTo()]);

                int fromX = Math.min(from.getX(), to.getX()) + 1;
                int toX = Math.max(from.getX(), to.getX()) - 1;
                int fromY = Math.min(from.getY(), to.getY()) + 1;
                int toY = Math.max(from.getY(), to.getY()) - 1;

                // Draw in the points
                for (Point point : new Point[] { from, to }) {
                    grid.set(point.getX() * (maxY + 1) + point.getY());
                }

                System.out.println(from + " x " + to);

                // Corner point
                {
                    boolean hor = configuration.get(edgeIdx * 2);
                    boolean vert = configuration.get(edgeIdx * 2 + 1);
                    int y = !vert ? fromY - 1 : toY + 1;
                    int x = hor ? fromX - 1 : toX + 1;

                    System.out.println(x + " x " + y);
                    int idx = x * (maxY + 1) + y;

                    if (grid.get(idx)) {
                        valid = false;
                        break;
                    }

                    grid.set(idx);
                }

                // Fill horizontal line
                for (int x = fromX; x <= toX; x++) {
                    int y = configuration.get(edgeIdx * 2) ? fromY : toY;
                    int idx = x * (maxY + 1) + y;

                    if (grid.get(idx)) {
                        valid = false;
                        break;
                    }

                    grid.set(idx);
                }

                // Fill vertical line
                for (int y = fromY; y <= toY; y++) {
                    int x = configuration.get(edgeIdx * 2 + 1) ? fromX : toX;
                    int idx = x * (maxY + 1) + y;

                    if (grid.get(idx)) {
                        valid = false;
                        break;
                    }

                    grid.set(idx);
                }

                edgeIdx++;
            }

            // Valid?
            if (true) {
                System.out.println("=> " + maxX + " " + maxY);

                Set<Point> pointSet = Sets.newHashSet(p);
                for (int y = 0; y <= maxY; y++) {
                    for (int x = 0; x <= maxX; x++) {
                        if (pointSet.contains(new FixedPoint(x, y))) {
                            System.out.print("X");
                        } else if (grid.get(x * (maxY + 1) + y)) {
                            System.out.print("*");
                        } else {
                            System.out.print(" ");
                        }
                    }

                    System.out.println();
                }
                if (!valid)
                    continue;
                System.exit(0);
                return true;
            }
        }

        return false;
    }

}
