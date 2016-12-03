import generators.*;
import geometry.Point;
import geometry.Tree;
import math.Interval;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    static Set<Point> generatePoints() {
        PointSet generator = new RandomPointSet(10, new Interval(0, 100), new Interval(0, 100));
        return generator.generate();
    }

    public static void main(String[] args) {

        // read from file or System.in
        Scanner scanner = null;
        if (args.length == 1) {
            String path = args[0];
            try {
                scanner = new Scanner(new File(path));
            } catch (FileNotFoundException e) {
                System.err.println("Could not read file (" + path + ") : " + e);
                return;
            }
        } else {
            scanner = new Scanner(System.in);
        }

        int n = scanner.nextInt();
        int k = scanner.nextInt();

        PointSet pointset = new RandomPointSet(n, new Interval(0, 100), new Interval(0, 100));
        Set<Point> P = pointset.generate();

        TreeCodeGenerator generator = new TreeCodeGenerator(n, k);
        for (int[] code : generator) {
            System.out.println("(" + Arrays.toString(code) + ")");
            Iterable<int[]> sequences = new SequenceGenerator(code);
            for (int[] sequence : sequences) {
                System.out.println(Arrays.toString(sequence));
                //Tree T = TreeBuilder.fromSequence(sequence);

                // generate embeddings of T onto P

                // validate embedding
            }
            break;
        }
    }
}
