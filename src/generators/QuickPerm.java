package generators;

public class QuickPerm<T> extends AbstractQuickPerm<T[]> {
    public QuickPerm(T[] in) {
        super(in);
    }

    @Override
    protected int size(T[] in) {
        return in.length;
    }

    protected void swap(int i, int j) {
        T v = a[i];
        a[i] = a[j];
        a[j] = v;
    }
}
