package nl.tue.cpps.lbend.tree;

import java.io.IOException;

public interface IOConsumer<T> {
    void accept(T v) throws IOException;
}
