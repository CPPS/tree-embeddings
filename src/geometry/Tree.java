package geometry;

import java.util.*;

import javax.annotation.Nullable;

import com.koloboke.collect.IntCursor;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class Tree implements Iterable<Node> {
    protected List<Node> nodes;

    public Tree(int n) {
        checkArgument(n >= 1);

        nodes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            nodes.add(new Node());
        }
    }

    public boolean areConnected(int a, int b) {
        checkRange(a);
        checkRange(b);

        boolean result = nodes.get(a).isNeighbour(b);
        checkState(result == nodes.get(b).isNeighbour(a));

        return result;
    }

    public void connect(int a, int b) {
        checkRange(a);
        checkRange(b);

        nodes.get(a).addNeighbour(b);
        nodes.get(b).addNeighbour(a);
    }

    public void disconnect(int a, int b) {
        checkRange(a);
        checkRange(b);

        nodes.get(a).removeNeighbour(b);
        nodes.get(b).removeNeighbour(a);
    }

    private void checkRange(int idx) {
        checkArgument(0 <= idx && idx < size(), " 0 <= %s < %s", idx, size());
    }

    /**
     * @return The amount of nodes in the graph.
     */
    public int size() {
        return nodes.size();
    }

    /**
     * Returns the specified node.
     *
     * @param index
     *            The node index.
     * @return The node.
     * @throws IndexOutOfBoundsException
     *             If the index is out of range.
     */
    public Node getNode(int index) {
        return nodes.get(index);
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public Iterator<Edge> edgeIterator() {
        return new Iterator<Edge>() {
            private final ListIterator<Node> nodeIt = nodes.listIterator();
            private int idx;
            private @Nullable Node node;
            private @Nullable IntCursor cursor;

            // The next edge, null if it has not been found yet by findEdge()
            private @Nullable Edge foundEdge;

            private boolean findEdge() {
                do {
                    if (node != null) {
                        // Have a node, try to find an edge
                        if (cursor.moveNext()) {
                            int elem = cursor.elem();

                            // We have already reported this (undirected)
                            if (elem < idx) {
                                continue;
                            }

                            foundEdge = new Edge(idx, elem);
                            return true;
                        }

                        // Don't have another edge, fall back to getting new
                        // node
                    }

                    // Try to find the next node
                    if (nodeIt.hasNext()) {
                        idx = nodeIt.nextIndex();
                        node = nodeIt.next();
                        cursor = node.getNeighbours().cursor();
                        // Loop again, to find the edge
                        continue;
                    } else {
                        return false;
                    }
                } while (true);
            }

            @Override
            public boolean hasNext() {
                return foundEdge != null || findEdge();
            }

            @Override
            public Edge next() {
                // Have not found an edge yet, look for it
                if (foundEdge == null) {
                    if (!findEdge()) {
                        throw new NoSuchElementException();
                    }
                }

                Edge edge = foundEdge;
                foundEdge = null;
                return edge;
            }

        };
    }
}
