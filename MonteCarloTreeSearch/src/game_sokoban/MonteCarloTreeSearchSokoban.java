/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_sokoban;

import java.util.ArrayList;
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
public class MonteCarloTreeSearchSokoban extends TreeSolver {

    public MonteCarloTreeSearchSokoban(Game game, Simulator simulator) {
        super(game, simulator);
    }

    @Override
    public State getBestNextState(State root) {
        root.reset(game);
        int time = 2000;
//        SokobanState.set.clear();
        while (time-- > 0) {
            if (Main.death) {
                System.out.println("time = " + time);
            }
            State leaf = selection(root);
            State expandedLeaf = expansion(leaf);
            Value simulationResult = rollout(expandedLeaf);
            backpropagation(simulationResult, expandedLeaf);
        }
        return bestChild(root);
    }

    private State selection(State state) {
        State st = state;
        if (Main.death) {
            System.out.println("========================");
            System.out.println("selection started");
            System.out.println("gonna select from = " + st);
        }
        int pes = 0;
        while (st.isInTree && st.isNotTerminal() && pes++ < 2 * 200) {
            st = best_uct(st);
            if (Main.death) {
                System.out.println("in selecting = " + st);
            }
        }
        if (Main.death) {
            System.out.println("selected state = " + st);
            System.out.println("selection ended");
            System.out.println("========================");
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

    private State best_uct(State state) {
        Value vx = state.value;
        ArrayList<State> childs = state.getChilds();
        // again no sag here :( dont forget sag.clear();
        State ans = null;
        Value vbest = null;
        for (State st : childs) {
            if (!st.isInTree) {
                return st;
            }
            Value vv = st.value;
            if (vbest == null || vbest.compareTo_UCT(vv, vx.num) < 0) {
                vbest = vv;
                ans = st;
            }
        }
        return ans;
    }

    private Value rollout(State state) {
        if (Main.death) {
            System.out.println("***************");
            System.out.println("into rollout: ");
        }
        if (Main.fastRollOut) {
            return fastRollout(state);
        }
        if (Main.death) {
            System.out.println("rolling in from = " + state);
        }
        int ses = 0;
        while (state.isNotTerminal() && ses++ < 200) {
            state = state.getRandomChild();
            if (Main.death) {
                System.out.println("rolling down randomly = " + state);
            }
        }
        if (Main.death) {
            System.out.println("rollout from");
            System.out.println("state = " + state);
            System.out.println("***************");
        }
        Value value = state.getValue();
        if (Main.death) {
            System.out.println("value = " + value);
        }
        return value;
    }

    private Value fastRollout(State state) {
        SokobanState st = new SokobanState((SokobanState) state);
        while (st.isNotTerminal()) {
            st.rollDown();
        }
        return st.getValue();
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
        ArrayList<State> sag = new ArrayList<>();
        Value vbest = null;
        if (Main.death) {
            System.out.println("******************");
        }
        for (State ch : childs) {
            Value vv = ch.value;
            if (Main.death) {
                System.out.println("==================");
                System.out.println("CH:\n" + ch + vv);
                System.out.println("==================");
            }
            if (vbest == null || vbest.compareTo(vv) < 0) {
                vbest = vv;
                sag.add(ch);
            } else if (vbest.compareTo(vv) == 0) {
                sag.add(ch);
            }
        }
        if (Main.death) {
            System.out.println("******************");
        }
        if (Main.death) {
            System.out.println("sag = " + sag);
        }
        Random r = new Random();
        ans = sag.get(r.nextInt(sag.size()));
        return ans;
    }

}

