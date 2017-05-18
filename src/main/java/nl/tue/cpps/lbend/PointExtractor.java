package nl.tue.cpps.lbend;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator.PointGenerator;

public class PointExtractor {
    private static final int PER_FILE = 100_000;
    private static final File POINTS_DIR = new File("point-dump");

    public static void main(String[] argv) throws IOException {
        if (!POINTS_DIR.exists() && !POINTS_DIR.mkdirs()) {
            System.err.println("Failed to create point dir");
        }

        int n = 13;
        PointGenerator it = new PermutedPointGenerator(n).generate();

        try (
                OutputStream fos = new FileOutputStream(
                        new File(POINTS_DIR, "out-" + n + ".points"));
                GZIPOutputStream zos = new GZIPOutputStream(fos);
                BufferedOutputStream bos = new BufferedOutputStream(zos);
                DataOutputStream dos = new DataOutputStream(bos); //
        ) {
            dos.writeInt(n);
            dos.writeInt(PER_FILE);

            int i = 0;
            while (it.hasNext()) {
                it.getPermuter().write(dos);
                System.out.println("State: " + ++i);

                for (int j = 0; j < PER_FILE && it.hasNext(); j++) {
                    it.next();
                }
            }
        }
    }
}
