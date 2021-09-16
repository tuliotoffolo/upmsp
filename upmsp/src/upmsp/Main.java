package upmsp;

import upmsp.algorithm.constructive.*;
import upmsp.algorithm.heuristic.*;
import upmsp.algorithm.neighborhood.*;
import upmsp.model.*;
import upmsp.model.solution.*;
import upmsp.util.*;

import java.io.*;
import java.util.*;

/**
 * This class is the Main class of the program, responsible of parsing the input, instantiating moves and heuristics and
 * printing the results.
 *
 * @author Tulio Toffolo
 */
public class Main {

    // region solver parameters and default values

    public static long startTimeMillis = System.currentTimeMillis();

    public static boolean validate = false;

    public static String algorithm = "sa";
    public static String inFile;
    public static String outFile = null;

    public static long seed = 0;
    public static long maxIters = ( long ) 1e8;
    public static long timeLimit = 60 * 1000;

    public static int bestKnown = Integer.MAX_VALUE;

    // ILS
    public static long rnaMax = 9000000;
    public static int itersP = 700;
    public static int p0 = 80;
    public static int pMax = 6;

    // LAHC
    public static int listSize = ( int ) 1000;

    // SA (Simulated Annealing)
    public static double alpha = 0.99;
    public static int saMax = ( int ) 1e7;
    public static double t0 = 1;

    // SCHC
    public static int stepSize = 1000;

    // Neighborhoods
    public static boolean neighborhoods[];

    static {
        neighborhoods = new boolean[6 * 4];
        for (int i = 0; i < neighborhoods.length; i++)
            neighborhoods[i] = true;
    }

    // endregion solver parameters and default values

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException if any IO error occurs.
     */
    public static void main(String[] args) throws IOException {
        Locale.setDefault(new Locale("en-US"));
        if (!readArgs(args))
            return;

        Problem problem = new Problem(inFile);
        Random random = new Random(seed);

        // check if solver should be executed only as a validator
        if (validate) {
            Solution solution = new Solution(problem);
            solution.read(outFile);
            boolean validSolution = solution.validate(System.out);
            if (validSolution) {
                System.out.println("\nSolution was validated and has cost "+ solution.getCost() +"  :)\n");
            }
            else {
                System.out.println("\nSolution is invalid!  :'(\n");
            }
            return;
        }

        Heuristic solver;
        switch (algorithm) {
            case "lahc":
                solver = new LAHC(problem, random, listSize);
                break;
            case "lahc-ils":
                solver = new ILS(problem, random, new LAHC(problem, random, listSize), rnaMax, itersP, p0, pMax);
                break;
            case "ils":
                solver = new ILS(problem, random, rnaMax, itersP, p0, pMax);
                break;
            case "sa":
                solver = new SA(problem, random, alpha, t0, saMax);
                break;
            case "sa-ils":
                solver = new ILS(problem, random, new SA(problem, random, alpha, t0, saMax), rnaMax, itersP, p0, pMax);
                break;
            case "schc":
                solver = new SCHC(problem, random, stepSize);
                break;
            case "schc-ils":
                solver = new ILS(problem, random, new SCHC(problem, random, stepSize), rnaMax, itersP, p0, pMax);
                break;
            default:
                System.exit(-1);
                return;
        }

        // adding moves (neighborhoods)
        createNeighborhoods(problem, random, solver);

        System.out.printf("Instance....: %s\n", inFile);
        System.out.printf("Algorithm...: %s\n", solver);
        System.out.printf("Other params: maxIters=%s, seed=%d, timeLimit=%.2fs\n\n", Util.longToString(maxIters), seed, timeLimit / 1000.0);
        System.out.printf("    /--------------------------------------------------------\\\n");
        System.out.printf("    | %8s | %8s | %8s | %8s | %10s | %s\n", "Iter", "RDP(%)", "S*", "S'", "Time", "");
        System.out.printf("    |----------|----------|----------|----------|------------|\n");

        // re-starting time counting (after reading files)
        startTimeMillis = System.currentTimeMillis();

        // generating initial solution
        Solution solution = SimpleConstructive.randomSolution(problem, random);
        Util.safePrintStatus(System.out, 0, solution, solution, "s0");
        assert solution.validate(System.err);

        // running stochastic local search
        if (solver.getMoves().size() > 0)
            solution = solver.run(solution, timeLimit, maxIters, System.out);
        solution.validate(System.err);

        System.out.printf("    \\--------------------------------------------------------/\n\n");

        System.out.printf("Neighborhoods statistics (values in %%):\n\n");
        System.out.printf("    /----------------------------------------------------------------\\\n");
        System.out.printf("    | %-18s | %8s | %8s | %8s | %8s |\n", "Move", "Improvs.", "Sideways", "Accepts", "Rejects");
        System.out.printf("    |--------------------|----------|----------|----------|----------|\n");
        for (Move move : solver.getMoves())
            Util.safePrintMoveStatistics(System.out, move, "");
        System.out.printf("    \\----------------------------------------------------------------/\n\n");

        if (bestKnown != Integer.MAX_VALUE)
            System.out.printf("Best RDP..........: %.4f%%\n", 100 * ( double ) (solution.getCost() - bestKnown) / ( double ) bestKnown);
        System.out.printf("Best makespan.....: %d\n", solution.getCost());
        System.out.printf("N. of Iterations..: %d\n", solver.getNIters());
        System.out.printf("Total runtime.....: %.2fs\n", (System.currentTimeMillis() - startTimeMillis) / 1000.0);

        solution.write(outFile);
    }

