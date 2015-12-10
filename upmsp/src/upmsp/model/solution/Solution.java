package upmsp.model.solution;

import upmsp.model.*;
import upmsp.util.*;

import java.io.*;
import java.nio.file.*;

/**
 * This class represents a Solution of the Unrelated Parallel Machine Scheduling
 * Problem..
 *
 * @author Tulio Toffolo
 */
public class Solution {

    public final Problem problem;
    public final Machine[] machines;

    protected int nMachines;
    protected int makespan;
    public Machine makespanMachine;

    /**
     * Instantiates a new Solution.
     *
     * @param problem problem considered.
     */
    public Solution(Problem problem) {
        this.problem = problem;

        machines = new Machine[problem.nMachines];
        for (int m = 0; m < problem.nMachines; m++) {
            machines[m] = new Machine(this, m);
        }

        nMachines = 0;
        makespan = 0;
        makespanMachine = machines[0];
    }

    /**
     * Private constructor used for cloning.
     *
     * @param solution solution to copy from.
     */
    private Solution(Solution solution) {
        this.problem = solution.problem;

        machines = new Machine[problem.nMachines];
        for (int m = 0; m < problem.nMachines; m++) {
            machines[m] = solution.machines[m].clone(this);
        }

        nMachines = solution.nMachines;
        makespan = solution.makespan;
        makespanMachine = machines[solution.makespanMachine.id];
    }

    /**
     * Creates and returns a copy of this solution.
     */
    public Solution clone() {
        return new Solution(this);
    }

    /**
     * Gets the solution makespan. Note the the makespan may be outdated if the
     * solution was modified. To ensure that it is updated, call {@link
     * #updateCost()}.
     *
     * @return the solution cost.
     */
    public int getCost() {
        return makespan;
    }

    /**
     * Gets the number of machines used in this solution.
     *
     * @return number of machines used in this solution.
     */
    public int getNMachines() {
        return nMachines;
    }

    /**
     * Updates (and returns) the makespan of the solution.
     *
     * @return the updated solution cost.
     */
    public int updateCost() {
        makespan = 0;
        for (Machine machine : machines) {
            if (machine.getMakespan() > makespan) {
                makespan = machine.getMakespan();
                makespanMachine = machine;
            }
        }
        return makespan;
    }

    /**
     * Validates the solution.
     *
     * @param output output stream (example: System.out) to print eventual error
     *               messages.
     * @return true if the solution and its costs are valid and false otherwise.
     */
    public boolean validate(PrintStream output) {
        boolean valid = true;

        // checking jobs allocations
        boolean allocs[] = new boolean[problem.nJobs];
        for (Machine machine : machines) {
            for (int idx = 0; idx < machine.getNJobs(); idx++) {
                if (allocs[machine.jobs[idx]]) {
                    valid = false;
                    Util.safePrintf(output, "Job %d is allocated twice\n", machine.jobs[idx]);
                }
                allocs[machine.jobs[idx]] |= true;
            }
        }

        // checking if all jobs are allocated
        for (int j = 0; j < allocs.length; j++) {
            if (!allocs[j]) {
                valid = false;
                Util.safePrintf(output, "Job %d is not allocated to any machine\n", j);
            }
        }

        // checking makespan of all machines
        int makespanValue = 0, makespanMachineId = 0;
        for (Machine machine : machines) {
            if (machine.getNJobs() > 0) {
                int machineMakespanValue = problem.processTimes[machine.id][machine.jobs[0]];
                for (int i = 1; i < machine.getNJobs(); i++)
                    machineMakespanValue += problem.setupTimes[machine.id][machine.jobs[i - 1]][machine.jobs[i]] + problem.processTimes[machine.id][machine.jobs[i]];

                if (machineMakespanValue != machine.getMakespan()) {
                    valid = false;
                    Util.safePrintf(output, "Makespan is wrong in machine %d: %d vs %d (expected value)\n", machine.id, machineMakespanValue, machine.getMakespan());
                }
                if (makespanValue < machine.getMakespan()) {
                    makespanValue = machine.getMakespan();
                    makespanMachineId = machine.id;
                }
            }
        }

        // checking global makespan
        if (makespan != makespanValue) {
            valid = false;
            Util.safePrintf(output, "Makespan is wrong: %d vs %d (expected value)\n", makespan, makespanValue);
        }

        // checking machine with the maximum makespan
        if (makespanMachine.id != makespanMachineId) {
            valid = false;
            Util.safePrintf(output, "Makespan machine is wrong: %d vs %d (expected machine)\n", makespanMachine.id, makespanMachineId);
        }

        return valid;
    }

    /**
     * Writes the solution to a file.
     *
     * @param filePath the output file path.
     * @throws IOException in case any IO error occurs.
     */
    public void write(String filePath) throws IOException {
        PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(filePath)));

        writer.printf("%d\n", problem.nMachines);
        for (Machine machine : machines) {
            writer.printf("%d", machine.getNJobs());
            for (int j = 0; j < machine.getNJobs(); j++) {
                writer.printf(" %d", machine.jobs[j]);
            }
            writer.printf("\n");
        }
        writer.printf("\n");

        updateCost();
        writer.printf("Total makespan: %d\n", getCost());
        writer.close();
    }
}
