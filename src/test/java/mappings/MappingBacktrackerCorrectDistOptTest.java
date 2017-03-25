package mappings;

import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;
import nl.tue.cpps.lbend.mappings.MappingBacktrackerCorrect;
import nl.tue.cpps.lbend.mappings.MappingBacktrackerCorrectDistOpt;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class MappingBacktrackerCorrectDistOptTest {

    @Test
    public void testFindMapping() throws Exception {

        MappingBacktrackerCorrect backtrackerCorrect = new MappingBacktrackerCorrect();
        MappingBacktrackerCorrectDistOpt backtrackerDist = new MappingBacktrackerCorrectDistOpt();

//        for (Tree tree : MappingBacktrackerTest.getListOfTestTrees()) {
            Tree tree = new Tree(13).connect(0, 1).connect(0, 2).connect(0, 3).connect(1, 4).connect(1, 5).connect(1, 6).connect(2, 7).connect(2, 8).connect(2, 9).connect(3, 10).connect(3, 11).connect(3, 12);
            int n = tree.size();
            PermutedPointGenerator pointGenerator = new PermutedPointGenerator(n);

            int pointSetIdx = 0;
            for (Iterator<List<Point>> iter = pointGenerator.generate(); iter.hasNext(); pointSetIdx++) {
                List<Point> points = iter.next();

                backtrackerCorrect.setPointSet(points);
                backtrackerDist.setPointSet(points);

                if (pointSetIdx % 1000 == 0) System.out.println("n=" + n + " #" + pointSetIdx /*+ " " + points*/);
                long time;

                time = System.currentTimeMillis();
                int[] mapping1 = backtrackerCorrect.findMapping(tree);
                long duration1 = System.currentTimeMillis() - time;
                System.out.println("1: " + duration1 + "ms");
                if (mapping1 == null) {
                    System.err.println(points);
                    System.err.println(tree);
                    throw new RuntimeException("Found no mapping1!");
                }

                time = System.currentTimeMillis();
                int[] mapping2 = backtrackerDist.findMapping(tree);
                long duration2 = System.currentTimeMillis() - time;
                System.out.println("2: " + duration2 + "ms");
                if (mapping2 == null) {
                    System.err.println(points);
                    System.err.println(tree);
                    throw new RuntimeException("Found no mapping2!");
                }
            }
//        }
    }
}