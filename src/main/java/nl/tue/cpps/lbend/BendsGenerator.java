package nl.tue.cpps.lbend;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.geometry.MappingValidator2SAT;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;
import nl.tue.cpps.lbend.mappings.MappingFinder;
import nl.tue.cpps.lbend.mappings.QuickMappingFinder;

public class BendsGenerator {
    private static final Tree DONE = new Tree(1);

    private final Executor executor;
    private final int n;
    private final Iterator<Tree> trees;
    private final Callback cb;

    @ThreadSafe
    interface Callback {
        void on(
                Tree tree, List<Point> points,
                @Nullable int[] mapping, @Nullable boolean[] solution);
    }

    BendsGenerator(
            Executor executor,
            Iterator<Tree> trees,
            int n,
            Callback cb) {
        this.executor = executor;
        this.n = n;
        this.trees = trees;
        this.cb = cb;
    }

    public void run(int nThreads) {
        BlockingQueue<Tree> Q = new ArrayBlockingQueue<>(4 * nThreads);

        CountDownLatch doneSignal = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Runner runner = new Runner(Q, doneSignal, n, cb);
            executor.execute(runner);
        }

        while (trees.hasNext()) {
            Q.add(trees.next());
        }

        for (int i = 0; i < nThreads; i++) {
            Q.add(DONE);
        }

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class Runner implements Runnable {
        private final BlockingQueue<Tree> Q;
        private final CountDownLatch doneSignal;
        @SuppressWarnings("unused")
        private final int n;
        private final int[] mapping;
        private final MappingValidator2SAT validator;
        private final Callback cb;
        private final PermutedPointGenerator pointGen;

        public Runner(
                BlockingQueue<Tree> Q,
                CountDownLatch doneSignal,
                int n,
                Callback cb) {
            this.Q = Q;
            this.doneSignal = doneSignal;
            this.n = n;
            this.mapping = new int[n];
            this.validator = new MappingValidator2SAT(n);
            this.pointGen = new PermutedPointGenerator(n);
            this.cb = cb;
        }

        @Override
        public void run() {
            while (true) {
                Tree t;
                try {
                    t = Q.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

                if (t == DONE) {
                    break;
                }

                run(t);
            }

            doneSignal.countDown();
        }

        private void run(Tree tree) {
            Iterator<List<Point>> it = pointGen.generate();
            while (it.hasNext()) {
                List<Point> point = it.next();
                boolean[] solution = run(tree, point);
                cb.on(tree, point, mapping, solution);
            }
        }

        private boolean[] run(Tree tree, List<Point> points) {
            // TODO: refactor such that it becomes For each pointset -> For each tree
            // instead of For each tree -> For each pointset
            // to make use of mapping finders optimizations per point set
            MappingFinder finder = new QuickMappingFinder(points);
            if (finder.findMapping(tree, mapping)) {
                // found valid mapping

                // get placement of bends
                // TODO: we do not want to do this for each tree, pointset and mapping,
                // as finding A mapping is enough, and ensures a valid placement of bends is possible
                return run(tree, points, mapping);
            }

            return null;
        }

        private boolean[] run(Tree tree, List<Point> points, int[] mapping) {
            return validator.validateWithSolution(
                    tree,
                    mapping,
                    points);
        }
    }
}
