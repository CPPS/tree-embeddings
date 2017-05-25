package nl.tue.cpps.lbend.mappings;

import java.util.List;

import javax.naming.TimeLimitExceededException;

import nl.tue.cpps.lbend.geometry.LBend;
import nl.tue.cpps.lbend.geometry.MappingValidator2SAT;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;

public final class QuickMappingFinder extends AbstractMappingFinder {
    private final MappingValidator2SAT validator;
    // private final ShuffleMappingFinder shuffleMapper;
    private final AbstractLBendMappingFinder fastIncorrectBacktracker;
    private final AbstractLBendMappingFinder correctBacktracker;

    public QuickMappingFinder(int n) {
        validator = new MappingValidator2SAT(n);
        // shuffleMapper = new ShuffleMappingFinder(validator);
        fastIncorrectBacktracker = new MappingBacktrackerFastIncomplete(
                n, validator);
        correctBacktracker = new MappingBacktrackerCorrect(n);
    }

    @Override
    public MappingFinder setPointSet(List<Point> points) {
        LBend[][][] bends = LBend.createAllBends(points);
        // shuffleMapper.setPointSet(points);
        fastIncorrectBacktracker.setPoints(points, bends);
        correctBacktracker.setPoints(points, bends);

        return this;
    }

    @Override
    public boolean findMapping(Tree tree, int[] mapping, long maxTimeMS) throws TimeLimitExceededException {
        // if (shuffleMapper.findMapping(tree, mapping, maxTimeMS)) {
        // return true;
        // }
        if (fastIncorrectBacktracker.findMapping(tree, mapping, maxTimeMS)) {
            return true;
        }
        if (correctBacktracker.findMapping(tree, mapping, maxTimeMS)) {
            return true;
        }

        return false;
    }

}
