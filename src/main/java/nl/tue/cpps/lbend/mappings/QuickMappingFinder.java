package nl.tue.cpps.lbend.mappings;

import java.util.List;
import java.util.Random;

import nl.tue.cpps.lbend.geometry.LBend;
import nl.tue.cpps.lbend.geometry.MappingValidator2SAT;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

import javax.naming.TimeLimitExceededException;

public final class QuickMappingFinder implements MappingFinder {
    private final Random random = new Random(0);

    private final MappingValidator2SAT validator;
    private final AbstractMappingFinder fastIncorrectBacktracker;
    private final AbstractMappingFinder correctBacktracker;

    private List<Point> points;

    public QuickMappingFinder(int n) {
        validator = new MappingValidator2SAT(n);
        fastIncorrectBacktracker = new MappingBacktrackerFastIncomplete(
                n, validator);
        correctBacktracker = new MappingBacktrackerCorrect(n);
    }

    @Override
    public MappingFinder setPointSet(List<Point> points) {
        LBend[][][] bends = LBend.createAllBends(points);
        fastIncorrectBacktracker.setPoints(points, bends);
        correctBacktracker.setPoints(points, bends);

        this.points = points;

        return this;
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping, long maxTimeMS) throws TimeLimitExceededException {
//        if (getMappingByShuffle(points, tree, 0, mapping)) {
//            return true;
//        }
        if (fastIncorrectBacktracker.findMapping(tree, mapping, maxTimeMS)) {
            return true;
        }
        if (correctBacktracker.findMapping(tree, mapping, maxTimeMS)) {
            return true;
        }

        return false;
    }

    private boolean getMappingByShuffle(
            List<Point> points, Tree tree,
            int maxShuffles,
            int[] mapping) {
        int n = points.size();
        for (int i = 0; i < n; i++) {
            mapping[i] = i;
        }

        while (maxShuffles-- > 0) {
            shuffle(mapping);
            if (validator.validate(tree, mapping, points)) {
                return true;
            }
        }

        return false;
    }

    private void shuffle(int[] array) {
        int size = array.length;
        for (int i = size; i > 1; i--) {
            int temp = array[i - 1];
            int idx = random.nextInt(i);
            array[i - 1] = array[idx];
            array[idx] = temp;
        }
    }

}
