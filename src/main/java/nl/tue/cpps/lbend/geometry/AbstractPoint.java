package nl.tue.cpps.lbend.geometry;

abstract class AbstractPoint implements Point {
    @Override
    public final boolean equals(Object a) {
        if (a instanceof Point) {
            Point b = (Point) a;
            if (b.getX() == getX() && b.getY() == getY()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getX();
        result = prime * result + getY();
        return result;
    }
}
