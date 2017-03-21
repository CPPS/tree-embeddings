package generators;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import generator.point.PermutedPointGenerator;
import geometry.Point;

public class PermutedPointGeneratorTest {
    @Test
    public void test() {
        // Amount of points
        int size = 4;

        // Amount of point sets
        int n = 1;
        for (int i = 1; i <= size; i++) {
            n *= i;
        }

        Set<Set<Point>> seen = new HashSet<>();
        Iterator<List<Point>> it = new PermutedPointGenerator(4).generate();
        while (it.hasNext()) {
            Collection<Point> set = it.next();
            n--;
            assertTrue(seen.add(new HashSet<>(set)));
        }

        assertEquals(0, n);
    }
}
