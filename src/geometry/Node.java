package geometry;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Node {
    private final IntSet neighbours = HashIntSets.newMutableSet();

    // Package-private
    void addNeighbour(int node) {
        neighbours.add(node);
    }

    // Package-private
    void removeNeighbour(int node) {
        neighbours.removeInt(node);
    }

    public boolean isNeighbour(int node) {
        return neighbours.contains(node);
    }

    public IntSet getNeighbours() {
        return neighbours;
    }

    public boolean isLeaf() {
        return neighbours.isEmpty();
    }

    public int getDegree() {
        return neighbours.size() + 1;
    }
}
