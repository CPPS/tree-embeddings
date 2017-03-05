package math;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by s148327 on 5-3-2017.
 */
public class Solver2SATTest {

    Solver2SAT solver;
    int n;

    public void init(int n) throws Exception {
        this.n = n;
        solver = new Solver2SAT(n);
    }

    @Test
    public void testSolve() throws Exception {
        // (x0 OR x1) AND (!x0 OR x2) AND (!x1 OR !x2)

        init(3);
        solver.addClause(0, false, 1, false)
                .addClause(0, true, 2, false)
                .addClause(1, true, 2, true);

        System.out.println(Arrays.toString(solver.solve()));
    }

    @Test
    public void testMultiple() throws Exception {
        for (int i = 0; i < 30; i++) {
            randomInit(100, 200);

            boolean[] solution = solver.solve();

            if (solution != null)
                solution = Arrays.copyOf(solution, solution.length);

            for (int j = 0; j < 40; j++) {
                boolean[] newSolution = solver.solve();

                assertTrue((solution == null && newSolution == null)
                        || Arrays.equals(solution, newSolution));
            }
        }
    }

    @Test
    public void testNoSolution() throws Exception {
        // (x0 OR x0) AND (!x0 OR !x0)
        init(1);
        solver.addClause(0, false, 0, false)
                .addClause(0, true, 0, true);

        assertNull(solver.solve());
    }

    @Test
    public void testSolveOneSolution() throws Exception {
        // (x0 OR x1) AND (x0 OR !x1) AND (x0 OR !x1) AND (!x0 OR !x1)
        init(2);
        solver.addClause(0, false, 1, false)
                .addClause(0, false, 1, true)
                .addClause(0, false, 1, true)
                .addClause(0, true, 1, true);

        // only solution:
        // x0 = true
        // x1 = false
        assertArrayEquals(solver.solve(), new boolean[] {true, false});
    }

    private void randomInit(int maxN, int maxClauses) throws Exception {
        Random random = new Random();
        init(random.nextInt(maxN) + 1);

        int m = random.nextInt(maxClauses + 1);
        for (int i = 0; i < m; i++) {
            solver.addClause(random.nextInt(n), random.nextBoolean(), random.nextInt(n), random.nextBoolean());
        }
    }
}