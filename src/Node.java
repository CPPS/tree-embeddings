import java.util.LinkedList;
import java.util.List;

public class Node {
    protected List<Integer> children = new LinkedList<>();

    public void add(int node) {
        children.add(node);
    }

    public List<Integer> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public int getDegree() {
        return children.size() + 1;
    }
}
