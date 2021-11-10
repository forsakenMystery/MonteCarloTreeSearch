/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_sokoban;

import main.Action;
import main.Simulator;
import main.State;

/**
 *
 * @author Hamed Khashehchi
 */
public class SokobanSimulator extends Simulator {

    public SokobanSimulator() {
    }

    @Override
    public State simulate(State state, Action action) {
        return null;
    }

    public static State simulateX(State state, Action action) {
        SokobanAction act = (SokobanAction) action;
        SokobanState st = (SokobanState) state;
//        System.out.println("st = " + st);
        SokobanState res = new SokobanState(st, act);
        return res;
    }

}
