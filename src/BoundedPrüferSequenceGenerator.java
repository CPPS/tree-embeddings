import java.util.Iterator;

public class BoundedPrüferSequenceGenerator implements Iterator<int[]> {
    protected int n, k;
    protected PrüferSequenceGenerator generator;
    protected int[] next;

    public BoundedPrüferSequenceGenerator(int n, int k) {
        if (n < 2) {
            throw new IllegalArgumentException();
        }

        if (k < 1) {
            throw new IllegalArgumentException();
        }

        this.n = n;
        this.k = k;
        this.generator = new PrüferSequenceGenerator(n);
        next = computeNext();
    }

    @Override
    public boolean hasNext() {
        return !(next == null);
    }

    protected int[] computeNext() {
        while (generator.hasNext()) {
            int[] sequence = generator.next();
            if (valid(sequence)) {
                return sequence;
            }
        }

        return null;
    }

    protected boolean valid(int[] sequence) {
        int[] C = new int[n];
        for (int i = 0; i < sequence.length; i++) {
            int j = sequence[i];

            C[j]++;
            if (C[j] >= k) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int[] next() {
        int[] sequence = next;
        next = computeNext();
        return sequence;
    }
}
