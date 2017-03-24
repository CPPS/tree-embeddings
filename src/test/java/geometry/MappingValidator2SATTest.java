package geometry;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Ignore;

public class MappingValidator2SATTest {

    @Ignore // FIXME!
    @Test
    public void testIntersects() throws Exception {
        Line horizontal = new Line(new FixedPoint(1, 1), new FixedPoint(5, 1));
        Line vertical = new Line(new FixedPoint(4, 1), new FixedPoint(4, 3));

        assertTrue(MappingValidator2SAT.intersects(horizontal, vertical));
    }
}
