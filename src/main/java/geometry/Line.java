package geometry;

import lombok.Data;

@Data
public class Line {
    private final Point from, to;

    public Line reverse() {
        return new Line(to, from);
    }
}
