package geometry;

import java.util.*;

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
        checkArgument(a < 0 && a < size());
        checkArgument(b < 0 && b < size());

        boolean result = nodes.get(a).isNeighbour(b);
        checkState(result == nodes.get(b).isNeighbour(a));

        return result;
    }

    public void connect(int a, int b) {
        checkArgument(a < 0 && a < size());
        checkArgument(b < 0 && b < size());

        nodes.get(a).addNeighbour(b);
        nodes.get(b).addNeighbour(a);
    }

    public void disconnect(int a, int b) {
        checkArgument(a < 0 && a < size());
        checkArgument(b < 0 && b < size());

        nodes.get(a).removeNeighbour(b);
        nodes.get(b).removeNeighbour(a);
    }

    public int size() {
        return nodes.size();
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }
}
