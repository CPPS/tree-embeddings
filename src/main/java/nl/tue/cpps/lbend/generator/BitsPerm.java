package nl.tue.cpps.lbend.generator;

import java.util.BitSet;
import java.util.Iterator;

import com.google.common.base.Preconditions;

public class BitsPerm implements Iterator<BitSet> {
    private final long mask;
    private long v;

    public BitsPerm(int n) {
        Preconditions.checkArgument(n < Long.SIZE);

        this.mask = (((long) 1) << n) - 1;
        this.v = 0;
    }

    @Override
    public boolean hasNext() {
        return (v & ~mask) == 0;
    }

    @Override
    public BitSet next() {
        long val = v;
        v++;
        return BitSet.valueOf(new long[] { val });
    }
}
