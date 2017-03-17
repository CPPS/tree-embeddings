import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import generators.IntQuickPerm;
import generators.PermutedPointGenerator;
import geometry.MappingValidator2SAT;
import geometry.Point;
import geometry.Tree;

public class BendsGenerator {
    private final int n;
    private final Iterator<Tree> trees;
    private final MappingValidator2SAT validator;
    private final int[] mapping;
    private final Callback cb;

    interface Callback {
        void on(
                Tree tree, Collection<Point> points,
                @Nullable int[] mapping, @Nullable boolean[] solution);
    }

    BendsGenerator(
            Iterator<Tree> trees,
            MappingValidator2SAT validator,
            int n,
            Callback cb) {
        this.n = n;
        this.trees = trees;
        this.validator = validator;
        this.mapping = new int[n];
        this.cb = cb;
    }

    public void run() {
        while (trees.hasNext()) {
            run(trees.next());
        }
    }

    private void run(Tree tree) {
        PermutedPointGenerator pointGen = new PermutedPointGenerator(n);
        Iterator<Collection<Point>> it = pointGen.generate();
        while (it.hasNext()) {
            Collection<Point> point = it.next();
            boolean[] solution = run(tree, point);
            cb.on(tree, point, mapping, solution);
        }
    }

    private boolean[] run(Tree tree, Collection<Point> points) {
        for (int i = 0; i < n; i++) {
            mapping[i] = i;
        }

        IntQuickPerm mapper = new IntQuickPerm(mapping);
        while (mapper.hasNext()) {
            mapper.next();
            boolean[] solution = run(tree, points, mapping);
            if (solution != null) {
                return solution;
            }
        }

        return null;
    }

    private boolean[] run(Tree tree, Collection<Point> points, int[] mapping) {
        return validator.validateWithSolution(tree, mapping, new ArrayList<>(points));
    }

}
