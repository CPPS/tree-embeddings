package geometry;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

public class MappingValidator {
    public boolean validate(Tree t, int[] mapping, List<Point> p) {
        int maxX = 0, maxY = 0;

        // Make sure all edges are L-bends
        {
            Iterator<Edge> it = t.edgeIterator();
            while (it.hasNext()) {
                Edge e = it.next();
                Point from = p.get(mapping[e.getFrom()]);
                Point to = p.get(mapping[e.getTo()]);

                maxX = Math.max(maxX, Math.max(from.getX(), to.getX()));
                maxY = Math.max(maxY, Math.max(from.getY(), to.getY()));

                if (from.getX() == to.getX() || from.getY() == to.getY()) {
                    System.out.println("Not an L bend");
                    return false;
                }
            }
        }

        // Make sure there are no overlapping edges.
        {
            BitSet bits = new BitSet(maxX * maxY);
            Iterator<Edge> it = t.edgeIterator();
            while (it.hasNext()) {
                Edge e = it.next();
                Point from = p.get(mapping[e.getFrom()]);
                Point to = p.get(mapping[e.getTo()]);

                int fromX = Math.min(from.getX(), to.getX());
                int toX = Math.max(from.getX(), to.getX());
                int fromY = Math.min(from.getY(), to.getY());
                int toY = Math.max(from.getY(), to.getY());

                // TODO: There is to ways to do this
                // We should check both!
                for (int x = fromX; x <= toX; x++) {
                    for (int y = fromY; y <= toY; y++) {
                        int idx = x * maxY + y;

                        // Already marked
                        if (bits.get(idx)) {
                            System.out.println("Duplicate marking " + x + "x" + y);
                            return false;
                        }

                        bits.set(idx, true);
                    }
                }
            }

            // Valid?
            return true;
        }
    }

}