    private static void createNeighborhoods(Problem problem, Random random, Heuristic solver) {
        int index = -1;

        if (neighborhoods[++index]) solver.addMove(new Shift(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new Shift(problem, random, 1, false));
        if (neighborhoods[++index]) solver.addMove(new ShiftSmart(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new ShiftSmart(problem, random, 1, false));

        if (neighborhoods[++index]) solver.addMove(new SimpleSwap(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new SimpleSwap(problem, random, 1, false));
        if (neighborhoods[++index]) solver.addMove(new SimpleSwapSmart(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new SimpleSwapSmart(problem, random, 1, false));

        if (neighborhoods[++index]) solver.addMove(new Swap(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new Swap(problem, random, 1, false));
        if (neighborhoods[++index]) solver.addMove(new SwapSmart(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new SwapSmart(problem, random, 1, false));

        if (neighborhoods[++index]) solver.addMove(new Switch(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new Switch(problem, random, 1, false));
        if (neighborhoods[++index]) solver.addMove(new SwitchSmart(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new SwitchSmart(problem, random, 1, false));

        if (neighborhoods[++index]) solver.addMove(new TaskMove(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new TaskMove(problem, random, 1, false));
        if (neighborhoods[++index]) solver.addMove(new TaskMoveSmart(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new TaskMoveSmart(problem, random, 1, false));

        if (neighborhoods[++index]) solver.addMove(new TwoShift(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new TwoShift(problem, random, 1, false));
        if (neighborhoods[++index]) solver.addMove(new TwoShiftSmart(problem, random, 1, true));
        if (neighborhoods[++index]) solver.addMove(new TwoShiftSmart(problem, random, 1, false));

        // creating and adding compound move 2-Swap
        //CompoundedMove swap2 = new CompoundedMove(problem, random, "2-Swap(mk)", 1);
        //swap2.addMove(new Swap(problem, random, 1, true));
        //swap2.addMove(new Swap(problem, random, 1, true));
        //solver.addMove(swap2);

        // creating and adding compound move 2-TaskMove
        //CompoundedMove taskMove2 = new CompoundedMove(problem, random, "2-TaskMove(mk)", 1);
        //taskMove2.addMove(new TaskMove(problem, random, 1, true));
        //taskMove2.addMove(new TaskMove(problem, random, 1, true));
        //solver.addMove(taskMove2);

        // creating and adding compound move 2-Shift
        //CompoundedMove shift2 = new CompoundedMove(problem, random, "2-Shift(mk)", 1);
        //shift2.addMove(new Shift(problem, random, 1, true));
        //shift2.addMove(new Shift(problem, random, 1, true));
        //solver.addMove(shift2);
    }

    /**
     * Prints the program usage.
     */
    public static void printUsage() {
        System.out.println("Usage: java -jar upmsp.jar <input> <output> [options]");
        System.out.println("    <input>  : Path of the problem input file.");
        System.out.println("    <output> : Path of the (output) solution file.");
        System.out.println();
        System.out.println("Options:");
        System.out.println("    -algorithm <algorithm> : ils, lahc, lahc-ils, sa, sa-ils, schc or schc-ils (default: " + algorithm + ").");
        System.out.println("    -bestKnown <makespan>  : best known makespan for RDP output (default: " + bestKnown + ").");
        System.out.println("    -seed <seed>           : random seed (default: " + seed + ").");
        System.out.println("    -maxIters <maxIters>   : maximum number of consecutive rejections (default: Long.MAXVALUE).");
        System.out.println("    -time <timeLimit>      : time limit in seconds (default: " + timeLimit + ").");
        System.out.println("    -validate              : executes the solver as a validator (existing output file will be checked).");
        System.out.println();
        System.out.println("    ILS parameters:");
        System.out.println("        -rnamax <rnamax> : maximum rejected iterations in the descent phase of ILS (default: " + rnaMax + ").");
        System.out.println("        -itersP <itersP> : number of iterations per perturbation level for ILS (default: " + itersP + ").");
        System.out.println("        -p0 <p0>         : initial perturbation level for ILS (default: " + p0 + ").");
        System.out.println("        -pMax <pMax>     : maximum steps up (each step of value p0) for ILS perturbation's level (default: " + pMax + ").");
        System.out.println();
        System.out.println("    LAHC parameters:");
        System.out.println("        -listSize <listSize> : LAHC list size  (default: " + listSize + ").");
        System.out.println();
        System.out.println("    SA parameters:");
        System.out.println("        -alpha <alpha> : cooling rate for the Simulated Annealing (default: " + alpha + ").");
        System.out.println("        -samax <samax> : iterations before updating the temperature for Simulated Annealing (default: " + saMax + ").");
        System.out.println("        -t0 <t0>       : initial temperature for the Simulated Annealing (default: " + t0 + ").");
        System.out.println();
        System.out.println("    SCHC parameters:");
        System.out.println("        -stepSize <stepSize> : SCHC step size (default: " + stepSize + ").");
        System.out.println();
        System.out.println("    Neighborhoods selection:");
        System.out.println("        -n <id,policy,value> : disables a policy(0..3) for neighborhood id(0..5) if value = 0 and enables it otherwise.");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("    java -jar upmsp.jar instance.txt solution.txt");
        System.out.println("    java -jar upmsp.jar instance.txt solution.txt -validate");
        System.out.println("    java -jar upmsp.jar instance.txt solution.txt -algorithm sa -alpha 0.98 -samax 1000 -t0 100000");
        System.out.println();
    }

    /**
     * Reads the input arguments.
     *
     * @param args the input arguments
     */
    public static boolean readArgs(String args[]) {
        if (args.length < 2) {
            printUsage();
            return false;
        }

        int index = -1;

        inFile = args[++index];
        outFile = args[++index];

        while (index < args.length - 1) {
            String option = args[++index].toLowerCase();

            switch (option) {
                case "-algorithm":
                    algorithm = args[++index].toLowerCase();
                    break;
                case "-seed":
                    seed = Integer.parseInt(args[++index]);
                    break;
                case "-maxiters":
                    maxIters = Long.parseLong(args[++index]);
                    break;
                case "-time":
                    timeLimit = Math.round(Double.parseDouble(args[++index]) * 1000.0);
                    break;
                case "-validate":
                    validate = true;
                    break;

                case "-bestknown":
                    bestKnown = Integer.parseInt(args[++index]);
                    break;

                // ILS
                case "-rnamax":
                    rnaMax = Long.parseLong(args[++index]);
                    break;
                case "-itersp":
                    itersP = Integer.parseInt(args[++index]);
                    break;
                case "-p0":
                    p0 = Integer.parseInt(args[++index]);
                    break;
                case "-pmax":
                    pMax = Integer.parseInt(args[++index]);
                    break;

                // LAHC
                case "-listsize":
                    listSize = Integer.parseInt(args[++index]);
                    break;

                // SA
                case "-alpha":
                    alpha = Double.parseDouble(args[++index]);
                    break;
                case "-samax":
                    saMax = Integer.parseInt(args[++index]);
                    break;
                case "-t0":
                    t0 = Double.parseDouble(args[++index]);
                    break;

                // SCHC
                case "-stepsize":
                    stepSize = Integer.parseInt(args[++index]);
                    break;

                // Neighborhoods selection
                case "-n":
                    String[] values = args[++index].split(",");
                    int i = Integer.parseInt(values[0]) * 4 + Integer.parseInt(values[1]);
                    neighborhoods[i] = values[2].equals("1");
                    break;

                default:
                    printUsage();
                    return false;
            }
        }

        return true;
    }
}
