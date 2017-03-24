package nl.tue.cpps.lbend.geometry;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class MutablePoint implements Point {
    private int x, y;
}
