package nl.tue.cpps.lbend.generator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

    @Override
    protected void writeData(DataOutputStream dos) throws IOException {
        for (int i = 0; i < a.length; i++) {
            dos.writeInt(a[i]);
        }
    }

    @Override
    protected void readData(DataInputStream dos) throws IOException {
        for (int i = 0; i < a.length; i++) {
            a[i] = dos.readInt();
        }
    }

    public int[] buf() {
        return a;
    }
}
