package generators;

import java.util.Iterator;

import lombok.NonNull;

/** The Counting QuickPerm Algorithm: */
public class IntQuickPerm implements Iterator<int[]> {
    /** Amount of objects */
    private final int N;
    /** List of objects to permute (N) */
    private final int[] a;
    /** Integer array to control the permutation (N) */
    private final int[] p;
    /** Index */
    private int i;
    private boolean didFindNext = true;

    public IntQuickPerm(@NonNull int[] in) {
        N = in.length;
        a = in.clone();
        p = new int[N];
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

    public int[] next() {
        if (!didFindNext) {
            findNext();
        }

        didFindNext = false;
        return a;
    }

    private void swap(int i, int j) {
        int v = a[i];
        a[i] = a[j];
        a[j] = v;
    }

}
