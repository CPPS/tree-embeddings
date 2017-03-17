import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import generators.TreeIterator;
import geometry.MappingValidator2SAT;
import geometry.Tree;

public class Main {
    public static void main(String[] args) {
        int n = 5;
        int k = 4;

        Iterator<Tree> treeGen = TreeIterator.iterable(n, k).iterator();
        MappingValidator2SAT mappingValidator = new MappingValidator2SAT(n);

        Dumper dumper = new Dumper();

        int[] cnt = new int[1];
        new BendsGenerator(
                treeGen,
                mappingValidator,
                n,
                (tree, points, mapping, solution) -> {
                    int i = ++cnt[0];
                    if (mapping == null) {
                        throw new RuntimeException("" +
                                tree + " " + points + " "
                                + Arrays.toString(mapping));
                    }

                    System.out.println("" +
                            i + " " + points + " " +
                            Arrays.toString(mapping));

                    dumper.draw(i, tree, new ArrayList<>(points), mapping, solution);
                }).run();
    }
}
