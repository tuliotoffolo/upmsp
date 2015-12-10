package upmsp.algorithm.learning;

import upmsp.algorithm.heuristic.*;
import upmsp.algorithm.neighborhood.*;

import java.io.*;
import java.util.*;

public class LearningAutomata implements Serializable {

    public final static double LEARNING_RATE = 1e-4;
    public final static boolean LOGGING = false;
    public final static int N_SELECTED_ACTIONS = 1;

    public final Random random;

    private List<Move> moves;
    private int nMoves;

    private int nIters = 0;
    private double[] probabilities;
    private int lastMove;

    private Queue<Integer> lastNMoves = new LinkedList<>();
    private double learningRate = LEARNING_RATE;
    private double learningRate2 = learningRate;

    private PrintWriter logWriter;

    public LearningAutomata(Random random, Heuristic heuristic) {
        this.random = random;
        this.moves = heuristic.getMoves();

        initProbabilities(heuristic.getMoves());
        if (LOGGING) {
            try {
                logWriter = new PrintWriter(new File("output/learninglog.csv"));
                for (Move move : heuristic.getMoves())
                    logWriter.write(move.name + ";");
                logWriter.write("\n");
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public LearningAutomata(Random random, Heuristic heuristic, int nMoves, double learningRate, double epsilon, List<Move> moves) {
        this(random, heuristic);

        this.nMoves = nMoves;
        this.learningRate = learningRate;
        this.learningRate2 = learningRate * epsilon;
        this.probabilities = new double[nMoves];
    }

    public void initProbabilities(List<Move> moves) {
        this.moves = moves;
        this.nMoves = moves.size();
        if (nMoves == 0) return;

        probabilities = new double[moves.size()];
        double sumWeights = 0;
        for (Move move : moves)
            sumWeights += move.getPriority();

        for (int i = 0; i < nMoves; i++)
            probabilities[i] = moves.get(i).getPriority() / sumWeights;
    }

    public int nextAction() {
        // return action with highest probability
        double w = random.nextDouble();
        for (int i = 0; i < nMoves; i++) {
            if (w < probabilities[i]) {
                lastMove = i;
                break;
            }
            w -= probabilities[i];
        }
        nIters++;

        if (N_SELECTED_ACTIONS > 1) {
            lastNMoves.add(lastMove);
            if (lastNMoves.size() > N_SELECTED_ACTIONS)
                lastNMoves.poll();
        }

        if (LOGGING && nIters % 1000 == 0) {
            for (int i = 0; i < nMoves; i++)
                logWriter.printf("%.8f;", probabilities[i]);
            logWriter.println();
            logWriter.flush();
        }

        return lastMove;
    }

    public void updateNProbabilities(double obtainedReinforcement) {
        int i = lastNMoves.size() - 1;
        for (Integer usedAction : lastNMoves) {
            updateProbabilities(usedAction, obtainedReinforcement / Math.pow(2, i)); // TODO: check discount function
            i--;
        }
    }

    public void updateProbabilities(double obtainedReinforcement) {
        updateProbabilities(lastMove, obtainedReinforcement);
    }

    public void updateProbabilities(int usedMove, double obtainedReinforcement) {
        for (int i = 0; i < nMoves; i++) {
            if (usedMove == i) {
                probabilities[i] = probabilities[i] + learningRate * obtainedReinforcement * (1 - probabilities[i])
                  - learningRate2 * (1 - obtainedReinforcement) * probabilities[i];
            }
            else {
                probabilities[i] = probabilities[i] - learningRate * obtainedReinforcement * probabilities[i]
                  + learningRate2 * (1 - obtainedReinforcement)
                  * ((( double ) 1 / (( double ) nMoves - ( double ) 1)) - probabilities[i]);
            }
        }
    }


    // region getters and setters

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double[] getProbabilities() {
        return probabilities;
    }

    public int getLastMove() {
        return lastMove;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nMoves; i++) {
            sb.append(probabilities[i] + "\t");
        }
        sb.append("\n");
        return new String(sb);

    }

    // endregion getters and setters
}
