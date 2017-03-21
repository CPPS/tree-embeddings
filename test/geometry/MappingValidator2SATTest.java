package geometry;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by s148327 on 5-3-2017.
 */
public class MappingValidator2SATTest {

    @Test
    public void testIntersects() throws Exception {
        Line horizontal = new Line(new FixedPoint(1, 1), new FixedPoint(5, 1));
        Line vertical = new Line(new FixedPoint(4, 1), new FixedPoint(4, 3));

        assertTrue(MappingValidator2SAT.intersects(horizontal, vertical));
    }
}