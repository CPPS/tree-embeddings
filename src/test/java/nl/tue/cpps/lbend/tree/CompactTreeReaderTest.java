package nl.tue.cpps.lbend.tree;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class CompactTreeReaderTest {
    @Test
    public void test() throws IOException {
        int maxTest = 6; // max: 20
        for (int i = 4; i <= maxTest; i++) {
            int n = i;
            forN(n);
        }
    }

    private void forN(int n) throws IOException {
        try (PlainTreeReader reader = new PlainTreeReader(
                TreeCompactor.IN_DIR, n)) {
            CompactTreeReader.forN(n, new IOConsumer<CompactTreeReader>() {
                @Override
                public void accept(CompactTreeReader w) throws IOException {
                    while (reader.hasNext()) {
                        assertEquals(w.next(), reader.next());
                    }
                }
            });
        }
    }
}
