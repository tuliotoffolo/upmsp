package upmsp.algorithm.constructive;

import upmsp.model.*;
import upmsp.model.solution.*;

import java.util.*;

/**
 * This class contains simple constructive procedures for the UPMSP.
 *
 * @author Tulio Toffolo
 */
public class SimpleConstructive {

    /**
     * Generates and returns a greedy randomized solution. The jobs are added in
     * a random order to the machine to which they incur the smallest increase
     * in the makespan.
     *
     * @param problem problem
     * @param random  random number generator
     * @return the solution generated
     */
    public static Solution greedySolution(Problem problem, Random random) {
        // creating shuffled list of jobs
        List<Integer> shuffledJobs = new ArrayList<>();
        for (int j = 0; j < problem.nJobs; j++)
            shuffledJobs.add(j);
        Collections.shuffle(shuffledJobs, random);

        Solution solution = new Solution(problem);
        int m = 0, p = 0; // stores machine and position to add a certain job

        for (int j = 0; j < problem.nJobs; j++) {
            int job = shuffledJobs.get(j);
            int cost = Integer.MAX_VALUE;

            for (Machine machine : solution.machines) {
                for (int index = 0; index <= machine.getNJobs(); index++) {
                    int deltaCost = machine.getDeltaCostAddJob(job, index);
                    if (deltaCost < cost) {
                        m = machine.id;
                        p = index;
                        cost = deltaCost;
                    }
                }
            }

            solution.machines[m].addJob(job, p);
        }

        solution.updateCost();
        return solution;
    }

    /**
     * Generates and returns a naive solution. Basically, each job j is assigned
     * to the machine j mod M (where M is the number of machines).
     *
     * @param problem the problem
     * @return the solution generated
     */
    public static Solution naiveSolution(Problem problem) {
        Solution solution = new Solution(problem);

        for (int j = 0; j < problem.nJobs; j++) {
            int m = j % problem.nMachines;
            solution.machines[m].addJob(j);
        }

        solution.updateCost();
        return solution;
    }

    /**
     * Generates and returns an extremely naive solution. Basically, all jobs
     * are added to the first machine.
     *
     * @param problem the problem
     * @return the solution generated
     */
    public static Solution veryNaiveSolution(Problem problem) {
        Solution solution = new Solution(problem);

        for (int j = 0; j < problem.nJobs; j++) {
            solution.machines[0].addJob(j);
        }

        solution.updateCost();
        return solution;
    }

    /**
     * Generates and returns a completely random solution. Each job is assigned
     * to a random position in a random machine.
     *
     * @param problem the problem
     * @param random  random number generator
     * @return the solution generated
     */
    public static Solution randomSolution(Problem problem, Random random) {
        // creating shuffled list of jobs
        List<Integer> shuffledJobs = new ArrayList<>();
        for (int j = 0; j < problem.nJobs; j++)
            shuffledJobs.add(j);
        Collections.shuffle(shuffledJobs, random);

        Solution solution = new Solution(problem);

        for (int j = 0; j < problem.nJobs; j++) {
            int job = shuffledJobs.get(j);
            int m = random.nextInt(problem.nMachines);
            solution.machines[m].addJob(job);
        }

        solution.updateCost();
        return solution;
    }
}
