package math;

import com.koloboke.collect.IntCursor;
import geometry.Node;

import javax.annotation.Nullable;
import java.util.Arrays;

public class Solver2SAT {

    /**
     * Implemented using:
     * https://kartikkukreja.wordpress.com/2013/05/16/solving-2-sat-in-linear-
     * time/
     *
     * Also called Implication Graph
     */

    // Implication graph
    private final int n, size;
    private Node[] graph, graphReverse;
    private boolean[] explored;
    private int[] leader, finish, order;
    private int t, parent;
    private boolean[] truthAssignment, hasTruthAssignment;

    public Solver2SAT(int n) {
        // n - 1 edges in a tree,
        // so for each edge, put two nodes for 2-SAT
        this.n = n;
        this.size = 2 * n;

        graph = new Node[size];
        graphReverse = new Node[size];
        explored = new boolean[size];
        leader = new int[size];
        finish = new int[size];
        order = new int[size];
        truthAssignment = new boolean[size];
        hasTruthAssignment = new boolean[size];

        for (int i = 0; i < size; i++) {
            graph[i] = new Node();
            graphReverse[i] = new Node();
        }
    }

    /**
     * Adds a clause to the 2-SAT problem. ... AND (i OR j)
     *
     * if complementI, then !i is placed in formula same for j
     *
     * for (u OR v) following edges are placed: !u -> v !v -> u
     *
     * @param i
     *            0 <= i < n
     * @param j
     *            0 <= j < n
     */
    public Solver2SAT addClause(int i, boolean complementI, int j, boolean complementJ) {
        int u = complementI ? n + i : i;
        int notU = complementI ? i : n + i;
        int v = complementJ ? n + j : j;
        int notV = complementJ ? j : n + j;

        graph[notU].addNeighbour(v);
        graph[notV].addNeighbour(u);
        graphReverse[v].addNeighbour(notU);
        graphReverse[u].addNeighbour(notV);

        return this;
    }

    /**
     * Get a satisfying assignment of the 2SAT, or null when not satisfiable.
     * 
     * @return boolean assignment or null when not satisfiable
     */
    @Nullable
    public boolean[] solve() {
        Arrays.fill(leader, 0);
        Arrays.fill(finish, 0);
        Arrays.fill(order, 0);
        Arrays.fill(hasTruthAssignment, false);
        t = 0;
        parent = 0;

        // run dfs on the reverse graph to get reverse postorder
        Arrays.fill(explored, false);
        for (int i = size - 1; i >= 0; i--) {
            if (!explored[i]) {
                reverseDfs(i);
            }
            order[finish[i]] = i;
        }

        // run dfs on the actual graph in reverse postorder
        Arrays.fill(explored, false);
        for (int i = size - 1; i >= 0; i--) {
            if (!explored[order[i]]) {
                parent = order[i];
                dfs(order[i]);
            }
        }

        // check if a variable and its complement belong in the same SCC in
        // reverse postorder
        // and assign truth values to SCC
        int i = size - 1;
        while (i >= 0) {
            int u = order[i];
            if (u >= n) {
                if (stronglyConnected(u, u - n))
                    break;
                if (!hasTruthAssignment[leader[u - n]]) {
                    hasTruthAssignment[leader[u]] = true;
                    hasTruthAssignment[leader[u - n]] = true;
                    truthAssignment[leader[u]] = true;
                    truthAssignment[leader[u - n]] = false;
                }
            } else {
                if (stronglyConnected(u, u + n))
                    break;
                if (!hasTruthAssignment[leader[u]]) {
                    hasTruthAssignment[leader[u]] = true;
                    hasTruthAssignment[leader[u + n]] = true;
                    truthAssignment[leader[u]] = true;
                    truthAssignment[leader[u + n]] = false;
                }
            }

            i--;
        }

        if (i >= 0) {
            // no satisfying assignment
            return null;
        }

        boolean[] solution = new boolean[n];
        for (i = 0; i < n; i++) {
            solution[i] = truthAssignment[leader[i]];
        }

        return solution;
    }

    /**
     * Resets the 2SAT, removing any present clauses.
     */
    public void reset() {
        for (Node node : graph)
            node.removeAllNeighbours();
        for (Node node : graphReverse)
            node.removeAllNeighbours();
    }

    private boolean stronglyConnected(int i, int j) {
        return leader[i] == leader[j];
    }

    private void dfs(int i) {
        explored[i] = true;
        leader[i] = parent;

        IntCursor cursor = graph[i].getNeighbours().cursor();
        while (cursor.moveNext()) {
            int neighbour = cursor.elem();
            if (!explored[neighbour]) {
                dfs(neighbour);
            }
        }
    }

    private void reverseDfs(int i) {
        explored[i] = true;

        IntCursor cursor = graphReverse[i].getNeighbours().cursor();
        while (cursor.moveNext()) {
            int neighbour = cursor.elem();
            if (!explored[neighbour]) {
                reverseDfs(neighbour);
            }
        }

        finish[i] = t;
        t++;
    }
}
