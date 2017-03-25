package nl.tue.cpps.lbend.tree;

import java.io.File;
import java.io.IOException;

import nl.tue.cpps.lbend.generator.TreeReader;

public class TreeCompactor {
    static final File IN_DIR = new File("trees");
    static final File OUT_DIR = new File("compact-trees");

    public static void main(String[] argv) throws IOException {
        if (!OUT_DIR.exists() && !OUT_DIR.mkdirs()) {
            System.err.println("Could not create ouput dir");
        }

        for (int i = 4; i <= 20; i++) {
            run(i);
        }
    }

    private static void run(int i) throws IOException {
        System.out.println("Writing " + i);

        File out = new File(OUT_DIR, i + ".tree");
        CompactTreeWriter.forFile(out, w -> {
            TreeReader reader = new TreeReader(IN_DIR, i);
            while (reader.hasNext()) {
                w.writeTree(reader.next());
            }
        });
    }
}
