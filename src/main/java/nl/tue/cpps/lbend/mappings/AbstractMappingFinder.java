package nl.tue.cpps.lbend.mappings;

import javax.naming.TimeLimitExceededException;

import lombok.SneakyThrows;
import nl.tue.cpps.lbend.geometry.Tree;

abstract class AbstractMappingFinder implements MappingFinder {
    public final int[] findMapping(Tree tree) {
        int[] mapping = new int[tree.size()];
        if (findMapping(tree, mapping)) {
            return mapping;
        }

        return null;
    }

    @SneakyThrows(TimeLimitExceededException.class)
    public final boolean findMapping(Tree tree, int[] mapping) {
        return findMapping(tree, mapping, Long.MAX_VALUE);
    }
}
