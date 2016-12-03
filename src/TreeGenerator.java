public abstract class TreeGenerator implements Iterable<Tree> {
    protected int n, k;

    public TreeGenerator(int n, int k) {
        this.n = n;
        this.k = k;
    }
}
