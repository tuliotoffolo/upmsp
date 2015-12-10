package upmsp.algorithm.heuristic;

import upmsp.algorithm.neighborhood.*;
import upmsp.model.*;
import upmsp.model.solution.*;
import upmsp.util.*;

import java.io.*;
import java.util.*;

/**
 * This class is an Iterated Local Search implementation.
 *
 * @author Tulio Toffolo
 */
public class ILS extends Heuristic {

    /**
     * ILS parameters.
     */
    private long rnaMax;
    private int itersP, p0, pMax;

    /**
     * Heuristic executed during the descent phase
     */
    private Heuristic heuristic;

    /**
     * Instantiates a new ILS.
     *
     * @param problem the problem
     * @param random  random number generator
     * @param rnaMax  maximum number of rejected iterations for the RNA algorithm
     * @param itersP  number of iterations per perturbation level (before changing the level)
     * @param p0      initial perturbation level
     * @param pMax    maximum perturbation level (note that the value given by pMax will be multiplied by p0)
     */
    public ILS(Problem problem, Random random, long rnaMax, int itersP, int p0, int pMax) {
        this(problem, random, new Descent(problem, random), rnaMax, itersP, p0, pMax);
    }

    /**
     * Instantiates a new ILS.
     *
     * @param problem   the problem
     * @param random    random number generator
     * @param heuristic heuristic to run as the descent phase
     * @param rnaMax    maximum number of rejected iterations for the RNA algorithm
     * @param itersP    number of iterations per perturbation level (before changing the level)
     * @param pMax      maximum perturbation level (note that the value given by pMax will be multiplied by p0)
     */
    public ILS(Problem problem, Random random, Heuristic heuristic, long rnaMax, int itersP, int p0, int pMax) {
        super(problem, random, "ILS");

        this.heuristic = heuristic;
        this.rnaMax = rnaMax;
        this.itersP = itersP;
        this.p0 = p0;
        this.pMax = pMax * p0;
    }

    /**
     * Adds a Move (neighborhood) to the collection of possible neighborhoods for the local search.
     *
     * @param move the move to be added.
     */
    public void addMove(Move move) {
        super.addMove(move);
        heuristic.addMove(move);
    }

    /**
     * Gets the total number of iterations performed.
     *
     * @return the total number of iterations performed.
     */
    public long getNIters() {
        return nIters + heuristic.getNIters();
    }

    /**
     * Executes the Iterated Local Search.
     *
     * @param initialSolution the initial (input) solution.
     * @param timeLimitMillis the time limit (in milliseconds).
     * @param maxIters        the maximum number of iterations without improvements to execute.
     * @param output          output PrintStream for logging purposes.
     * @return the best solution encountered by the ILS.
     */
    public Solution run(Solution initialSolution, long timeLimitMillis, long maxIters, PrintStream output) {
        long finalTimeMillis = System.currentTimeMillis() + timeLimitMillis;

        bestSolution = initialSolution;
        bestSolution = heuristic.run(initialSolution, finalTimeMillis - System.currentTimeMillis(), rnaMax, output);
        Solution solution = bestSolution.clone();

        int perturbLevel = p0;
        int nItersInPerturb = 0;
        int nItersWithoutImprovement = 0;

        while (System.currentTimeMillis() < finalTimeMillis) {
            while (System.currentTimeMillis() < finalTimeMillis && nItersWithoutImprovement++ < maxIters) {
                applyPerturbation(output, solution, perturbLevel);
                solution = heuristic.run(solution, finalTimeMillis - System.currentTimeMillis(), rnaMax, output);

                Util.safePrintStatus(output, heuristic.getNIters(), bestSolution, solution, "p-" + perturbLevel);

                // if solution is improved...
                if (solution.getCost() < bestSolution.getCost()) {
                    bestSolution = solution.clone();
                    nItersInPerturb = 0;
                    perturbLevel = p0;
                }

                // if solution is not improved
                else {
                    solution = bestSolution.clone();
                    nItersInPerturb++;
                }

                if (nItersInPerturb >= itersP) {
                    nItersInPerturb = 0;
                    perturbLevel = perturbLevel + p0 <= pMax ? perturbLevel + p0 : p0;
                    nIters++;
                }
            }

            if (System.currentTimeMillis() < finalTimeMillis) {
                nItersWithoutImprovement = 0;
                Util.safePrintText(output, "ILS reached maxIters", "");
            }
        }

        return bestSolution;
    }

    /**
     * Returns the string representation of the heuristic.
     *
     * @return the string representation of the heuristic.
     */
    public String toString() {
        return String.format("ILS (rnaMax=%s, itersP=%s, p0=%d, pMax=%d)",
          Util.longToString(rnaMax), Util.longToString(itersP), p0, pMax);
    }


    private void applyPerturbation(PrintStream output, Solution solution, int level) {
        for (int i = 0; i < level; i++) {
            Move move = moves.get(random.nextInt(moves.size()));
            while (!move.hasMove(solution))
                move = moves.get(random.nextInt(moves.size()));

            move.doMove(solution);
            move.accept();
        }
    }
}
