import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    static Set<Point> generatePoints() {
        PointGenerator generator = new RandomPointGenerator(10, new Interval(0, 100), new Interval(0, 100));
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

        PointGenerator points = new RandomPointGenerator(n, new Interval(min_x, max_x), new Interval(min_y, max_y));
        Set<Point> P = points.generate();

        TreeGenerator generator = new NaiveTreeGenerator(n, k);
        for (Tree T : generator) {
            // ...
        }
    }
}
