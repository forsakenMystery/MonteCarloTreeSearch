/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_colorFill;

import main.Action;

/**
 *
 * @author Hamed Khashehchi
 */
public class CollorFillAction extends Action{
    private int c;

    public CollorFillAction(int c) {
        this.c = c;
    }
    
    public int getCollor() {
        return c;
    }
    
}
