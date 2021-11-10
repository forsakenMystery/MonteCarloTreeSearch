/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_lightUp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import main.Game;
import main.Main;
import main.Simulator;
import main.State;
import main.TreeSolver;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class MonteCarloTreeSearchLightUp extends TreeSolver {

    boolean answerFound = false;
    State answer = null;

    public MonteCarloTreeSearchLightUp(Game game, Simulator simulator) {
        super(game, simulator);
    }

    public State getBestNextState(State root, boolean first) {
        root.isInTree = false;
        if (Main.death) {
            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        }
        if (!first) {
//            System.out.println("root = " + root);
            if (root.childs != null && root.childs.isEmpty()) {
//                System.out.println("well");
//                System.out.println("root.childs.size() = " + root.childs.size());
                if (root.parent != null) {
//                    System.out.println("hah");
//                    System.out.println("root = " + root);
//                    System.out.println("root.parent = " + root.parent);
                    root.parent.childs.remove(root);
//                    System.out.println("root.parent.childs.size() = " + root.parent.childs.size());
                    return root.parent;
                } else {
                    System.out.println("WTF");
                    return root;
                }
            }
        }
        int time = Main.time;
        while (time-- > 0) {
            if (Main.death) {
                System.out.println("from root = " + root);
                System.out.println("//////////////\\\\\\\\\\\\\\\\\\\\\\\\\\");
                System.out.println("time = " + time);
            }
            State leaf = selection(root);
            if (Main.death) {
                System.out.println("leaf = " + leaf);
            }
            State expandedLeaf = expansion(leaf);
            Value simulationResult = rollout(expandedLeaf);
            if (Main.death) {
                System.out.println("simulationResult = " + simulationResult);
            }
            backpropagation(simulationResult, expandedLeaf);
        }
        if (answerFound) {
            return answer;
        }
        State bestChild = bestChild(root);
        return bestChild;
    }

    private State selection(State st) {
        if (Main.death) {
            System.out.println("into selection");
            System.out.println("st = " + st);
        }
        while (st.isInTree && st.isNotTerminal()) {
            st = best_uct(st);
            if (Main.death) {
                System.out.println("st = " + st);
            }

        }

        if (Main.death) {
            System.out.println("out of selection");
        }
        return st;
    }

    private State expansion(State st) {
        if (!st.isInTree) {
            st.isInTree = true;
            st.value = game.CreateZeroValue();
        } else if (st.isNotTerminal()) {
            System.out.println("---WTF---");
        }
        return st;
    }

    private Value rollout(State state) {
        if (Main.fastRollOut) {
            return fastRollout(state);
        }
        if (Main.death) {
            System.out.println("before rolling out");
            System.out.println("**********************");
        }
        while (state.isNotTerminal()) {
            state = state.getRandomChild();
        }
        if (Main.death) {
            System.out.println("after rolling out");
        }
        Value value = state.getValue();
        return value;
    }

    private void backpropagation(Value simulation_result, State state) {
        while (state != null) {
            if (state.isInTree) {
                state.value = state.value.update(state, simulation_result);
            }
            state = state.parent;
        }
    }

    private State bestChild(State state) {
        ArrayList<State> childs = state.getChilds();
        State ans = null;
        Value vbest = null;
        for (State ch : childs) {
            Value vv = ch.value;
            if (vbest == null || vbest.compareTo(vv) < 0) {
                vbest = vv;
                ans = ch;
            }
        }
        return ans;
    }

    private State best_uct(State state) {
        Value vx = state.value;
        ArrayList<State> childs = state.getChilds();
        State ans = null;
        Value vbest = null;
        if (Main.death) {
            System.out.println("best uct");
            System.out.println("childs");
            System.out.println("st = " + childs);
            System.out.println("state = " + state);
            System.out.println("============================================");
        }
        for (State st : childs) {
            if (!st.isInTree) {
                if (Main.death) {
                    System.out.println("from here returned this:");
                    System.out.println("s  = " + st);
                }
                return st;
            }
            Value vv = st.value;
            if (vbest == null || vbest.compareTo_UCT(vv, vx.num) < 0) {
                vbest = vv;
                ans = st;
            }
        }
        if (ans == null) {
            if (state.parent != null) {
                state.parent.childs.remove(state);
                return state.parent;
            } else {
                return state;
            }
        }
        return ans;
    }

    private Value fastRollout(State state) {
        LightUpState st = new LightUpState((LightUpState) state);
        if (Main.death) {
            System.out.println("sonic");
        }
        while (st.isNotTerminal()) {
            st.rollDown();
            if (Main.death) {
                System.out.println("st = " + st);
            }
        }
        if (st.getValue().value == 1) {
            answerFound = true;
            answer = st;
        }
        return st.getValue();
    }

    @Override
    public State getBestNextState(State state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
