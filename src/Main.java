import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

import generators.PointSet;
import generators.RandomPointSet;
import generators.SequenceGenerator;
import generators.TreeBuilder;
import generators.TreeCodeGenerator;
import geometry.Point;
import geometry.Tree;
import math.Interval;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 1) {
            String path = args[0];
            try (Scanner scanner = new Scanner(new File(path), "UTF-8")) {
                run(scanner);
            } catch (FileNotFoundException e) {
                System.err.println("Could not read file (" + path + ") : " + e);
                return;
            }
        } else {
            run(new Scanner(System.in, "UTF-8"));
        }
    }

    private static void run(Scanner scanner) {
        int n = scanner.nextInt();
        int k = scanner.nextInt();

        PointSet pointset = new RandomPointSet(n, new Interval(0, 100), new Interval(0, 100));
        @SuppressWarnings("unused")
        Set<Point> P = pointset.generate();

        TreeCodeGenerator generator = new TreeCodeGenerator(n, k);
        for (int[] code : generator) {
            Iterable<int[]> sequences = new SequenceGenerator(code);
            for (int[] sequence : sequences) {
                @SuppressWarnings("unused")
                Tree T = TreeBuilder.fromSequence(sequence);

                // generate embeddings of T onto P

                // validate embedding
            }
        }
    }
}
