package upmsp.algorithm.heuristic;

import upmsp.algorithm.neighborhood.*;
import upmsp.model.*;
import upmsp.model.solution.*;
import upmsp.util.*;

import java.io.*;
import java.util.*;

/**
 * This class represents a Descent First Improvement Heuristic.
 *
 * @author Tulio Toffolo
 */
public class Descent extends Heuristic {

    /**
     * Instantiates a new Descent (First Improvement Heuristic) object.
     *
     * @param problem problem reference.
     * @param random  random number generator.
     */
    public Descent(Problem problem, Random random) {
        super(problem, random, "Descent");
    }

    /**
     * Executes the Descent heuristic.
     *
     * @param initialSolution the initial (input) solution.
     * @param timeLimitMillis the time limit (in milliseconds).
     * @param maxIters        the maximum number of iterations without improvements to execute.
     * @param output          output PrintStream for logging purposes.
     * @return the best solution encountered by the heuristic.
     */
    public Solution run(Solution initialSolution, long timeLimitMillis, long maxIters, PrintStream output) {
        long finalTimeMillis = System.currentTimeMillis() + timeLimitMillis;

        bestSolution = initialSolution;
        Solution solution = initialSolution.clone();

        int nItersWithoutImprovement = 0;

        while (System.currentTimeMillis() < finalTimeMillis && nItersWithoutImprovement++ < maxIters) {
            Move move = selectMove(solution);
            double delta = move.doMove(solution);

            // if solution is improved...
            if (delta < 0) {
                acceptMove(move);
                nItersWithoutImprovement = 0;

                if (solution.getCost() < bestSolution.getCost()) {
                    bestSolution = solution.clone();
                    Util.safePrintStatus(output, nIters, bestSolution, solution, "*");
                }
            }

            // if a side solution is obtained
            else if (delta == 0) {
                acceptMove(move);
            }

            // if solution is rejected..
            else {
                rejectMove(move);
            }

            nIters++;
        }

        return bestSolution;
    }
}
