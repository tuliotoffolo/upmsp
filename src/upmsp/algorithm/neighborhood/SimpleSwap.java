package upmsp.algorithm.neighborhood;

import upmsp.model.*;
import upmsp.model.solution.*;

import java.util.*;

/**
 * This class represents a Simple Swap Move. A neighbor in the Swap Move is generated by swapping two jobs between two
 * machines. The parameter "useMakespanMachine" determines whether the machine with the largest total execution time is
 * always used.
 *
 * @author Tulio Toffolo
 */
public class SimpleSwap extends Move {

    private Machine machine1, machine2;
    private int posM1, posM2, job1, job2;
    private boolean useMakespanMachine;

    /**
     * Instantiates a new SimpleSwap Move.
     *
     * @param problem            problem.
     * @param random             random number generator.
     * @param priority           the priority of this neighborhood.
     * @param useMakespanMachine true if the makespan machine should be always considered or false otherwise.
     */
    public SimpleSwap(Problem problem, Random random, int priority, boolean useMakespanMachine) {
        super(problem, random, "SimpSwap" + (useMakespanMachine ? "(mk)" : ""), priority);
        this.useMakespanMachine = useMakespanMachine;
    }

    public void accept() {
        super.accept();
    }

    public int doMove(Solution solution) {
        super.doMove(solution);

        // selecting machines to involve in operation
        if (useMakespanMachine && solution.makespanMachine.getNJobs() > 0) {
            int m;
            do {
                m = random.nextInt(solution.machines.length);
            }
            while (m == solution.makespanMachine.id || solution.machines[m].getNJobs() == 0);

            machine1 = solution.makespanMachine;
            machine2 = solution.machines[m];
        }
        else {
            int m1, m2;
            do {
                m1 = random.nextInt(solution.machines.length);
                m2 = random.nextInt(solution.machines.length);
            }
            while (m1 == m2 || solution.machines[m1].getNJobs() == 0 || solution.machines[m2].getNJobs() == 0);
            machine1 = solution.machines[m1];
            machine2 = solution.machines[m2];
        }

        // selecting jobs to perform operation
        posM1 = random.nextInt(machine1.getNJobs());
        posM2 = random.nextInt(machine2.getNJobs());
        job1 = machine1.jobs[posM1];
        job2 = machine2.jobs[posM2];

        // swapping jobs
        machine1.setJob(job2, posM1);
        machine2.setJob(job1, posM2);

        solution.updateCost();
        return deltaCost = solution.getCost() - initialCost;
    }

    public boolean hasMove(Solution solution) {
        return solution.getNMachines() > 1;
    }

    public void reject() {
        super.reject();

        machine1.setJob(job1, posM1);
        machine2.setJob(job2, posM2);
        currentSolution.updateCost();
    }
}
