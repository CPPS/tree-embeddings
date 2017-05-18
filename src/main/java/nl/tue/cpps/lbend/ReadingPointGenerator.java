package nl.tue.cpps.lbend;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;

import nl.tue.cpps.lbend.generator.IntQuickPerm;
import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.generator.point.PointSetGenerator;
import nl.tue.cpps.lbend.geometry.Point;

public class ReadingPointGenerator implements PointSetGenerator {
    private static final File POINTS_DIR = new File("point-dump");

    private final int N;
    private final int PER_FILE;

    private IntQuickPerm Q;

    public ReadingPointGenerator(int n, int offset) throws IOException {
        IntQuickPerm Q = new IntQuickPerm(new int[n]);

        try (
                InputStream fis = new FileInputStream(
                        new File(POINTS_DIR, "out-" + n + ".points"));
                GZIPInputStream zis = new GZIPInputStream(fis);
                BufferedInputStream bis = new BufferedInputStream(zis);
                DataInputStream dis = new DataInputStream(bis); //
        ) {
            N = dis.readInt();
            PER_FILE = dis.readInt();

            if (n != N) {
                throw new IOException("N mismatch");
            }

            // Skip for the offset
            for (int i = 0; i < offset; i++) {
                Q.read(dis);
            }

            // Read the real data.
            Q.read(dis);
        }

        this.Q = Q;
    }

    @Override
    public Iterator<List<Point>> generate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Iterator<List<Point>>> splitGenerator(int nSplit) {
        Preconditions.checkState(Q != null);
        IntQuickPerm q = Q;
        Q = null;

        List<Iterator<List<Point>>> iterators = new PermutedPointGenerator(N).splitGenerator(q, nSplit);

        AtomicInteger atomic = new AtomicInteger(PER_FILE);
        for (int i = 0; i < iterators.size(); i++) {
            Iterator<List<Point>> it = iterators.get(i);
            iterators.set(i, new AbstractIterator<List<Point>>() {
                @Override
                protected List<Point> computeNext() {
                    // Don't care if we don't have a new pointset.
                    if (!it.hasNext()) {
                        return endOfData();
                    }

                    // Limit to one page.
                    int got = atomic.decrementAndGet();
                    if (got <= 0) {
                        atomic.set(-1);
                        return endOfData();
                    }

                    // Do the thing
                    return it.next();
                }

            });
        }

        return iterators;
    }
}
