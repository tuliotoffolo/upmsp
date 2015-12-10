package upmsp.model;

import upmsp.util.*;

import java.io.*;
import java.nio.file.*;

/**
 * This class represents an Unrelated Parallel Machine Scheduling Problem.
 *
 * @author Tulio Toffolo
 */
public class Problem {

    /***
     * Number of available machines
     */
    public final int nMachines;

    /***
     * Number of jobs
     */
    public final int nJobs;

    /***
     * Matrix with the process time of a job in a machine
     * processTime[machine][job]
     */
    public final int processTimes[][];

    /***
     * Matrix with the the setup times time for scheduling each job j after each
     * job i in a certain machine: setupTimes[machine][job_i][job_j]
     */
    public final int setupTimes[][][];


    /**
     * Instantiates a new Problem from a file.
     *
     * @param instancePath the instance file path
     */
    public Problem(String instancePath) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(instancePath));

        SimpleTokenizer token = new SimpleTokenizer(reader.readLine());

        // reading number of jobs (nJobs) and number of machines (nMachines)
        nJobs = token.nextInt();
        nMachines = token.nextInt();

        // initializing arrays
        processTimes = new int[nMachines][nJobs];
        setupTimes = new int[nMachines][nJobs][nJobs];

        // skip next line
        reader.readLine();

        // reading process times
        for (int job = 0; job < nJobs; job++) {
            token = new SimpleTokenizer(reader.readLine());
            for (int machine = 0; machine < nMachines; machine++) {
                int machineId = token.nextInt();
                assert machine == machineId : "machine does not match ID in file";

                processTimes[machine][job] = token.nextInt();
            }
        }

        // skip next line (SSD)
        reader.readLine();

        // reading setupTimes times
        for (int machine = 0; machine < nMachines; machine++) {
            // skip machine line
            reader.readLine();
            for (int job = 0; job < nJobs; job++) {
                token = new SimpleTokenizer(reader.readLine());
                for (int nextJob = 0; nextJob < nJobs; nextJob++) {
                    setupTimes[machine][job][nextJob] = token.nextInt();

                    assert job != nextJob || setupTimes[machine][job][nextJob] == 0 : "setup between equal jobs must be zero";
                }
            }
        }

        reader.close();
    }
}
