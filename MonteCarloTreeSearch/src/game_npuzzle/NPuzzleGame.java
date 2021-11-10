/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_npuzzle;

import main.Game;
import main.Main;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class NPuzzleGame extends Game {

    @Override
    public void init() {
        myState = new NPuzzleState(Main.level);
    }

    @Override
    public Value CreateZeroValue() {
        return new NPuzzleValue(0, 0);
    }
    
}
