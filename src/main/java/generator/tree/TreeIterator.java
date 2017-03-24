package generator.tree;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import geometry.Tree;

public class TreeIterator extends AbstractIterator<Tree> {
    private final Iterator<int[]> codes;
    private Iterator<int[]> sequenceGenerator;

    public TreeIterator(int n, int k) {
        codes = new TreeCodeGenerator(n, k).iterator();
    }

    public static Iterable<Tree> iterable(int n, int k) {
        return () -> new TreeIterator(n, k);
    }

    @Override
    protected Tree computeNext() {
        if (sequenceGenerator != null) {
            if (sequenceGenerator.hasNext()) {
                return TreeBuilder.fromSequence(sequenceGenerator.next());
            } else {
                sequenceGenerator = null;
                return computeNext();
            }
        } else if (codes.hasNext()) {
            sequenceGenerator = new SequenceGenerator(codes.next()).iterator();
            return computeNext();
        } else {
            return endOfData();
        }
    }
}
