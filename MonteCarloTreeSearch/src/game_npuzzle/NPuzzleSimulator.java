/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_npuzzle;

import main.Action;
import main.Simulator;
import main.State;

/**
 *
 * @author Hamed Khashehchi
 */
public class NPuzzleSimulator extends Simulator{

    static State simulateX(State state, Action action) {
        NPuzzleAction act = (NPuzzleAction) action;
        NPuzzleState st = (NPuzzleState) state;
        return new NPuzzleState(st, act);
    }
    
    @Override
    public State simulate(State state, Action action) {
        return null;
    }
    
}
