package nl.tue.cpps.lbend.tree;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import nl.tue.cpps.lbend.generator.TreeReader;

public class CompactTreeReaderTest {
    @Test
    public void test() throws IOException {
        int maxTest = 6; // max: 20
        for (int i = 4; i <= maxTest; i++) {
            File out = new File(TreeCompactor.OUT_DIR, i + ".tree");
            TreeReader reader = new TreeReader(TreeCompactor.IN_DIR, i);

            CompactTreeReader.forFile(out, w -> {
                while (reader.hasNext()) {
                    assertEquals(w.next(), reader.next());
                }
            });
        }
    }
}
