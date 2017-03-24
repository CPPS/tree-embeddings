package nl.tue.cpps.lbend.generator;

public class IntQuickPerm extends AbstractQuickPerm<int[]> {

    public IntQuickPerm(int[] in) {
        super(in);
    }

    @Override
    protected int size(int[] in) {
        return in.length;
    }

    protected void swap(int i, int j) {
        int v = a[i];
        a[i] = a[j];
        a[j] = v;
    }
}
