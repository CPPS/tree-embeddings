public class Tree {
    protected Node[] nodes;

    public Tree(int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }

        nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node();
        }
    }

    public int size() {
        return nodes.length;
    }
}
