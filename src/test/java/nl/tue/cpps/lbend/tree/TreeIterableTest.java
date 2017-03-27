package nl.tue.cpps.lbend.tree;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TreeIterableTest {
    @Test
    public void test() throws IOException {
        new TreeIterable(new File("compact-trees/20.tree")).iterator();
    }
}
