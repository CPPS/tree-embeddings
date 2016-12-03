import java.util.*;

public class Tree implements Iterable<Node> {
    protected List<Node> nodes;

    public Tree(int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }

        nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(new Node());
        }
    }

    public boolean areConnected(int a, int b) {
        if (0 > a || a > size()) {
            throw new IllegalArgumentException();
        }

        if (0 > b || b > size()) {
            throw new IllegalArgumentException();
        }

        boolean result = nodes.get(a).isNeighbour(b);
        if (result != nodes.get(b).isNeighbour(a)) {
            throw new IllegalStateException();
        }

        return result;
    }

    public void connect(int a, int b) {
        if (0 > a || a > size()) {
            throw new IllegalArgumentException();
        }

        if (0 > b || b > size()) {
            throw new IllegalArgumentException();
        }

        nodes.get(a).add(b);
        nodes.get(b).add(a);
    }

    public void disconnect(int a, int b) {
        if (0 > a || a > size()) {
            throw new IllegalArgumentException();
        }

        if (0 > b || b > size()) {
            throw new IllegalArgumentException();
        }

        nodes.get(a).remove(b);
        nodes.get(b).remove(a);
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
