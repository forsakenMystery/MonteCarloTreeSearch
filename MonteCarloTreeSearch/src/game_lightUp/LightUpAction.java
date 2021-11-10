/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_lightUp;

import main.Action;

/**
 *
 * @author Hamed Khashehchi
 */
public class LightUpAction extends Action {

    int x;
    int y;

    public LightUpAction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")"; //To change body of generated methods, choose Tools | Templates.
    }

}
