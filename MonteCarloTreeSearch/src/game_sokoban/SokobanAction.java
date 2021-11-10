/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_sokoban;

import main.Action;

/**
 *
 * @author Hamed Khashehchi
 */
public class SokobanAction extends Action {


	public int x;
	public int y;

	public SokobanAction(int x, int y) {
		this.x = x;
		this.y = y;
	}
        
}