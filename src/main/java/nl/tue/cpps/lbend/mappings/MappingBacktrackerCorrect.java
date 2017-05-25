package nl.tue.cpps.lbend.mappings;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import javax.naming.TimeLimitExceededException;

import lombok.RequiredArgsConstructor;
import nl.tue.cpps.lbend.geometry.LBend;
import nl.tue.cpps.lbend.geometry.Node;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

/**
 * Try first l-bend of p1, see if sub tree of p1 can be placed. yes? -> try all
 * positions for p2, each position both l-bends exists possible placement ->
 * return true no possible placement -> try second bend of p1. Goto begin no? ->
 * try second bend of p1. Goto begin.
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
 * // check if last child, or further backtracking should follow if (i is last
 * child || backtrack i + 1) { return true; }
 *
 * } } } } return false;
 *
 *
 * initial call: int[] mapping = new int[n]; for (point p : points) { mapping[i]
 * = p; if (backtrack(root=0, rootlocation=p, children=nodes[0].children,
 * childIdx=0, availablePoints=points-p, bends=[], mapping)) { return mapping; }
 * } return null;
 *
 * Not thread safe!
 */
public final class MappingBacktrackerCorrect extends AbstractLBendMappingFinder {
    private final Queue<TreeNode> Q = new ArrayDeque<>();
    private final TreeNode root = new TreeNode(0, -1, -1);
    private final List<LBend> bends = new ArrayList<>();

    private final int n;
    private final boolean[] availableLocations;

    private Tree tree;
    private LBend[][][] allBends;

    private long maxSteps;
    private long maxTimeMS;
    private long stepCounter;
    private long startTime;

    public MappingBacktrackerCorrect(int n) {
        this.n = n;
        this.availableLocations = new boolean[n];
    }

    @Override
    public void setPoints(List<Point> points, LBend[][][] bends) {
        allBends = bends;
    }

    private LBend[] getLBends(int from, int to) {
        return allBends[from][to];
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping, long maxTimeMS) throws TimeLimitExceededException {
        this.tree = tree;
        this.maxSteps = maxTimeMS < Long.MAX_VALUE / 900 ? maxTimeMS * 900 : Long.MAX_VALUE; // around 900 steps are executed per ms
        this.maxTimeMS = maxTimeMS;
        this.stepCounter = 0;
        this.startTime = System.currentTimeMillis();

        for (int i = 0; i < n; i++) {
            Arrays.fill(availableLocations, true);
            Arrays.fill(mapping, -1);
            availableLocations[i] = false;
            mapping[0] = i;
            Q.clear();
            bends.clear();

            root.addChildrenToQueue(tree, Q, i);
            if (backtrackMapping(
                    Q,
                    availableLocations,
                    bends,
                    mapping)) {
                return true;
            }
        }
        return false;
    }

    private boolean backtrackMapping(
            Queue<TreeNode> Q,
            boolean[] availableLocations,
            List<LBend> bends,
            int[] mapping) throws TimeLimitExceededException {

        if (Q.isEmpty()) {
            return true;
        }

        stepCounter++;
        if (stepCounter > maxSteps) {
            throw new TimeLimitExceededException("allowed: " + maxTimeMS + ", used: " + (System.currentTimeMillis() - startTime));
        }

        TreeNode treeNode = Q.remove();

        assert !availableLocations[treeNode.parentLocation];
        assert mapping[treeNode.parent] == treeNode.parentLocation;

        for (int location = 0; location < availableLocations.length; location++) {
            if (!availableLocations[location]) {
                continue; // not available
            }

            availableLocations[location] = false;
            assert mapping[treeNode.node] == -1;

            mapping[treeNode.node] = location;
            for (LBend bend : getLBends(treeNode.parentLocation, location)) {
                if (intersects(bends, bend)) {
                    continue;
                }

                Queue<TreeNode> copyQ = copyQueue(Q);

                bends.add(bend);
                treeNode.addChildrenToQueue(tree, Q, location);

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

    private boolean intersects(List<LBend> bends, LBend bend) {
        for (LBend bend1 : bends) {
            if (bend1.intersectsWith(bend)) {
                return true;
            }
        }

        return false;
    }

    @RequiredArgsConstructor
    private static final class TreeNode {
        final int node;
        final int parent;
        final int parentLocation;

        void addChildrenToQueue(
                Tree tree,
                Queue<TreeNode> Q,
                int nodeLocation) {
            Node node = tree.getNode(this.node);

            for (int neighbour : node.getNeighbours()) {
                if (neighbour == parent) {
                    continue;
                }

                Q.add(new TreeNode(neighbour, this.node, nodeLocation));
            }
        }
    }
}
