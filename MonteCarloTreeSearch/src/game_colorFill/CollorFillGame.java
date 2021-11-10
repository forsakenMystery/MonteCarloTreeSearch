/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_colorFill;

import main.Game;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
import main.Main;

public class CollorFillGame extends Game {

    @Override
    public void init() {
        myState = new CollorFillState(Main.level);
    }

    @Override
    public Value CreateZeroValue() {
        return new CollorFillValue(0, 0);
    }

}
