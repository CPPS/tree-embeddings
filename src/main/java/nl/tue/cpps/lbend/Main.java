package nl.tue.cpps.lbend;

import java.io.File;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Stopwatch;

import nl.tue.cpps.lbend.geometry.Tree;
import nl.tue.cpps.lbend.tree.TreeIterable;

public class Main {
    private static final NumberFormat NR_FORMAT = NumberFormat.getNumberInstance();

    // Recommended JVM flags:
    // -server -XX:NewSize=5G -Xms6G -Xmx6G
    public static void main(String[] args) throws Exception {
        int n = 8;

        run(n, new TreeIterable(new File("compact-trees/" + n + ".tree")));
    }

    private static void run(int n, Iterable<Tree> treeGen) {
        int nCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nCores);

        // Dumper dumper = new DummyDumper();

        Stopwatch stopwatch = Stopwatch.createStarted();
        AtomicInteger cnt = new AtomicInteger(0);
        new BendsGenerator(
                executor,
                treeGen,
                n,
                (tree, points, mapping) -> {
                    int i = cnt.getAndIncrement();
                    if (mapping == null) {
                        throw new RuntimeException(tree + " " + points);
                    }
                    if (i % 10000 == 0) {
                        System.out.println(NR_FORMAT.format(i));
                    }

                    // Printing is too slow :(
                    /*
                     * System.out.println("" + i + " " + points + " " +
                     * Arrays.toString(mapping));
                     */

                    // dumper.draw(i, tree, points, mapping);
                }).run(nCores);

        long ms = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println(
                "Took: " + NR_FORMAT.format(ms) + "ms (" +
                        NR_FORMAT.format(cnt.get()) + ")");
        // Initial time for 7 . . . . -> 83,264ms
        // After removing lambda in dfs: 61,608ms
        // After inserting mapping backtracker: 2,727ms
        // After per-pointset backtracking + print removal: 1.531

        executor.shutdown();
    }
}
