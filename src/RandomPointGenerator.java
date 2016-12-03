import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class RandomPointGenerator implements PointGenerator {

    protected int n;
    protected Interval x_range;
    protected Interval y_range;
    protected Random random;

    public RandomPointGenerator(int n, int min_x, int max_x, int min_y, int max_y) {
        this(n, new Interval(min_x, max_x), new Interval(min_y, max_y));
    }

    public RandomPointGenerator(int n, Interval x_range, Interval y_range) {

        if (x_range.getLength() < n) {
            throw new IllegalArgumentException();
        }

        if (y_range.getLength() < n) {
            throw new IllegalArgumentException();
        }

        this.n = n;
        this.x_range = x_range;
        this.y_range = y_range;
        this.random = new Random();
    }

    protected int randomCoordinate(Interval range) {
        return random.nextInt(range.getLength()) + range.getMin();
    }

    protected Set<Integer> generateCoordinates(Interval range) {
        Set<Integer> coordinates = new HashSet<>();

        while (coordinates.size() < n) {
            int coordinate = randomCoordinate(range);
            coordinates.add(coordinate);
        }

        return coordinates;
    }

    @Override
    public Set<Point> generate() {
        Set<Point> points = new HashSet<>();
        Set<Integer> x_coordinates = generateCoordinates(x_range);
        Set<Integer> y_coordinates = generateCoordinates(y_range);

        Iterator<Integer> x_generator = x_coordinates.iterator();
        Iterator<Integer> y_generator = y_coordinates.iterator();

        for (int i = 0; i < n; i++) {
            int x = x_generator.next();
            int y = y_generator.next();

            Point point = new Point(x,y);
            points.add(point);
        }

        return points;
    }
}
