package nl.tue.cpps.lbend.mappings;

import java.util.List;

import javax.annotation.Nullable;
import javax.naming.TimeLimitExceededException;

import lombok.SneakyThrows;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

public interface MappingFinder {
    /**
     * Set the point set to find the mapping on.
     * Allows for preprocessing and optimizations
     * when multiple trees are executed on the same
     * point set.
     * @param points the point set
     * @return this
     */
    MappingFinder setPointSet(List<Point> points);

    /**
     * Finds a possible mapping of the tree onto the point set.
     * setPointSet must be called before this method is executed.
     * If not possible, returns null.
     * @param tree the tree to map onto the point set
     * @return possible mapping, or null when not possible
     */
    @Nullable
    default int[] findMapping(Tree tree) {
        int[] mapping = new int[tree.size()];
        if (findMapping(tree, mapping)) {
            return mapping;
        }

        return null;
    }

    /**
     * If possible mapping exists, resulting mapping will be stored
     * in {@code mapping} and true is returned. If no mapping is found
     * false is returned.
     * @param tree tree
     * @param mapping the array to store the mapping in
     * @return true iff valid mapping is found
     */
    @SneakyThrows(TimeLimitExceededException.class)
    default boolean findMapping(Tree tree, int[] mapping) {
        return findMapping(tree, mapping, Long.MAX_VALUE);
    }

    /**
     * If possible mapping exists, resulting mapping will be stored
     * in {@code mapping} and true is returned.
     * @param tree tree
     * @param mapping the array to store the mapping in
     * @param maxTimeMS maximum running time hint in milliseconds
     * @return true iff valid mapping is found
     */
    boolean findMapping(Tree tree, int[] mapping, long maxTimeMS) throws TimeLimitExceededException;
}
