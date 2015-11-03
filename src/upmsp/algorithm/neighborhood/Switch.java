package upmsp.algorithm.neighborhood;

import upmsp.model.*;
import upmsp.model.solution.*;

import java.util.*;

/**
 * This class represents a Switch Move. A neighbor in the Switch Move structure
 * is generated by switching the order of two jobs of a machine. The parameter
 * "useMakespanMachine" determines whether the machine with the largest total
 * execution time is always used.
 *
 * @author Tulio Toffolo
 */
public class Switch extends Move {

    private Machine machine;
    private int pos1, pos2, job1, job2;
    private boolean useMakespanMachine;

    /**
     * Instantiates a new Switch.
     *
     * @param problem            problem.
     * @param random             random number generator.
     * @param priority           the priority of this neighborhood.
     * @param useMakespanMachine true if the makespan machine should be always
     *                           considered or false otherwise.
     */
    public Switch(Problem problem, Random random, int priority, boolean useMakespanMachine) {
        super(problem, random, "Switch" + (useMakespanMachine ? "(mk)" : ""), priority);
        this.useMakespanMachine = useMakespanMachine;
    }

    public void accept() {
        super.accept();
    }

    public int doMove(Solution solution) {
        super.doMove(solution);

        // selecting machine for operation
        if (useMakespanMachine && solution.makespanMachine.getNJobs() > 1) {
            machine = solution.makespanMachine;
        }
        else {
            int m;
            do {
                m = random.nextInt(solution.machines.length);
            }
            while (solution.machines[m].getNJobs() <= 1);
            machine = solution.machines[m];
        }

        // selecting jobs to perform operation
        pos1 = random.nextInt(machine.getNJobs());
        pos2 = random.nextInt(machine.getNJobs());
        job1 = machine.jobs[pos1];
        job2 = machine.jobs[pos2];

        // swapping jobs
        machine.setJob(job2, pos1);
        machine.setJob(job1, pos2);

        solution.updateCost();
        return deltaCost = solution.getCost() - initialCost;
    }

    public boolean hasMove(Solution solution) {
        return !useMakespanMachine || solution.makespanMachine.getNJobs() > 1;
    }

    public void reject() {
        super.reject();

        machine.setJob(job1, pos1);
        machine.setJob(job2, pos2);
        currentSolution.updateCost();
    }
}
