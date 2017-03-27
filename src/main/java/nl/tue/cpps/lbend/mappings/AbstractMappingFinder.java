package nl.tue.cpps.lbend.mappings;

import java.util.List;

import nl.tue.cpps.lbend.geometry.LBend;
import nl.tue.cpps.lbend.geometry.Point;

abstract class AbstractMappingFinder implements MappingFinder {
    @Override
    public final AbstractMappingFinder setPointSet(List<Point> points) {
        setPoints(points, LBend.createAllBends(points));
        return this;
    }

    abstract void setPoints(List<Point> points, LBend[][][] bends);
}
