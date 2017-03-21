import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import generator.IntQuickPerm;
import generator.point.PermutedPointGenerator;
import geometry.MappingValidator2SAT;
import geometry.Point;
import geometry.Tree;

public class BendsGenerator {
    private static final Tree DONE = new Tree(1);

    private final Executor executor;
    private final int n;
    private final Iterator<Tree> trees;
    private final Callback cb;

    @ThreadSafe
    interface Callback {
        void on(
                Tree tree, Collection<Point> points,
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

        List<Runner> runners = new ArrayList<>(nThreads);
        for (int i = 0; i < nThreads; i++) {
            Runner runner = new Runner(Q, doneSignal, n, cb);
            runners.add(runner);
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
        private final int n;
        private final int[] mapping;
        private final IntQuickPerm mapper;
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
            this.mapper = new IntQuickPerm(mapping);
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
            for (int i = 0; i < n; i++) {
                mapping[i] = i;
            }

            mapper.reset();
            while (mapper.hasNext()) {
                mapper.next();
                boolean[] solution = run(tree, points, mapping);
                if (solution != null) {
                    return solution;
                }
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
