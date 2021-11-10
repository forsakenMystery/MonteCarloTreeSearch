package main;

import java.util.ArrayList;
import java.util.Random;

public abstract class State {

    public boolean isInTree = false;
    public State parent = null;
    public Value value = null;
    public ArrayList<State> childs = null;
    protected int depth = 0;

    public abstract boolean isNotTerminal();

    public abstract boolean hasChild();

    public abstract Value getValue();

    public ArrayList<State> getChilds() {
        if (isInTree) {
            if (childs == null) {
                if (Main.death) {
                    System.out.println("refreshing");
                }
                childs = refreshChilds();
            }
            if (Main.death) {
                System.out.println("already had it");
            }
            return childs;
        }
        if (Main.death) {
            System.out.println("refreshing didnt had it");
        }
        return refreshChilds();
    }

    protected abstract ArrayList<State> refreshChilds();

    public State getRandomChild() {
        ArrayList<State> childss = getChilds();

        Random random = new Random(1);
        int v = random.nextInt(childss.size());
        
        return childss.get(v);
    }

    public void reset() {
        isInTree = false;
        value = null;
        parent = null;
        childs = null;
    }

    public int getDepth() {
        return depth;
    }

    public void reset(Game game) {
        reset();
    }
}
