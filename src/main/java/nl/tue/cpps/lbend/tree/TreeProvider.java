package nl.tue.cpps.lbend.tree;

import java.io.IOException;
import java.util.Iterator;

import nl.tue.cpps.lbend.geometry.Tree;

public interface TreeProvider extends Iterator<Tree>, AutoCloseable {
    void close() throws IOException;
}
