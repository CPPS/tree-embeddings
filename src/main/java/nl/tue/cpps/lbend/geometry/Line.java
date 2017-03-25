package nl.tue.cpps.lbend.geometry;

import javax.annotation.Nonnull;

import lombok.Data;

@Data
public class Line {
    private final @Nonnull Point from, to;
}
