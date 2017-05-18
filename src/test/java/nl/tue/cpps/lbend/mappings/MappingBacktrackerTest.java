package nl.tue.cpps.lbend.mappings;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import nl.tue.cpps.lbend.generator.IntQuickPerm;
import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.geometry.MappingValidator2SAT;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

@Ignore
public class MappingBacktrackerTest {
    public static List<Tree> getListOfTestTrees() {
        return Arrays.asList(
                new Tree(7).connect(0, 1).connect(0, 2).connect(1, 3).connect(1, 4).connect(2, 5).connect(2, 6),
                new Tree(2).connect(0, 1),
                new Tree(3).connect(0, 1).connect(0, 2),
                new Tree(3).connect(0, 1).connect(1, 2),
                new Tree(10).connect(0, 1).connect(0, 2).connect(0, 3).connect(1, 4).connect(1, 5).connect(1, 6).connect(2, 7).connect(2, 8).connect(2, 9),
                new Tree(13).connect(0, 1).connect(0, 2).connect(0, 3).connect(1, 4).connect(1, 5).connect(1, 6).connect(2, 7).connect(2, 8).connect(2, 9).connect(3, 10).connect(3, 11).connect(3, 12)
        );
    }

    @Test
    public void testGetValidMapping() throws Exception {

        for (Tree tree : getListOfTestTrees()) {

            int n = tree.size();
            PermutedPointGenerator pointGenerator = new PermutedPointGenerator(n);

            int pointSetIdx = 0;
            for (Iterator<List<Point>> iter = pointGenerator.generate(); iter.hasNext(); pointSetIdx++) {
                List<Point> points = iter.next();

                if (pointSetIdx % 1000 == 0) System.out.println("n=" + n + " #" + pointSetIdx /*+ " " + points*/);

                QuickMappingFinder mapper = new QuickMappingFinder(n);
                mapper.setPointSet(points);
                int[] mapping = mapper.findMapping(tree);
                if (mapping == null) {
                    System.err.println(points);
                    System.err.println(tree);
                    throw new RuntimeException("Found no mapping!");
                }

                /** (Wrong) old backtracking algorithm */
//                time = System.currentTimeMillis();
//                int[] mapping1 = mappingBacktracker.getValidMapping();
//                long duration1 = System.currentTimeMillis() - time;
//                System.out.println("1 - " + Arrays.toString(mapping1) + " " + duration1 + "ms");

                /** New backtracking algorithm */
//                if (mapping1 == null) {
//                time = System.currentTimeMillis();
//                    System.err.println("#" + pointSetIdx + " no mapping1 found");
//                    int[] mapping2 = mappingBacktracker.getValidMapping2();
//                long duration2 = System.currentTimeMillis() - time;
//                System.out.println("2 - " + Arrays.toString(mapping2) + " " + duration2 + "ms");
//                    if (mapping2 == null) {
//                        System.err.println(points);
//                        System.err.println(tree);
//                        throw new RuntimeException("Found no mapping!");
//                    } else validateMapping(tree, points, mapping2);
//                } else validateMapping(tree, points, mapping1);

                /** 2-SAT Permutation algorithm */
//                time = System.currentTimeMillis();
//                int[] mapping3 = getMapping2SAT(points, tree);
//                long duration3 = System.currentTimeMillis() - time;
//                System.out.println("3 - " + Arrays.toString(mapping3) + " " + duration3 + "ms");

//                System.out.println();

//                if (mapping1 == null) {
//                    System.err.println("mapping1 is null");
//                    int[] mapping2 = mappingBacktracker.getValidMapping2();
//                    System.err.println("mapping2: " + Arrays.toString(mapping2));
//                }
//                validateMapping(tree, points, mapping1);
//                validateMapping(tree, points, mapping2);

            }
        }

    }

    @SuppressWarnings("unused")
    private int[] getMapping2SAT(List<Point> points, Tree tree) {
        int n = points.size();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) array[i] = i;

        IntQuickPerm perm = new IntQuickPerm(array);

        MappingValidator2SAT validator = new MappingValidator2SAT(n);

        while (perm.hasNext()) {
            int[] mapping = perm.next();
            if (validator.validate(tree, mapping, points)) {
                return mapping;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private static void validateMapping(Tree tree, List<Point> points, int[] mapping) {
        int n = mapping.length;
        MappingValidator2SAT validator = new MappingValidator2SAT(n);

        if (!validator.validate(tree, mapping, points)) {
            throw new AssertionError("Invalid mapping, 2-SAT can't find non-intersecting positioning of l-bends");
        }

        boolean[] contained = new boolean[n];
        for (int i = 0; i < n; i++) {
            int val = mapping[i];
            if (val >= 0 && val < n)
                contained[mapping[i]] = true;
        }

        for (int i = 0; i < n; i++) {
            if (!contained[i]) throw new AssertionError("Point " + i + " does not have a node mapped to it");
        }
    }
}