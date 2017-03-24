package mappings;

import geometry.Point;
import geometry.Tree;

import javax.annotation.Nullable;
import java.util.List;

public abstract class MappingFinder {

    public MappingFinder() {

    }

    public MappingFinder(List<Point> points) {
        this();
        setPointSet(points);
    }

    /**
     * Set the point set to find the mapping on.
     * Allows for preprocessing and optimizations
     * when multiple trees are executed on the same
     * point set.
     * @param points the point set
     * @return this
     */
    public abstract MappingFinder setPointSet(List<Point> points);

    /**
     * Finds a possible mapping of the tree onto the point set.
     * setPointSet must be called before this method is executed.
     * If not possible, returns null.
     * @param tree the tree to map onto the point set
     * @return possible mapping, or null when not possible
     */
    @Nullable
    public int[] findMapping(Tree tree) {
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
    public abstract boolean findMapping(Tree tree, int[] mapping);
}
