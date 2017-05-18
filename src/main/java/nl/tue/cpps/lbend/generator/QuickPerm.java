package nl.tue.cpps.lbend.generator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

    @Override
    protected void writeData(DataOutputStream dos) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void readData(DataInputStream dis) throws IOException {
        throw new UnsupportedOperationException();
    }
}
