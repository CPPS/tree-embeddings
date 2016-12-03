import generators.*;
import geometry.Point;
import geometry.Tree;
import math.Interval;

import java.util.*;

public class Main {

    static Set<Point> generatePoints() {
        PointSet generator = new RandomPointSet(10, new Interval(0, 100), new Interval(0, 100));
        return generator.generate();
    }

    public static void main(String[] args) {

        // input
        int n = 5;
        int k = 3;
        int min_x = 0;
        int max_x = 100;
        int min_y = 0;
        int max_y = 100;

        PointSet pointset = new RandomPointSet(n, new Interval(min_x, max_x), new Interval(min_y, max_y));
        Set<Point> P = pointset.generate();

        TreeCodeGenerator generator = new TreeCodeGenerator(n, k);
        for (int[] code : generator) {
            Iterable<int[]> sequences = new SequenceGenerator(code);
            for (int[] sequence : sequences) {
                Tree T = TreeBuilder.fromSequence(sequence);

                // generate embeddings of T onto P

                // validate embedding
            }
        }
    }
}
