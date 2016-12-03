package generators;

import math.Combinatorics;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class TreeCodeGenerator implements Iterable<int[]>{
    protected int n, k;
    protected Collection<int[]> codes;

    public TreeCodeGenerator(int n, int k) {
        this.n = n;
        this.k = k;

        if (n >= 2) {
            this.codes = Combinatorics.partitions(n - 2, k);
        }
    }

    @Override
    public Iterator<int[]> iterator() {
        if (n < 2) return Collections.emptyIterator();
        return codes.iterator();
    }
}
