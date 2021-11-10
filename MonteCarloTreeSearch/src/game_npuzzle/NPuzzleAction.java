/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_npuzzle;

import main.Action;

/**
 *
 * @author Hamed Khashehchi
 */
public class NPuzzleAction extends Action {

    int dx;
    int dy;

    public NPuzzleAction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public NPuzzleAction reverse() {
        return new NPuzzleAction(-dx, -dy);
    }

    @Override
    public String toString() {
        String sight = "";
        // human readable move dont convert it
        if (dx == 1) {
            sight = "up";
        }
        if(dx == -1){
            sight = "down";
        }
        if (dy == 1) {
            sight = "left";
        }
        if(dy == -1){
            sight = "right";
        }
        return sight+": (" + dx + ", " + dy + ")"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NPuzzleAction other = (NPuzzleAction) obj;
        if (this.dx != other.dx) {
            return false;
        }
        if (this.dy != other.dy) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.dx;
        hash = 79 * hash + this.dy;
        return hash;
    }

}
