package generator;

import java.util.Iterator;

import lombok.NonNull;

/** The Counting QuickPerm Algorithm: */
public abstract class AbstractQuickPerm<T> implements Iterator<T> {
    /** Amount of objects */
    private final int N;
    /** List objects to permute */
    protected final T a;
    /** Integer array to control the permutation (N) */
    private final int[] p;
    /** Index */
    private int i;
    /** True if the next case has already been found */
    private boolean didFindNext = true;

    protected abstract int size(T in);

    protected abstract void swap(int i, int j);

    public AbstractQuickPerm(@NonNull T in) {
        N = size(in);
        a = in;
        p = new int[N];
        reset();
    }

    public void reset() {
        for (int i = 0; i < N; i++) {
            p[i] = 0;
        }

        i = 1;
    }

    public boolean hasNext() {
        return didFindNext || findNext();
    }

    public boolean findNext() {
        while (i < N) {
            if (p[i] < i) {
                int j = (i % 2 == 1) ? p[i] : 0;
                swap(j, i);
                p[i]++;
                i = 1;
                return didFindNext = true;
            } else {
                p[i] = 0;
                i++;
            }
        }

        return false;
    }

    public T next() {
        if (!didFindNext) {
            findNext();
        }

        didFindNext = false;
        return a;
    }
}
