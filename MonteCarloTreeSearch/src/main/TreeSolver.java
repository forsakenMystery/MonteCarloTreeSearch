package main;

public abstract class TreeSolver {

    protected Simulator simulator;
    protected Game game;

    public TreeSolver(Game game, Simulator simulator) {
        this.game = game;
        this.simulator = simulator;
    }

    abstract public State getBestNextState(State state);
}
