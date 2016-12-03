import java.util.Set;

public class Main {
    public static void main(String[] args) {
        PointGenerator G = new RandomPointGenerator(10, new Interval(0, 100), new Interval(0, 100));
        Set<Point> P = G.generate();

        for (Point p : P) {
            System.out.println(p);
        }
    }
}
