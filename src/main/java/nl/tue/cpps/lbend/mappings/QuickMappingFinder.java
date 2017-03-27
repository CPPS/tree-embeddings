package nl.tue.cpps.lbend.mappings;

import java.util.List;
import java.util.Random;

import nl.tue.cpps.lbend.geometry.MappingValidator2SAT;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

public class QuickMappingFinder implements MappingFinder {
    private final Random random = new Random(0);

    private final MappingFinder fastIncorrectBacktracker = new MappingBacktrackerFastIncorrect();
    private final MappingFinder correctBacktracker = new MappingBacktrackerCorrect();

    private List<Point> points;

    @Override
    public MappingFinder setPointSet(List<Point> points) {
        fastIncorrectBacktracker.setPointSet(points);
        correctBacktracker.setPointSet(points);
        this.points = points;

        return this;
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping) {
        if (getMappingByShuffle(points, tree, 0, mapping)) {
            return true;
        }
        if (fastIncorrectBacktracker.findMapping(tree, mapping)) {
            return true;
        }
        if (correctBacktracker.findMapping(tree, mapping)) {
            return true;
        }

        return false;
    }

    private boolean getMappingByShuffle(List<Point> points, Tree tree, int maxShuffles, int[] mapping) {
        MappingValidator2SAT validator = new MappingValidator2SAT(points.size());
        int n = points.size();
        for (int i = 0; i < n; i++)
            mapping[i] = i;

        while (maxShuffles-- > 0) {
            shuffle(mapping);
            if (validator.validate(tree, mapping, points)) {
                // System.err.println("yes shuffle");
                return true;
            }
        }
        // System.err.println("no shuffle");
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
