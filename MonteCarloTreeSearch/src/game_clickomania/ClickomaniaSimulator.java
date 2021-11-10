/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_clickomania;

import main.Action;
import main.Simulator;
import main.State;

/**
 *
 * @author Hamed Khashehchi
 */
public class ClickomaniaSimulator extends Simulator {

    @Override
    public State simulate(State state, Action action) {
        return null;
    }

    static State simulateX(ClickomaniaState state, ClickomaniaAction action) {
        ClickomaniaAction act = (ClickomaniaAction) action;
        ClickomaniaState st = (ClickomaniaState) state;
//        System.out.println("st = " + st);
        return new ClickomaniaState(st, act);
    }
}
