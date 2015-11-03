package upmsp.model.solution;

import upmsp.util.*;

import java.io.*;

/**
 * This class represents a Machine.
 *
 * @author Tulio Toffolo
 */
public class Machine {

    public final Solution solution;
    public final int id;

    public int jobs[];

    private int nJobs;
    private int makespan;

    private final int process[];
    private final int setup[][];


    /**
     * Instantiates a new Machine (of a Solution).
     *
     * @param solution the solution
     * @param id       the id
     */
    public Machine(Solution solution, int id) {
        this.solution = solution;
        this.id = id;

        process = solution.problem.processTimes[id];
        setup = solution.problem.setupTimes[id];

        jobs = new int[solution.problem.nJobs];
        nJobs = 0;
        makespan = 0;
    }


    /**
     * Adds a job to the last position of the machine.
     *
     * @param job job to add.
     */
    public void addJob(int job) {
        addJob(job, nJobs);
    }

    /**
     * Adds a job to the specified position, given by the {@param index}
     * parameter.
     *
     * @param job   job to add.
     * @param index position (index) to which the job will be added.
     */
    public void addJob(int job, int index) {
        assert index <= nJobs : "adding job to an invalid index in machine" + id;

        makespan += getDeltaCostAddJob(job, index);
        System.arraycopy(jobs, index, jobs, index + 1, nJobs - index);

        jobs[index] = job;
        if (++nJobs == 1)
            solution.nMachines++;

        assert validate(System.err);
    }

    /**
     * Clones machine and returns.
     *
     * @param solution solution of the cloned machine.
     * @return a copy of this machine.
     */
    public Machine clone(Solution solution) {
        Machine machine = new Machine(solution, id);
        System.arraycopy(jobs, 0, machine.jobs, 0, nJobs);
        machine.nJobs = nJobs;
        machine.makespan = makespan;

        return machine;
    }

    /**
     * Deletes the job from the position {@param index}.
     *
     * @param index index (position) of the job to remove.
     */
    public void delJob(int index) {
        assert index < nJobs : "deleting job from an invalid index in machine" + id;

        makespan += getDeltaCostDelJob(index);
        System.arraycopy(jobs, index + 1, jobs, index, nJobs - index - 1);

        if (--nJobs == 0)
            solution.nMachines--;

        assert validate(System.err);
    }

    /**
     * Gets the makespan of this machine.
     *
     * @return the makespan
     */
    public int getMakespan() {
        return makespan;
    }

    /**
     * Gets the number of jobs in this machine.
     *
     * @return the jobs
     */
    public int getNJobs() {
        return nJobs;
    }

    /**
     * Sets the job in the position {@param index}..
     *
     * @param job   job to set the position to.
     * @param index index (position) of the job to change.
     */
    public void setJob(int job, int index) {
        assert index < nJobs : "set≈ting job of an invalid index in machine" + id;

        makespan += getDeltaCostSetJob(job, index);
        jobs[index] = job;

        assert validate(System.err);
    }

    /**
     * Validates the machine.
     *
     * @param output output stream (example: System.out) to print eventual error
     *               messages.
     * @return true if the machine and its costs are valid and false otherwise.
     */
    public boolean validate(PrintStream output) {
        boolean valid = true;

        if (nJobs > 0) {
            int makespanValue = process[jobs[0]];
            for (int i = 1; i < nJobs; i++)
                makespanValue += setup[jobs[i - 1]][jobs[i]] + process[jobs[i]];

            if (makespanValue != makespan) {
                valid = false;
                Util.safePrintf(output, "Makespan is wrong in machine %d: %d vs %d (expected value)\n", id, makespanValue, makespan);
            }
        }

        return valid;
    }


    // region delta cost calculation

    /**
     * Gets the (delta) change in the makespan if job {@param job} is added to
     * position {@param index}.
     *
     * @param job   job to add.
     * @param index position (index) to which the job will be added.
     * @return the delta makespan
     */
    public int getDeltaCostAddJob(int job, int index) {
        assert index <= nJobs : "adding job to an invalid index in machine" + id;

        if (nJobs == 0) {
            return process[job];
        }
        else if (index == 0) {
            return setup[job][jobs[index]] + process[job];
        }
        else if (index == nJobs) {
            return setup[jobs[index - 1]][job] + process[job];
        }
        else {
            return -setup[jobs[index - 1]][jobs[index]]
              + setup[jobs[index - 1]][job] + process[job] + setup[job][jobs[index]];
        }
    }

    /**
     * Gets the (delta) change in the makespan if job in position {@param index}
     * is removed.
     *
     * @param index index (position) of the job to remove.
     * @return the delta makespan
     */
    public int getDeltaCostDelJob(int index) {
        assert index < nJobs : "deleting job from an invalid index in machine" + id;

        if (nJobs == 1) {
            assert makespan == process[jobs[index]] : String.format("%d vs %d\n", makespan, process[jobs[index]]);
            return -makespan;
        }
        else if (index == 0) {
            return -(setup[jobs[index]][jobs[index + 1]] + process[jobs[index]]);
        }
        else if (index == nJobs - 1) {
            return -(setup[jobs[index - 1]][jobs[index]] + process[jobs[index]]);
        }
        else {
            return -(setup[jobs[index - 1]][jobs[index]] + process[jobs[index]] + setup[jobs[index]][jobs[index + 1]])
              + setup[jobs[index - 1]][jobs[index + 1]];
        }
    }

    /**
     * Gets the (delta) change in the makespan if job in position {@param index}
     * is replaced by job {@param job}.
     *
     * @param job   job to set the position to.
     * @param index position (index) to set
     * @return the delta makespan
     */
    public int getDeltaCostSetJob(int job, int index) {
        assert index < nJobs : "set≈ting job of an invalid index in machine" + id;

        if (nJobs == 1) {
            return -process[jobs[index]] + process[job];
        }
        else if (index == 0) {
            return -(setup[jobs[index]][jobs[index + 1]] + process[jobs[index]])
              + (setup[job][jobs[index + 1]] + process[job]);
        }
        else if (index == nJobs - 1) {
            return -(setup[jobs[index - 1]][jobs[index]] + process[jobs[index]])
              + (setup[jobs[index - 1]][job] + process[job]);
        }
        else {
            return -(setup[jobs[index - 1]][jobs[index]] + process[jobs[index]] + setup[jobs[index]][jobs[index + 1]])
              + (setup[jobs[index - 1]][job] + process[job] + setup[job][jobs[index + 1]]);
        }
    }

    // endregion
}
