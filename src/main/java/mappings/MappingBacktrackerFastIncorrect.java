package mappings;

import geometry.LBend;
import geometry.MappingValidator2SAT;
import geometry.Point;
import geometry.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MappingBacktrackerFastIncorrect extends MappingFinder {

    private List<Point> points;
    private Tree tree;
    private int n;

    // mapping validation
    private MappingValidator2SAT validator;
    boolean[] contained;

    public MappingBacktrackerFastIncorrect() {super();}
    public MappingBacktrackerFastIncorrect(List<Point> points) {super(points);}

    @Override
    public MappingFinder setPointSet(List<Point> points) {
        this.points = points;
        this.n = points.size();
        this.validator = new MappingValidator2SAT(n);
        this.contained = new boolean[n];

        allBends = LBend.createAllBends(points);
        return this;
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping) {
        this.tree = tree;
        boolean[] availablePoints = new boolean[n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(availablePoints, true);
            Arrays.fill(mapping, -1);

            availablePoints[i] = false;
            mapping[0] = i;

            BacktrackResult result = backtrackMapping(
                    0, i,
                    new ArrayList<>(tree.getNode(0).getNeighbours()), 0,
                    availablePoints,
                    new ArrayList<>(),
                    mapping);

            if (result.result && validateMapping(tree, points, mapping)) {
                return true;
            }

            availablePoints[i] = true;

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
    private BacktrackResult backtrackMapping(int root, int rootLocation, List<Integer> children, int childIdx, boolean[] availablePoints, List<LBend> bends, int[] mapping) {
        int child = children.get(childIdx);
        BacktrackResult thisResult = new BacktrackResult(child);

        List<Integer> childrenOfChild = new ArrayList<>(tree.getNode(child).getNeighbours());
        childrenOfChild.remove((Integer) root);

        for (int point = 0; point < availablePoints.length; point++) {
            if (!availablePoints[point]) continue; // not available

            // place child at point
            mapping[child] = point;
            availablePoints[point] = false;
            thisResult.locationPlaced = point;

            for (LBend bend : getLBends(rootLocation, point)) {

                if (!intersects(bends, bend)) {
                    bends.add(bend);
                    thisResult.bendsToRemove.add(bend);


                    if (childrenOfChild.isEmpty()) {
                        // node has no children, leaf

                        if (childIdx >= children.size() - 1) {
                            // last child
                            return thisResult.setResult(true);
                        } else {
                            // backtrack to next child
                            BacktrackResult bResult = backtrackMapping(root, rootLocation, children, childIdx + 1, availablePoints, bends, mapping);
                            if (bResult.result) {
                                return thisResult.setResult(true);
                            }
                        }
                    } else {
                        // find subtree under child
                        BacktrackResult bResult = backtrackMapping(child, point, childrenOfChild, 0, availablePoints, bends, mapping);
                        if (bResult.result) {

                            thisResult.resultsToUndo.add(bResult);

                            if (childIdx >= childrenOfChild.size() - 1 || childIdx >= children.size() - 1) {
                                return thisResult.setResult(true);
                            }

                            BacktrackResult bResult2 = backtrackMapping(root, rootLocation, children, childIdx + 1, availablePoints, bends, mapping);
                            if (bResult2.result) {
                                thisResult.resultsToUndo.add(bResult2);
                                return thisResult.setResult(true);
                            }

                            // else undo backtrack
                            bResult.undo(bends, mapping, availablePoints);
                        }
                    }

                    bends.remove(bend);
                    thisResult.bendsToRemove.remove(bend);
                }
            }
            mapping[child] = -1;
            availablePoints[point] = true;
        }
        return thisResult.setResult(false);
    }

    private class BacktrackResult {
        boolean result;
        int node;
        List<BacktrackResult> resultsToUndo = new ArrayList<>();
        List<LBend> bendsToRemove = new ArrayList<>();
        int locationPlaced;

        public BacktrackResult(int node) {this(node, false);}
        public BacktrackResult(int node, boolean result) {
            this.node = node;
            this.result = result;
        }

        public BacktrackResult setResult(boolean result) {
            this.result = result;
            return this;
        }

        public void undo(List<LBend> bends, int[] mapping, boolean[] availablePoints) {
            bends.removeAll(bendsToRemove);
            mapping[node] = -1;
            availablePoints[locationPlaced] = true;

            for (BacktrackResult br : resultsToUndo) {
                br.undo(bends, mapping, availablePoints);
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


    private boolean validateMapping(Tree tree, List<Point> points, int[] mapping) {
        for (int i = 0; i < n; i++) {
            // no -1 values
            if (mapping[i] < 0) return false;
        }

        if (!validator.validate(tree, mapping, points)) {
            return false;
//            throw new AssertionError("Invalid mapping, 2-SAT can't find non-intersecting positioning of l-bends");
        }

        Arrays.fill(contained, false);

        for (int i = 0; i < n; i++) {
            int val = mapping[i];
            if (val >= 0 && val < n)
                contained[mapping[i]] = true;
        }

        for (int i = 0; i < n; i++) {
            if (!contained[i]) return false;//throw new AssertionError("Point " + i + " does not have a node mapped to it");
        }
        return true;
    }
}
