/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_sokoban;

import main.Game;
import main.Main;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class SokobanGame extends Game {
    
    @Override
    public void init() {
        myState = new SokobanState(Main.level);
    }
    
    @Override
    public Value CreateZeroValue() {
        return new SokobanValue(0, 0);
    }

}
