import java.util.Iterator;

public class PrüferSequenceGenerator implements Iterator<int[]>{
    protected int n;
    protected int iteration;
    protected int limit;

    public PrüferSequenceGenerator(int n) {
        if (n < 2) {
            throw new IllegalArgumentException();
        }

        this.n = n;
        this.iteration = 0;
        this.limit = MathUtil.pow(n, n - 2);
    }

    @Override
    public boolean hasNext() {
        return iteration < limit;
    }

    @Override
    public int[] next() {
        int[] sequence = new int[n - 2];

        int source = iteration;
        int index = 0;

        while (source > 0) {
            sequence[index] = source % n;
            source /= n;
            index++;
        }

        iteration++;

        return sequence;
    }
}
