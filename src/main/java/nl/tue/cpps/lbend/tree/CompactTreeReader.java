package nl.tue.cpps.lbend.tree;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import com.google.common.collect.AbstractIterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.tue.cpps.lbend.geometry.Tree;

@RequiredArgsConstructor
public final class CompactTreeReader extends AbstractIterator<Tree> implements TreeProvider {
    private final @NonNull DataInputStream is;

    @Override
    protected Tree computeNext() {
        try {
            return computeNext0();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Tree computeNext0() throws IOException {
        int nNode = is.readInt();
        if (nNode == -1) {
            return endOfData();
        }

        int nEdge = is.readInt();

        Tree t = new Tree(nNode);

        for (int i = 0; i < nEdge; i++) {
            int left = is.readInt();
            int right = is.readInt();
            t.connect(left, right);
        }

        return t;
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    public static void forFile(File in, IOConsumer<CompactTreeReader> cb)
            throws IOException {
        try (//
                InputStream fis = new FileInputStream(in);
                InputStream zis = new GZIPInputStream(fis);
                InputStream bis = new BufferedInputStream(zis);
                DataInputStream dis = new DataInputStream(bis);
                CompactTreeReader w = new CompactTreeReader(dis); //
        ) {
            cb.accept(w);
        }
    }
}
