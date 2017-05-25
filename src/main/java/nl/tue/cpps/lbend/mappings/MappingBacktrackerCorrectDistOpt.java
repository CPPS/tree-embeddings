package nl.tue.cpps.lbend.mappings;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import lombok.RequiredArgsConstructor;
import nl.tue.cpps.lbend.geometry.LBend;
import nl.tue.cpps.lbend.geometry.Node;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

/**
 * Not thread safe!
 */
@Deprecated // Not used
final class MappingBacktrackerCorrectDistOpt extends AbstractMappingFinder {
    private final Queue<TreeNode> Q = new ArrayDeque<>();
    private final TreeNode root = new TreeNode(0, -1, -1);

    private List<Point> points;
    private Tree tree;
    private int n;

    private int[][] closestPoints;
    private int[] rootOrder;

    @Override
    public MappingFinder setPointSet(List<Point> points) {
        this.points = points;
        this.n = points.size();

        allBends = LBend.createAllBends(points);

        closestPoints = new int[n][n - 1];
        for (int i = 0; i < n; i++) {
            Integer[] neighbours = new Integer[n - 1];
            int idx = 0;
            for (int j = 0; j < n; j++) {
                if (i == j)
                    continue;
                neighbours[idx++] = j;
            }

            distanceComparator.setParentLocation(i);
            Arrays.sort(neighbours, distanceComparator);
            for (int j = 0; j < n - 1; j++) {
                closestPoints[i][j] = neighbours[j];
            }
        }

        rootOrder = new int[n];
        int half1 = 2;

        for (int i = 0; i < n; i++) {
            rootOrder[i] = (half1 + i) % n;
        }
        return this;
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping, long maxTimMS) {
        this.tree = tree;

        boolean[] availableLocations = new boolean[n];
        for (int i : rootOrder) {
            Arrays.fill(availableLocations, true);
            Arrays.fill(mapping, -1);
            availableLocations[i] = false;
            mapping[0] = i;

            Q.clear();
            root.addChildrenToQueue(Q, i);
            if (backtrackMapping(Q, availableLocations, new ArrayList<LBend>(), mapping)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Try first l-bend of p1, see if sub tree of p1 can be placed. yes? -> try
     * all positions for p2, each position both l-bends exists possible
     * placement -> return true no possible placement -> try second bend of p1.
     * Goto begin no? -> try second bend of p1. Goto begin.
     *
     * List<Integer> children List<LBend> bends for each location of each child
     *
     * input: list of children index of current child global mapping list of
     * available point (indices) list of placed lbends
     *
     * TODO: add further backtracking, not only to children
     *
     * child i root on point pr for (Point point : availablePoints) { for (LBend
     * bend : bends) { if (bend does not intersect) {
     *
     * if (child i is leaf) { if (i is last child) return true;
     *
     * if (backtrack i + 1) return true; }
     *
     * if (backtrack such that a subtree from child i is possible) {
     *
     * // check if last child, or further backtracking should follow if (i is
     * last child || backtrack i + 1) { return true; }
     *
     * } } } } return false;
     *
     *
     * initial call: int[] mapping = new int[n]; for (point p : points) {
     * mapping[i] = p; if (backtrack(root=0, rootlocation=p,
     * children=nodes[0].children, childIdx=0, availablePoints=points-p,
     * bends=[], mapping)) { return mapping; } } return null;
     */

    private boolean backtrackMapping(Queue<TreeNode> Q, boolean[] availableLocations, List<LBend> bends,
            int[] mapping) {
        if (Q.isEmpty()) {
            return true;
        }

        TreeNode treeNode = Q.poll();

        // if (availableLocations[treeNode.parentLocation])
        // throw new AssertionError("parent: " + treeNode.parent + ", available:
        // " + Arrays.toString(availableLocations) +
        // ", mapping: " + Arrays.toString(mapping));
        // if (mapping[treeNode.parent] != treeNode.parentLocation)
        // throw new AssertionError("mapping: " + mapping[treeNode.parent] + ",
        // treeNode: " + treeNode.parentLocation);

        int[] neighboursSortedByDist = closestPoints[treeNode.parentLocation];
        for (int location : neighboursSortedByDist) {
            if (!availableLocations[location])
                continue; // not available

            availableLocations[location] = false;
            // if (mapping[treeNode.node] != -1) throw new AssertionError();
            mapping[treeNode.node] = location;
            for (LBend bend : getLBends(treeNode.parentLocation, location)) {
                if (intersects(bends, bend))
                    continue;

                Queue<TreeNode> copyQ = copyQueue(Q);

                bends.add(bend);
                treeNode.addChildrenToQueue(Q, location);

                if (backtrackMapping(Q, availableLocations, bends, mapping)) {
                    // possible
                    return true;
                }

                restoreQueue(Q, copyQ);

                // not possible
                // continue with next bend/location
                bends.remove(bends.size() - 1);
            }
            availableLocations[location] = true;
            mapping[treeNode.node] = -1;
            // treeNode.removeChildrenFromQueue(Q, location);
        }
        return false;
    }

    private static <E> Queue<E> copyQueue(Queue<E> queue) {
        return new ArrayDeque<>(queue);
    }

    private static <E> void restoreQueue(Queue<E> toRestore, Queue<E> restoreTo) {
        toRestore.clear();
        toRestore.addAll(restoreTo);
    }

    @RequiredArgsConstructor
    private final class TreeNode {
        final int node;
        final int parent;
        final int parentLocation;

        void addChildrenToQueue(Queue<TreeNode> Q, int nodeLocation) {
            Node node = tree.getNode(this.node);

            for (int neighbour : node.getNeighbours()) {
                if (neighbour == parent)
                    continue;

                Q.add(new TreeNode(neighbour, this.node, nodeLocation));
            }
        }

        @SuppressWarnings("unused")
        void removeChildrenFromQueue(Queue<TreeNode> Q, int nodeLocation) {
            for (Iterator<TreeNode> iter = Q.iterator(); iter.hasNext();) {
                TreeNode tn = iter.next();

                if (tn.parentLocation == nodeLocation && tn.parent == this.node)
                    iter.remove();
            }
        }
    }

    public static boolean intersects(List<LBend> bends, LBend bend) {
        // if (true) return Math.random() < 0.99;
        for (LBend bend1 : bends) {
            if (bend1.intersectsWith(bend))
                return true;
        }
        return false;
    }

    private LBend[][][] allBends;

    private LBend[] getLBends(int from, int to) {
        return allBends[from][to];
    }

    private final DistanceComparator distanceComparator = new DistanceComparator();

    private final class DistanceComparator implements Comparator<Integer> {
        private Point parentPoint;

        public void setParentLocation(int location) {
            this.parentPoint = points.get(location);
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return Long.compare(distToParentSqrd(o1), distToParentSqrd(o2));
        }

        long distToParentSqrd(int from) {
            long dx = points.get(from).getX() - parentPoint.getX();
            long dy = points.get(from).getY() - parentPoint.getY();

            return dx * dx + dy * dy;
        }
    }
}
