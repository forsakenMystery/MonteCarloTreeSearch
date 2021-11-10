/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_lightUp;

import main.Action;
import main.Simulator;
import main.State;

/**
 *
 * @author Hamed Khashehchi
 */
public class LightUpSimulator extends Simulator {

    @Override
    public State simulate(State state, Action action) {
        return null;
    }

    static State simulateX(LightUpState state, LightUpAction action) {
        return new LightUpState(state, action);
    }
}
