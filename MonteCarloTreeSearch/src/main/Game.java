package main;

public abstract class Game {

    protected State myState;

    public abstract void init();

    public boolean notEnded() {
        return myState.isNotTerminal();
    }

    public abstract Value CreateZeroValue();

    public State getState() {
        return myState;
    }

    public void updateState(State newState) {
        myState = newState;
    }
}
