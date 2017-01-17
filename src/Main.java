import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.common.collect.ImmutableList;

import generators.IntQuickPerm;
import generators.PointGenerator;
import generators.RandomPointGenerator;
import generators.SequenceGenerator;
import generators.TreeBuilder;
import generators.TreeCodeGenerator;
import geometry.MappingValidator;
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
        // Amount of points to generate
        int n = scanner.nextInt();
        int k = scanner.nextInt();

        PointGenerator pointset = new RandomPointGenerator(
                n, new Interval(0, 100), new Interval(0, 100));
        List<Point> P = ImmutableList.copyOf(pointset.generate());

        TreeCodeGenerator generator = new TreeCodeGenerator(n, k);
        for (int[] code : generator) {
            System.out.println(" > ");
            Iterable<int[]> sequences = new SequenceGenerator(code);
            for (int[] sequence : sequences) {
                Tree t = TreeBuilder.fromSequence(sequence);

                System.out.println(" >> ");

                // node -> point mappings
                int[] mappings = new int[t.size()];
                for (int i = 0; i < mappings.length; i++) {
                    mappings[i] = i;
                }

                // generate embeddings of T onto P
                IntQuickPerm it = new IntQuickPerm(mappings);
                while (it.hasNext()) {
                    int[] mapping = it.next();

                    System.out.println("Mapping " + Arrays.toString(mapping));

                    // validate embedding
                    if (new MappingValidator().validate(t, mapping, P)) {
                        System.out.println("Valid mapping: " + Arrays.toString(mapping));
                        break;
                    }
                }

            }
        }

        System.out.println(" < ");
    }
}
