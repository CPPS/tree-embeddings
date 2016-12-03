import java.util.*;

public class NaiveTreeGenerator extends TreeGenerator {

    protected BoundedPrüferSequenceGenerator generator;

    public NaiveTreeGenerator(int n, int k) {
        super(n, k);
        this.generator = new BoundedPrüferSequenceGenerator(n, k);
    }

    @Override
    public Iterator<Tree> iterator() {
        return new Iterator<Tree>() {
            @Override
            public boolean hasNext() {
                return generator.hasNext();
            }

            @Override
            public Tree next() {
                Tree T = new Tree(n);

                int[] sequence = generator.next();
                int[] count = new int[n];

                for (int i : sequence) {
                    count[i]++;
                }

                int index = 0;

                List<Integer> list = new LinkedList<>();
                for (int i = 0; i < n; i++) list.add(i);

                if (list.size() < 2) return T;
                while (list.size() > 2) {
                    Iterator<Integer> iterator = list.iterator();
                    int element;

                    do { element = iterator.next(); }
                    while (!(count[element] == 0));
                    iterator.remove();

                    T.connect(element, sequence[index]);
                    count[sequence[index]]--;
                    index++;
                }

                T.connect(list.get(0), list.get(1));

                return T;
            }
        };
    }
}
