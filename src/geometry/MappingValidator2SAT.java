package geometry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import math.Solver2SAT;

import java.util.*;

public class MappingValidator2SAT {
    private Solver2SAT solver;

    public MappingValidator2SAT(int n) {
        solver = new Solver2SAT(n - 1); // n - 1 edges in tree over n nodes
    }

    /**
     * Returns whether the tree can be placed on the point set
     * given a certain mapping, such that edges (l-bends) do not
     * overlap.
     * @param tree tree
     * @param mapping mapping of tree onto point set
     * @param points point set
     * @return true iff it is possible to place the tree without
     *         overlapping edges, using the mapping
     */
    public boolean validate(Tree tree, int[] mapping, List<Point> points) {
        return validateWithSolution(tree, mapping, points) != null;
    }

    public boolean[] validateWithSolution(Tree tree, int[] mapping, List<Point> points) {
        solver.reset();

        // each edge in tree has two possible placings for its l-bend

        /**
         *  for (edge_i, edge_j):                           O(n^2)
         *      edge_i.L0 + edge_j.L0     valid?
         *      edge_i.L0 + edge_j.L1
         *      edge_i.L1 + edge_j.L0
         *      edge_i.L1 + edge_j.L1
         *
         *      for each invalid placement of l-bends:
         *          -> e.g. ( edge_i.L1 + edge_j.L0      ) =
         *                  ( !(edge_i.L1 AND edge_j.L0) ) =
         *                  ( !edge_i.L1 OR !edge_j.L0   ) = {!L1 = L0, !L0 = L1}
         *                  ( edge_i.L0 OR edge_j.L1     ) -> 2-SAT shape!
         */

        List<Edge> edges = Lists.newArrayList(tree.edgeIterator());

        Edge[] edgePair = new Edge[2];
        Point[][] endpoints = new Point[2][2];
        for (int i = 0; i < edges.size(); i++) {
            for (int j = i + 1; j < edges.size(); j++) {

                edgePair[0] = edges.get(i);
                edgePair[1] = edges.get(j);

                for (int k = 0; k < 2; k++) {
                    endpoints[k][0] = points.get(mapping[edgePair[k].getFrom()]);
                    endpoints[k][1] = points.get(mapping[edgePair[k].getTo()]);
                }

                /**
                 * Edge has start and end point.
                 *
                 * Two possible ways to create l-bend:
                 * (L0) |-   = start vertically from start point, finish horizontally towards end point
                 * (L1) -|   = vice versa
                 *
                 * So for two edges, four possible ways in total (2*2):
                 *  |-  |-  0=k     L0  L0
                 *  |-  -|  1       L0  L1
                 *  -|  |-  2       L1  L0
                 *  -|  -|  3       L1  L1
                 *
                 *  Always two intersections to check:
                 *  - Horizontal 0 against Vertical 1
                 *  - Vertical 0 against Horizontal 1
                 */

                // for all 4 possible placings of bends
                // |- |-    0 0
                // |- -|    0 1
                // -| |-    1 0
                // -| -|    1 1
                for (int k = 0; k < 2; k++) {
                    for (int m = 0; m < 2; m++) {
                        boolean complement0 = k == 1;
                        boolean complement1 = m == 1;
                        LBend bend1 = LBend.getBend(endpoints[0][0], endpoints[0][1], complement0);
                        LBend bend2 = LBend.getBend(endpoints[1][0], endpoints[1][1], complement1);

                        if (bend1.intersectsWith(bend2)) {
                            solver.addClause(i, !complement0, j, !complement1); // inverse, by De Morgan law
//                            BendPanel.showAsFrame().setBends(Arrays.asList(bend1, bend2));
                        }
                    }
                }
            }
        }

        // finally
        return solver.solve();
    }

    /**
     * Returns whether the two lines intersect.
     * When an endpoint lies on the other line (or endpoint),
     * it is considered an intersection.
     *
     * @param horizontal horizontal line, first point is endpoint of bend (NOT middle point!)
     * @param vertical vertical line, first point is endpoint of bend (NOT middle point!)
     * @return true iff the two lines intersect
     */
    public static boolean intersects(Line horizontal, Line vertical) {
        Preconditions.checkArgument(horizontal.getFrom().getY() == horizontal.getTo().getY());
        Preconditions.checkArgument(vertical.getFrom().getX() == vertical.getTo().getX());

        int horY = horizontal.getFrom().getY();
        int verX = vertical.getFrom().getX();

        int verY0 = vertical.getFrom().getY();
        int verY1 = vertical.getTo().getY();
        int horX0 = horizontal.getFrom().getX();
        int horX1 = horizontal.getTo().getX();

        return ((horX0 < verX && verX <= horX1) || (horX1 <= verX && verX < horX0))
                && ((verY0 < horY && horY <= verY1) || (verY1 <= horY && horY < verY0));
    }

}

/**
 *
 * Given point set and a tree:
 *  - Generate all possible mappings of that tree onto the point set
 *
 * Given point set, tree, and mapping of tree onto point set:
 *  - Check (using 2-SAT) whether the mapping is possible
 *
 */
