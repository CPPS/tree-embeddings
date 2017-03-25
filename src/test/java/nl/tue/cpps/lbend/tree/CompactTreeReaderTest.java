package nl.tue.cpps.lbend.tree;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class CompactTreeReaderTest {
    @Test
    public void test() throws IOException {
        int maxTest = 6; // max: 20
        for (int i = 4; i <= maxTest; i++) {
            int n = i;
            try (PlainTreeReader reader = new PlainTreeReader(
                    TreeCompactor.IN_DIR, n)) {
                CompactTreeReader.forN(n, w -> {
                    while (reader.hasNext()) {
                        assertEquals(w.next(), reader.next());
                    }
                });
            }
        }
    }
}
