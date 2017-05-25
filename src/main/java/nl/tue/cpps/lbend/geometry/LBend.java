package nl.tue.cpps.lbend.geometry;

import java.util.List;

import lombok.Data;

@Data
public class LBend {
    private final Line horizontal, vertical;

    public boolean intersectsWith(LBend other) {
        return MappingValidator2SAT.intersects(horizontal, other.vertical)
                || MappingValidator2SAT.intersects(other.horizontal, vertical);
    }

    public static LBend getBend(Point p1, Point p2, boolean complement) {

        // |- false
        // -| true

        Line horizontal = complement
                ? new Line(p1, new FixedPoint(p2.getX(), p1.getY()))
                : new Line(p2, new FixedPoint(p1.getX(), p2.getY()));

        Line vertical = complement
                ? new Line(p2, new FixedPoint(p2.getX(), p1.getY()))
                : new Line(p1, new FixedPoint(p1.getX(), p2.getY()));

        return new LBend(horizontal, vertical);
    }

    /**
     * Creates all possible bends on the given point set.
     * result[i][j] contains 2-valued array with the two
     * possible bends from point i to point j.
     * First value is with complement==true, second value
     * with complement==false.
     * @param points the point set
     * @return all bends over point set
     */
    public static LBend[][][] createAllBends(List<Point> points) {
        int n = points.size();
        LBend[][][] bends = new LBend[n][n][2];
        for (int from = 0; from < n; from++) {
            for (int to = 0; to < n; to++) {
                if (from == to) continue;

                bends[from][to][0] = getBend(points.get(from), points.get(to), true);
                bends[from][to][1] = getBend(points.get(from), points.get(to), false);
            }
        }
        return bends;
    }
}
