import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

import generators.PointGenerator;
import generators.RandomPointGenerator;
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

        PointGenerator pointset = new RandomPointGenerator(
                n, new Interval(0, 100), new Interval(0, 100));
        @SuppressWarnings("unused")
        Iterator<Point> P = pointset.generate();

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
