package nl.tue.cpps.lbend;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;
import nl.tue.cpps.lbend.mappings.MappingFinder;
import nl.tue.cpps.lbend.mappings.QuickMappingFinder;

public class BendsGenerator {
    private final Executor executor;
    private final int n;
    private final Iterable<Tree> trees;
    private final Callback cb;

    @ThreadSafe
    interface Callback {
        void on(
                Tree tree, List<Point> points, @Nullable int[] mapping);
    }

    BendsGenerator(
            Executor executor,
            Iterable<Tree> trees,
            int n,
            Callback cb) {
        this.executor = executor;
        this.n = n;
        this.trees = trees;
        this.cb = cb;
    }

    public void run(int nThreads) {

        CountDownLatch doneSignal = new CountDownLatch(nThreads);
        List<Iterator<List<Point>>> pointGen = new PermutedPointGenerator(n).splitGenerator(nThreads);

        for (int i = 0; i < nThreads; i++) {
            executor.execute(new Runner(
                    pointGen.get(i),
                    trees,
                    doneSignal,
                    n,
                    cb));
        }

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class Runner implements Runnable {
        private final MappingFinder finder = new QuickMappingFinder();

        private final Iterator<List<Point>> points;
        private final Iterable<Tree> trees;
        private final CountDownLatch doneSignal;
        @SuppressWarnings("unused")
        private final int n;
        private final int[] mapping;
        private final Callback cb;

        public Runner(
                Iterator<List<Point>> points,
                Iterable<Tree> trees,
                CountDownLatch doneSignal,
                int n,
                Callback cb) {
            this.points = points;
            this.trees = trees;
            this.doneSignal = doneSignal;
            this.n = n;
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
                boolean solution = run(finder, tree, point);
                cb.on(tree, point, solution ? mapping : null);
            }
        }

        private boolean run(
                MappingFinder finder,
                Tree tree,
                List<Point> points) {
            return finder.findMapping(tree, mapping);
        }
    }
}
