package generators;

import java.util.BitSet;

public class BitSetQuickPerm extends AbstractQuickPerm<BitSet> {
    private final int n;

    public BitSetQuickPerm(int n, BitSet bitSet) {
        super(bitSet);
        this.n = n;
    }

    @Override
    protected int size(BitSet in) {
        return n;
    }

    protected void swap(int i, int j) {
        boolean v = a.get(i);
        a.set(i, a.get(j));
        a.set(j, v);
    }
}
