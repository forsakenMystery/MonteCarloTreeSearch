/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_clickomania;

import java.util.ArrayList;
import main.Game;
import main.Main;
import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class ClickomaniaGame extends Game {
    public ArrayList<State> flow = new ArrayList<>();
    @Override
    public void init() {
        myState = new ClickomaniaState(Main.level);
        flow = new ArrayList<>();
        flow.add(myState);
    }

    @Override
    public void updateState(State newState) {
        myState = newState;
        flow.add(myState);
    }
    
    @Override
    public Value CreateZeroValue() {
        return new ClickomaniaValue(0, 0);
    }
    
}
