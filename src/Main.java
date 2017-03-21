import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import generators.TreeReader;
import geometry.Tree;

public class Main {
    public static void main(String[] args) throws Exception {
        int n = 8;
        File dir = new File("trees");

        Iterator<Tree> treeGen = new TreeReader(dir, n);

        int nCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nCores);

        Dumper dumper = new Dumper();

        AtomicInteger cnt = new AtomicInteger(0);
        new BendsGenerator(
                executor,
                treeGen,
                n,
                (tree, points, mapping, solution) -> {
                    int i = cnt.getAndIncrement();
                    if (mapping == null) {
                        throw new RuntimeException("" +
                                tree + " " + points + " "
                                + Arrays.toString(mapping));
                    }

                    System.out.println("" +
                            i + " " + points + " " +
                            Arrays.toString(mapping));

                    dumper.draw(i, tree, new ArrayList<>(points), mapping, solution);
                }).run(nCores);
    }
}
