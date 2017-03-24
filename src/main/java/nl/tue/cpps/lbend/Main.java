package nl.tue.cpps.lbend;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Stopwatch;

import nl.tue.cpps.lbend.generator.TreeReader;
import nl.tue.cpps.lbend.geometry.Tree;

public class Main {
    // Recommended JVM flags:
    // -server -XX:NewSize=5G -Xms6G -Xmx6G
    public static void main(String[] args) throws Exception {
        int n = 5;
        File dir = new File("trees");

        Iterator<Tree> treeGen = new TreeReader(dir, n);

        int nCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nCores);

        Dumper dumper = new DummyDumper();

        Stopwatch stopwatch = Stopwatch.createStarted();
        AtomicInteger cnt = new AtomicInteger(0);
        new BendsGenerator(
                executor,
                treeGen,
                n,
                (tree, points, mapping, solution) -> {
                    int i = cnt.getAndIncrement();
                    if (mapping == null) {
                        throw new RuntimeException(tree + " " + points);
                    }

                    System.out.println("" +
                            i + " " + points + " " +
                            Arrays.toString(mapping));

                    dumper.draw(i, tree, points, mapping, solution);
                }).run(nCores);

        long ms = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("Took: " + ms + "ms");
        // Initial time for 7 . . . . -> 83,264ms
        // After removing lambda in dfs: 61,608ms
        // After inserting mapping backtracker: 2,727ms

        executor.shutdown();
    }
}