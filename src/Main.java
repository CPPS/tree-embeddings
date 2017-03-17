import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import generators.TreeReader;
import geometry.MappingValidator2SAT;
import geometry.Tree;

public class Main {
    public static void main(String[] args) throws Exception {
        int n = 8;
        File dir = new File("trees");

        Iterator<Tree> treeGen = new TreeReader(dir, n);
        MappingValidator2SAT mappingValidator = new MappingValidator2SAT(n);

        Dumper dumper = new Dumper();

        int[] cnt = new int[1];
        new BendsGenerator(
                treeGen,
                mappingValidator,
                n,
                (tree, points, mapping, solution) -> {
                    int i = ++cnt[0];
                    if (mapping == null) {
                        throw new RuntimeException("" +
                                tree + " " + points + " "
                                + Arrays.toString(mapping));
                    }

                    System.out.println("" +
                            i + " " + points + " " +
                            Arrays.toString(mapping));

                    dumper.draw(i, tree, new ArrayList<>(points), mapping, solution);
                }).run();
    }
}
