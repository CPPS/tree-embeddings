package mappings;

import geometry.MappingValidator2SAT;
import geometry.Point;
import geometry.Tree;

import java.util.List;
import java.util.Random;

public class QuickMappingFinder extends MappingFinder {

    private List<Point> points;
    private int n;

    public QuickMappingFinder() {super();}
    public QuickMappingFinder(List<Point> points) {super(points);}

    @Override
    public MappingFinder setPointSet(List<Point> points) {
        this.points = points;
        this.n = points.size();

        return this;
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping) {
        MappingBacktrackerFastIncorrect fastIncorrectBacktracker = new MappingBacktrackerFastIncorrect(points);
        MappingBacktrackerCorrect correctBacktracker = new MappingBacktrackerCorrect(points);

        if (getMappingByShuffle(points, tree, 0, mapping)) return true;

        if (fastIncorrectBacktracker.findMapping(tree, mapping)) return true;

        if (correctBacktracker.findMapping(tree, mapping)) return true;

        return false;
    }

    private static boolean getMappingByShuffle(List<Point> points, Tree tree, int maxShuffles, int[] mapping) {
        MappingValidator2SAT validator = new MappingValidator2SAT(points.size());
        int n = points.size();
        for (int i = 0; i < n; i++) mapping[i] = i;

        while (maxShuffles-- > 0) {
            shuffle(mapping);
            if (validator.validate(tree, mapping, points)) {
//                System.err.println("yes shuffle");
                return true;
            }
        }
//        System.err.println("no shuffle");
        return false;
    }

    private static Random random = new Random();
    private static void shuffle(int[] array) {
        int size = array.length;
        for (int i=size; i>1; i--) {
            int temp = array[i - 1];
            int idx = random.nextInt(i);
            array[i - 1] = array[idx];
            array[idx] = temp;
        }
    }

}
