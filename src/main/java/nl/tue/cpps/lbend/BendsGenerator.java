package nl.tue.cpps.lbend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.geometry.FixedPoint;
import nl.tue.cpps.lbend.geometry.MappingValidator2SAT;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;
import nl.tue.cpps.lbend.mappings.MappingFinder;
import nl.tue.cpps.lbend.mappings.QuickMappingFinder;

public class BendsGenerator {
    private static final List<Point> DONE = Collections.unmodifiableList(
            new ArrayList<>());

    private final Executor executor;
    private final int n;
    private final Iterable<Tree> trees;
    private final Callback cb;

    @ThreadSafe
    interface Callback {
        void on(
                Tree tree, List<Point> points,
                @Nullable int[] mapping, @Nullable boolean[] solution);
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
        BlockingQueue<List<Point>> Q = new ArrayBlockingQueue<>(4 * nThreads);

        CountDownLatch doneSignal = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Runner runner = new Runner(trees, Q, doneSignal, n, cb);
            executor.execute(runner);
        }

        Iterator<List<Point>> pointGen = new PermutedPointGenerator(n).generate();

        try {
            while (pointGen.hasNext()) {
                Q.put(copyPoints(pointGen.next()));
            }

            for (int i = 0; i < nThreads; i++) {
                Q.put(DONE);
            }

            doneSignal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Point> copyPoints(List<Point> next) {
        List<Point> out = new ArrayList<>(next.size());

        for (Point n : next) {
            out.add(FixedPoint.of(n));
        }

        return out;
    }

    private static final class Runner implements Runnable {
        private final Iterable<Tree> trees;
        private final BlockingQueue<List<Point>> Q;
        private final CountDownLatch doneSignal;
        @SuppressWarnings("unused")
        private final int n;
        private final int[] mapping;
        private final MappingValidator2SAT validator;
        private final Callback cb;

        public Runner(
                Iterable<Tree> trees,
                BlockingQueue<List<Point>> Q,
                CountDownLatch doneSignal,
                int n,
                Callback cb) {
            this.trees = trees;
            this.Q = Q;
            this.doneSignal = doneSignal;
            this.n = n;
            this.mapping = new int[n];
            this.validator = new MappingValidator2SAT(n);
            this.cb = cb;
        }

        @Override
        public void run() {
            while (true) {
                List<Point> t;
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

        private void run(List<Point> point) {
            MappingFinder finder = new QuickMappingFinder(point);

            Iterator<Tree> it = trees.iterator();
            while (it.hasNext()) {
                Tree tree = it.next();
                boolean[] solution = run(finder, tree, point);
                cb.on(tree, point, mapping, solution);
            }
        }

        private boolean[] run(
                MappingFinder finder,
                Tree tree,
                List<Point> points) {
            if (finder.findMapping(tree, mapping)) {
                // found valid mapping

                // get placement of bends
                // TODO: we do not want to do this for each tree, pointset and
                // mapping,
                // as finding A mapping is enough, and ensures a valid placement
                // of bends is possible
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
