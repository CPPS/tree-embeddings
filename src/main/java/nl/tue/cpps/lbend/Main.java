package nl.tue.cpps.lbend;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Stopwatch;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionDescriptor;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.generator.point.PointSetGenerator;
import nl.tue.cpps.lbend.geometry.Tree;
import nl.tue.cpps.lbend.tree.TreeIterable;

public class Main {
    private static final NumberFormat NR_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    private static OptionParser parser = new OptionParser();

    private static ArgumentAcceptingOptionSpec<Integer> N = parser
            .accepts("n")
            .withRequiredArg()
            .ofType(Integer.class)
            .required();
    private static ArgumentAcceptingOptionSpec<Long> MAX_MAP_TIME = parser
            .accepts(
                    "max-time",
                    "The maximum amount of time to spend on mapping")
            .withOptionalArg()
            .ofType(Long.class)
            .defaultsTo(5_000L); // 5 sec;
    private static ArgumentAcceptingOptionSpec<Integer> OFFSET = parser
            .accepts(
                    "point-offset",
                    "The offset within the generated point offset files.")
            .withRequiredArg()
            .ofType(Integer.class)
            .required();
    private static ArgumentAcceptingOptionSpec<Integer> N_THREADS = parser
            .accepts(
                    "threads",
                    "The amount of threads to use. " +
                            "Defaults to the amount of cores available.")
            .withOptionalArg()
            .ofType(Integer.class)
            .defaultsTo(-1); // -1 => detect

    // Recommended JVM flags:
    // -server -XX:NewSize=5G -Xms6G -Xmx6G
    public static void main(String[] args) throws Exception {
        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException e) {
            System.err.println(e.getLocalizedMessage());
            return;
        }

        int n = options.valueOf(N);
        long maxTimeForMapping = options.valueOf(MAX_MAP_TIME);
        int offset = options.valueOf(OFFSET);

        int nThreads = options.valueOf(N_THREADS);
        if (nThreads <= 0) {
            nThreads = Runtime.getRuntime().availableProcessors();
        }

        TreeIterable trees = new TreeIterable(new File(
                "compact-trees/" + n + ".tree"));
        PointSetGenerator pointGen = new ReadingPointGenerator(n, offset);
        // pointGen = new PermutedPointGenerator(n);

        run(
                n,
                maxTimeForMapping,
                trees, pointGen, nThreads);
    }

    private static void run(
            int n, long maxTimeForMappingMS,
            Iterable<Tree> treeGen, PointSetGenerator pointGen, int nCores)
            throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(nCores);

        // Dumper dumper = new DummyDumper();

        Stopwatch stopwatch = Stopwatch.createStarted();
        AtomicInteger cnt = new AtomicInteger(0);
        new BendsGenerator(
                executor,
                treeGen,
                n,
                maxTimeForMappingMS,
                (tree, points, mapping, overTime) -> {
                    int i = cnt.getAndIncrement();
                    if (mapping == null) {
                        if (overTime) {
                            System.err.println("Overtime: " + tree + " " + points);
                        } else {
                            throw new RuntimeException(tree + " " + points);
                        }
                    }
                    if (i % 10000 == 0) {
                        System.out.println(NR_FORMAT.format(i));
                    }

                    // Printing is too slow :(

                    // System.out.println("" + i + " " + points + " " +
                    // Arrays.toString(mapping));

                    // dumper.draw(i, tree, points, mapping);
                },
                pointGen).run(nCores);

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
