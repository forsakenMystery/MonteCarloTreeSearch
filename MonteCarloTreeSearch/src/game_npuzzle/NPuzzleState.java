/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_npuzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Main;
import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class NPuzzleState extends State {

    int n;
    int table[][];
    int x;
    int y;
    ArrayList<NPuzzleAction> moves;

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
        final NPuzzleState other = (NPuzzleState) obj;

        return Arrays.deepEquals(this.table, other.table);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.table);
    }

    public NPuzzleState(NPuzzleState s, NPuzzleAction act) {
        long t = System.currentTimeMillis();
        n = s.n;
        isInTree = false;
        moves = new ArrayList<>(s.moves);
        moves.add(act);
        if (Main.death) {
            System.out.println("making new state");
            System.out.println("s==this");
        }
        depth = s.depth++;
        parent = s;
        table = new int[n][];
        for (int i = 0; i < n; i++) {
            table[i] = s.table[i].clone();
        }
        x = s.x;
        y = s.y;
        int temp = table[x + act.dx][y + act.dy];
        table[x + act.dx][y + act.dy] = table[x][y];
        table[x][y] = temp;

        x += act.dx;
        y += act.dy;

        if (Main.death) {
            System.out.println(s);
            System.out.println(this);
            System.out.println("__________________________");
        }
        Main.FILLING_TIME += System.currentTimeMillis() - t;
    }

    public NPuzzleState(NPuzzleState s) {
        n = s.n;
        isInTree = s.isInTree;
        depth = s.depth;
        parent = s.parent;
        x = s.x;
        y = s.y;
        moves = (ArrayList<NPuzzleAction>) s.moves.clone();
        table = new int[n][];
        for (int i = 0; i < n; i++) {
            table[i] = s.table[i].clone();
        }
    }

    public NPuzzleState(int level) {
        long t = System.currentTimeMillis();
        try {
            File file;
            this.depth = 0;
            file = new File(Main.LEVEL_PATH + "/level_" + level);
            Scanner sc;
            sc = new Scanner(file);
            n = sc.nextInt();
            this.table = new int[n][n];
            for (int i = 0; i < this.n; i++) {
                for (int j = 0; j < this.n; j++) {
                    this.table[i][j] = sc.nextInt();
                    if (table[i][j] == 0) {
                        x = i;
                        y = j;
                    }
                }
            }
            moves = new ArrayList<>();
            //  /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NPuzzleState.class.getName()).log(Level.SEVERE, null, ex);
        }
        Main.FILLING_TIME += System.currentTimeMillis() - t;
    }

    @Override
    public boolean isNotTerminal() {
        return hasChild();
    }

    @Override
    public boolean hasChild() {
        float f = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (table[i][j] != (i * n + j + 1) % (n * n)) {
                    q:
                    for (int m = 0; m < n; m++) {
                        for (int s = 0; s < n; s++) {
                            if (table[m][s] == (i * n + j + 1) % (n * n)) {
                                f += Math.sqrt(((i - m) * (i - m)) + ((j - s) * (j - s)));
                                break q;
                            }
                        }
                    }
                }
            }
        }
        if (f == 0) {
            return false;
        }
        return !(moves.size() > 210);
    }

    @Override
    public Value getValue() {
        int check = 1;
        int sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (table[i][j] == (check++) % (n * n)) {
                    sum++;
                }
            }
        }
//        System.out.println("sum = " + sum);
//         in place correctness and oopsie
        float f = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (table[i][j] != (i * n + j + 1) % (n * n)) {
                    q:
                    for (int m = 0; m < n; m++) {
                        for (int s = 0; s < n; s++) {
                            if (table[m][s] == (i * n + j + 1) % (n * n)) {
                                f += Math.sqrt(((i - m) * (i - m)) + ((j - s) * (j - s)));
                                break q;
                            }
                        }
                    }
                }
            }
        }
//        System.out.println("f = " + f);
        // weighted avg for hamiltoni and manhatan
        return new NPuzzleValue(-1, ((sum / (double) (n * n)) + (1 / (double) (1 + f))) / 2);
//        return new NPuzzleValue(-1, sum / (double) (n * n));
//        if (f == 0) {
//            return new NPuzzleValue(-1, 1);
//        } else {
//            return new NPuzzleValue(-1, 0);
//        }
    }

    @Override
    protected ArrayList<State> refreshChilds() {
        ArrayList<State> childss = new ArrayList<>();
        if (x + 1 < n) {
            NPuzzleAction act = new NPuzzleAction(+1, 0);
            if (moves.isEmpty() || !moves.get(moves.size() - 1).equals(act.reverse())) {
                childss.add(NPuzzleSimulator.simulateX(this, act));
            }
        }
        if (y + 1 < n) {
            NPuzzleAction act = new NPuzzleAction(0, +1);
            if (moves.isEmpty() || !moves.get(moves.size() - 1).equals(act.reverse())) {
                childss.add(NPuzzleSimulator.simulateX(this, new NPuzzleAction(0, +1)));
            }
        }
        if (x - 1 >= 0) {
            NPuzzleAction act = new NPuzzleAction(-1, 0);
            if (moves.isEmpty() || !moves.get(moves.size() - 1).equals(act.reverse())) {
                childss.add(NPuzzleSimulator.simulateX(this, new NPuzzleAction(-1, 0)));
            }
        }
        if (y - 1 >= 0) {
            NPuzzleAction act = new NPuzzleAction(0, -1);
            if (moves.isEmpty() || !moves.get(moves.size() - 1).equals(act.reverse())) {
                childss.add(NPuzzleSimulator.simulateX(this, new NPuzzleAction(0, -1)));
            }
        }
        return childss;
    }

    @Override
    public String toString() {
        String s = "\n{";
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                s += String.format("%2s", Integer.toString(table[i][j])) + ", ";
            }
            s += (i != n - 1 ? "\n " : "");
        }
        return s + "}\n, move: " + moves.size() + "\n";
    }

    public void rollDown() {
        //for now nothing
        Random r = new Random();
        ArrayList<NPuzzleAction> childss = new ArrayList<>();
        if (x + 1 < n) {
            NPuzzleAction act = new NPuzzleAction(+1, 0);
            if (moves.isEmpty() || !moves.get(moves.size() - 1).equals(act.reverse())) {
                childss.add(act);
            }
        }
        if (y + 1 < n) {
            NPuzzleAction act = new NPuzzleAction(0, +1);
            if (moves.isEmpty() || !moves.get(moves.size() - 1).equals(act.reverse())) {
                childss.add(act);
            }
        }
        if (x - 1 >= 0) {
            NPuzzleAction act = new NPuzzleAction(-1, 0);
            if (moves.isEmpty() || !moves.get(moves.size() - 1).equals(act.reverse())) {
                childss.add(act);
            }
        }
        if (y - 1 >= 0) {
            NPuzzleAction act = new NPuzzleAction(0, -1);
            if (moves.isEmpty() || !moves.get(moves.size() - 1).equals(act.reverse())) {
                childss.add(act);
            }
        }
        NPuzzleAction act = childss.get(r.nextInt(childss.size()));
        int temp = table[x + act.dx][y + act.dy];
        table[x + act.dx][y + act.dy] = table[x][y];
        table[x][y] = temp;
        moves.add(act);
//        System.out.println("moves = " + moves);
        x += act.dx;
        y += act.dy;

    }
}
