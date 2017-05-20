package nl.tue.cpps.lbend;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import nl.tue.cpps.lbend.generator.IntQuickPerm;

/**
 * Read a pointset and use the offsets to generate a stopos paremeter set.
 */
public class PointSetDump {
    public static void main(String[] argv) throws IOException {
        int n = 13;

        IntQuickPerm Q = new IntQuickPerm(new int[n]);

        try (
                InputStream fis = new FileInputStream(
                        new File(ReadingPointGenerator.POINTS_DIR, "out-" + n + ".points"));
                GZIPInputStream zis = new GZIPInputStream(fis);
                BufferedInputStream bis = new BufferedInputStream(zis);
                DataInputStream dis = new DataInputStream(bis); //
        ) {
            System.out.println("n=" + dis.readInt());
            System.out.println("per_batch=" + dis.readInt());

            int maxOffset = 0;
            do {
                Q.read(dis);

                dis.mark(10);
                if (dis.read() == -1) {
                    // EOF
                    break;
                }
                dis.reset();

                maxOffset++;
            } while (true);

            System.out.println("max_offset=" + maxOffset);
            System.out.println("Suggested: seq 0 " + maxOffset + " > " + n + ".stopos");
        }
    }
}
