/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_colorFill;

import main.Action;
import main.Simulator;
import main.State;

/**
 *
 * @author Hamed Khashehchi
 */
public class CollorFillSimulator extends Simulator {

    static State simulateX(CollorFillState state, CollorFillAction action) {
        CollorFillAction act = (CollorFillAction) action;
        CollorFillState st = (CollorFillState) state;
//        System.out.println("st = " + st);
        return new CollorFillState(st, act);
    }

    @Override
    public State simulate(State state, Action action) {
        //does nothing now, lol :P
        return null;
    }
    
}
