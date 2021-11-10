/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_lightUp;

import java.util.ArrayList;
import main.Game;
import main.Main;
import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class LightUpGame extends Game {
    @Override
    public void init() {
        myState = new LightUpState(Main.level);
    }

    @Override
    public void updateState(State newState) {
        myState = newState;
    }

    @Override
    public Value CreateZeroValue() {
        return new LightUpValue(0, 0);
    }

}
