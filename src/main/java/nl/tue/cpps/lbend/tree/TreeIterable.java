package nl.tue.cpps.lbend.tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import nl.tue.cpps.lbend.geometry.Tree;

public class TreeIterable implements Iterable<Tree> {
    private final byte[] buf;

    public TreeIterable(File file) throws IOException {
        buf = Files.readAllBytes(file.toPath());
    }

    public TreeProvider iterator() {
        try {
            return CompactTreeReader.forBuf(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
