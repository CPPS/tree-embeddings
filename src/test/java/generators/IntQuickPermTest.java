package generators;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import generator.IntQuickPerm;

public class IntQuickPermTest {
    @Test
    public void test() {
        int[] a = { 0, 1, 2, 3 };
        IntQuickPerm q = new IntQuickPerm(a);
        Set<int[]> found = new HashSet<>();
        int left = factorial(a.length);

        while (q.hasNext()) {
            int[] v = q.next();
            left--;

            if (!found.add(v.clone())) {
                assertTrue("Dupe: " + v + " " + found, false);
            }
        }

        assertEquals(0, left);
    }

    private int factorial(int v) {
        if (v == 0) {
            return 1;
        }

        return v * factorial(v - 1);
    }
}
