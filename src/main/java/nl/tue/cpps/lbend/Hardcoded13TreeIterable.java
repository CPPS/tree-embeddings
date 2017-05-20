package nl.tue.cpps.lbend;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import nl.tue.cpps.lbend.geometry.Tree;

public class Hardcoded13TreeIterable implements Iterable<Tree> {
    @Override
    public Iterator<Tree> iterator() {
        return new AbstractIterator<Tree>() {
            boolean first = true;

            @Override
            protected Tree computeNext() {
                if (!first) {
                    return endOfData();
                }
                first = false;

                return new Tree(13)
                        .connect(0, 1)
                        .connect(0, 2)
                        .connect(0, 3)

                        .connect(1, 4)
                        .connect(1, 5)
                        .connect(1, 6)

                        .connect(2, 7)
                        .connect(2, 8)
                        .connect(2, 9)

                        .connect(3, 10)
                        .connect(3, 11)
                        .connect(3, 12);
            }
        };
    }

}
