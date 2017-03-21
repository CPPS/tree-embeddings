package geometry;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class MutablePoint implements Point {
    private int x, y;
}
