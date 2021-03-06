package nl.tue.cpps.lbend.geometry;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Node {
    private final IntSet neighbours = HashIntSets.newMutableSet();

    public void addNeighbour(int node) {
        neighbours.add(node);
    }

    public void removeNeighbour(int node) {
        neighbours.removeInt(node);
    }

    public void removeAllNeighbours() {
        neighbours.clear();
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
        return neighbours.size();
    }
}
