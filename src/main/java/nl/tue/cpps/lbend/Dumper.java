package nl.tue.cpps.lbend;

import java.util.List;

import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

public interface Dumper {
    void draw(
            int idx,
            Tree tree, List<Point> points,
            int[] mapping, boolean[] solution);
}
