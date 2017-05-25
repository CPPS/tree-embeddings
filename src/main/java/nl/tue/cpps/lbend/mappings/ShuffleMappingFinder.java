package nl.tue.cpps.lbend.mappings;

import java.util.List;
import java.util.Random;

import javax.naming.TimeLimitExceededException;

import lombok.RequiredArgsConstructor;
import nl.tue.cpps.lbend.geometry.MappingValidator2SAT;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

@RequiredArgsConstructor
public class ShuffleMappingFinder extends AbstractMappingFinder {
    private final Random random = new Random(0);

    private final MappingValidator2SAT validator;

    private List<Point> points;

    @Override
    public MappingFinder setPointSet(List<Point> points) {
        this.points = points;
        return this;
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping, long maxTimeMS) throws TimeLimitExceededException {
        return getMappingByShuffle(points, tree, 0, mapping);
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
