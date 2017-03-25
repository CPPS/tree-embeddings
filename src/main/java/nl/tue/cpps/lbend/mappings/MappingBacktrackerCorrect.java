package nl.tue.cpps.lbend.mappings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import nl.tue.cpps.lbend.geometry.LBend;
import nl.tue.cpps.lbend.geometry.Node;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

/**
 * Not thread safe!
 */
public class MappingBacktrackerCorrect extends MappingFinder {
    private Tree tree;
    private int n;

    public MappingBacktrackerCorrect() {super();}
    public MappingBacktrackerCorrect(List<Point> points) {super(points);}

    @Override
    public MappingFinder setPointSet(List<Point> points) {
        this.n = points.size();

        allBends = LBend.createAllBends(points);
        return this;
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping) {
        this.tree = tree;

        boolean[] availableLocations = new boolean[n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(availableLocations, true);
            Arrays.fill(mapping, -1);
            availableLocations[i] = false;
            mapping[0] = i;

            Queue<TreeNode> Q = new LinkedList<>();
            TreeNode root = new TreeNode(0, -1, -1);
            root.addChildrenToQueue(Q, i);
            if (backtrackMapping(Q, availableLocations, new ArrayList<>(), mapping)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Try first l-bend of p1, see if sub tree of p1 can be placed.
     *  yes? -> try all positions for p2, each position both l-bends
     *      exists possible placement -> return true
     *      no possible placement -> try second bend of p1. Goto begin
     *  no? -> try second bend of p1. Goto begin.
     *
     *  List<Integer> children
     *  List<LBend> bends for each location of each child
     *
     *  input:
     *  list of children
     *  index of current child
     *  global mapping
     *  list of available point (indices)
     *  list of placed lbends
     *
     *  TODO: add further backtracking, not only to children
     *
     *  child i
     *  root on point pr
     *  for (Point point : availablePoints) {
     *      for (LBend bend : bends) {
     *          if (bend does not intersect) {
     *
     *              if (child i is leaf) {
     *                  if (i is last child) return true;
     *
     *                  if (backtrack i + 1) return true;
     *              }
     *
     *              if (backtrack such that a subtree from child i is possible) {
     *
     *                  // check if last child, or further backtracking should follow
     *                  if (i is last child || backtrack i + 1) {
     *                      return true;
     *                  }
     *
     *              }
     *          }
     *      }
     *  }
     *  return false;
     *
     *
     *  initial call:
     *  int[] mapping = new int[n];
     *  for (point p : points) {
     *      mapping[i] = p;
     *      if (backtrack(root=0, rootlocation=p, children=nodes[0].children, childIdx=0, availablePoints=points-p, bends=[], mapping)) {
     *          return mapping;
     *      }
     *  }
     *  return null;
     */

    private boolean backtrackMapping(Queue<TreeNode> Q, boolean[] availableLocations, List<LBend> bends, int[] mapping) {
        if (Q.isEmpty()) {
            return true;
        }

        TreeNode treeNode = Q.poll();

        if (availableLocations[treeNode.parentLocation])
            throw new AssertionError("parent: " + treeNode.parent + ", available: " + Arrays.toString(availableLocations) +
                                    ", mapping: " + Arrays.toString(mapping));
        if (mapping[treeNode.parent] != treeNode.parentLocation)
            throw new AssertionError("mapping: " + mapping[treeNode.parent] + ", treeNode: " + treeNode.parentLocation);

        for (int location = 0; location < availableLocations.length; location++) {
            if (!availableLocations[location]) continue; // not available

            availableLocations[location] = false;
            if (mapping[treeNode.node] != -1) throw new AssertionError();
            mapping[treeNode.node] = location;
            for (LBend bend : getLBends(treeNode.parentLocation, location)) {
                if (intersects(bends, bend)) continue;

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
                bends.remove(bend);
            }
            availableLocations[location] = true;
            mapping[treeNode.node] = -1;
//            treeNode.removeChildrenFromQueue(Q, location);
        }
        return false;
    }

    private static <E> Queue<E> copyQueue(Queue<E> queue) {
        LinkedList<E> copy = new LinkedList<>();
        for (E e : queue) {
            copy.add(e);
        }
        return copy;
    }

    private static <E> void restoreQueue(Queue<E> toRestore, Queue<E> restoreTo) {
        toRestore.clear();
        for (E e : restoreTo) {
            toRestore.add(e);
        }
    }

    private class TreeNode {
        final int node;
        final int parent;
        final int parentLocation;

        TreeNode(int node, int parent, int parentLocation) {
            this.node = node;
            this.parent = parent;
            this.parentLocation = parentLocation;
        }

        void addChildrenToQueue(Queue<TreeNode> Q, int nodeLocation) {
            Node node = tree.getNode(this.node);

            for (int neighbour : node.getNeighbours()) {
                if (neighbour == parent) continue;

                Q.add(new TreeNode(neighbour, this.node, nodeLocation));
            }
        }

        @SuppressWarnings("unused")
        void removeChildrenFromQueue(Queue<TreeNode> Q, int nodeLocation) {
            for (Iterator<TreeNode> iter = Q.iterator(); iter.hasNext(); ) {
                TreeNode tn = iter.next();

                if (tn.parentLocation == nodeLocation && tn.parent == this.node)
                    iter.remove();
            }
        }
    }

    private boolean intersects(List<LBend> bends, LBend bend) {
        for (LBend bend1 : bends) {
            if (bend1.intersectsWith(bend)) return true;
        }
        return false;
    }

    private LBend[][][] allBends;
    private LBend[] getLBends(int from, int to) {
        return allBends[from][to];
    }

}
