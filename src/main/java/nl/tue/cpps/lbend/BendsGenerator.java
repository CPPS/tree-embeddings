package nl.tue.cpps.lbend;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.naming.TimeLimitExceededException;

import nl.tue.cpps.lbend.generator.point.PointSetGenerator;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;
import nl.tue.cpps.lbend.mappings.MappingFinder;
import nl.tue.cpps.lbend.mappings.QuickMappingFinder;

public class BendsGenerator {
    private final Executor executor;
    private final int n;
    private final long maxTimeForMappingMS;
    private final Iterable<Tree> trees;
    private final Callback cb;
    private final PointSetGenerator pointGenerator;

    @ThreadSafe
    interface Callback {
        void on(
                Tree tree, List<Point> points, @Nullable int[] mapping, boolean overTime);
    }

    BendsGenerator(
            Executor executor,
            Iterable<Tree> trees,
            int n,
            long maxTimeForMappingMS,
            Callback cb,
            PointSetGenerator pointGenerator) {
        this.executor = executor;
        this.n = n;
        this.maxTimeForMappingMS = maxTimeForMappingMS;
        this.trees = trees;
        this.cb = cb;
        this.pointGenerator = pointGenerator;
    }

    public void run(int nThreads) {

        CountDownLatch doneSignal = new CountDownLatch(nThreads);
        List<Iterator<List<Point>>> pointGen = pointGenerator.splitGenerator(nThreads);

        for (int i = 0; i < nThreads; i++) {
            executor.execute(new Runner(
                    pointGen.get(i),
                    trees,
                    doneSignal,
                    n,
                    maxTimeForMappingMS,
                    cb));
        }

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class Runner implements Runnable {
        private final MappingFinder finder;

        private final Iterator<List<Point>> points;
        private final Iterable<Tree> trees;
        private final CountDownLatch doneSignal;
        @SuppressWarnings("unused")
        private final int n;
        private final long maxTimeForMappingMS;
        private final int[] mapping;
        private final Callback cb;

        public Runner(
                Iterator<List<Point>> points,
                Iterable<Tree> trees,
                CountDownLatch doneSignal,
                int n,
                long maxTimeForMappingMS,
                Callback cb) {
            this.finder = new QuickMappingFinder(n);
            this.points = points;
            this.trees = trees;
            this.doneSignal = doneSignal;
            this.n = n;
            this.maxTimeForMappingMS = maxTimeForMappingMS;
            this.mapping = new int[n];
            this.cb = cb;
        }

        @Override
        public void run() {
            while (points.hasNext()) {
                run(points.next());
            }

            doneSignal.countDown();
        }

        private void run(List<Point> point) {
            finder.setPointSet(point);

            Iterator<Tree> it = trees.iterator();
            while (it.hasNext()) {
                Tree tree = it.next();
                boolean solution, overTime;
                try {
                    solution = run(finder, tree, point);
                    overTime = false;
                } catch (TimeLimitExceededException e) {
                    solution = false;
                    overTime = true;
                }
                cb.on(tree, point, solution ? mapping : null, overTime);
            }
        }

        private boolean run(
                MappingFinder finder,
                Tree tree,
                List<Point> points) throws TimeLimitExceededException {

            return finder.findMapping(tree, mapping, maxTimeForMappingMS);
        }
    }
}
