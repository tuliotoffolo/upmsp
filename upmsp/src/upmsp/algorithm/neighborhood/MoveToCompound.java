package upmsp.algorithm.neighborhood;

import upmsp.model.*;
import upmsp.model.solution.*;

import java.util.*;

/**
 * This class represents a "Compoundable" Move, i.e. a Move that can be added to
 * a CompoundMove neighborhood. For this, the Move must execute the move when
 * the {@link #doMove(Solution)} is called, instead of only calculating the
 * delta and executing the move if {@link #accept()} is called.
 *
 * @author Tulio Toffolo
 */
public abstract class MoveToCompound extends Move {

    protected boolean inChain = false;

    /**
     * Instantiates a new Move.
     *
     * @param problem the problem reference.
     * @param random  the random number generator.
     */
    public MoveToCompound(Problem problem, Random random) {
        super(problem, random);
    }

    /**
     * Instantiates a new Move.
     *
     * @param problem  the problem reference.
     * @param random   the random number generator.
     * @param name     the name of this neighborhood (for debugging purposes).
     * @param priority the priority (priority) of this neighborhood structure.
     *                 The larger the value, the higher the priority.
     */
    public MoveToCompound(Problem problem, Random random, String name, int priority) {
        super(problem, random, name, priority);
    }

    /**
     * Sets if this Move is part of a chain (or part of a CompoundedMove).
     *
     * @param inChain true if this Move is part of a chain and false otherwise.
     */
    public void setInChain(boolean inChain) {
        this.inChain = inChain;
    }
}
