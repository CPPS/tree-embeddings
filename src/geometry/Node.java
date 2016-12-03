package geometry;

import java.util.*;

public class Node {
    protected Set<Integer> neighbours = new HashSet<>();

    public void add(int node) {
        neighbours.add(node);
    }
    public void remove(int node) {
        neighbours.remove(node);
    }
    public boolean isNeighbour(int node) {
        return neighbours.contains(node);
    }

    public Collection<Integer> getNeighbours() {
        return neighbours;
    }

    public boolean isLeaf() {
        return neighbours.isEmpty();
    }

    public int getDegree() {
        return neighbours.size() + 1;
    }
}
