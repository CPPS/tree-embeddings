package nl.tue.cpps.lbend.geometry;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class MutablePoint implements Point {
    private int x, y;
    
    @Override
    public boolean equals(Object a){
        if(a instanceof Point){
            Point b=(Point) a;
            if(b.getX() == x && b.getY()==y){
                return true;
            }
        }
        return false;
    }
}
