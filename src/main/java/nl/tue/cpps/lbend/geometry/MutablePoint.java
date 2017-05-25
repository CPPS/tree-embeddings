package nl.tue.cpps.lbend.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class MutablePoint extends AbstractPoint {
    private int x, y;
}
