/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_colorFill;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Main;
import main.Pair;
import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class CollorFillState extends State {

    private int height;
    private int width;
    private int minimum;
    private int color;
    private int[][] table;
    private int move;
    public ArrayList<State> well = new ArrayList<>();
//    HashSet<Integer> plays;

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
        final CollorFillState other = (CollorFillState) obj;
        if (!Arrays.deepEquals(this.table, other.table)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Arrays.deepHashCode(this.table);
        return hash;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public int getMove() {
        return move;
    }

    public int getMinimum() {
        return minimum;
    }

    public CollorFillState(int level) {
        long t = System.currentTimeMillis();
        try {
            File file;
            this.depth = 0;
            move = 0;
            file = new File(Main.LEVEL_PATH + "/level_" + level);
            Scanner sc;
            sc = new Scanner(file);
            String size = sc.next();
            String[] split = size.split("\\*");
            width = Integer.parseInt(split[0]);
            height = Integer.parseInt(split[1]);
            color = sc.nextInt();
            minimum = sc.nextInt();
            this.table = new int[width][height];
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    this.table[i][j] = sc.nextInt();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CollorFillState.class.getName()).log(Level.SEVERE, null, ex);
        }
        Main.FILLING_TIME += System.currentTimeMillis() - t;
    }

    public CollorFillState(CollorFillState s) {
        width = s.width;
        height = s.height;
        minimum = s.minimum;
        color = s.color;
        move = s.move;
        isInTree = s.isInTree;
        depth = s.depth;
        parent = s.parent;
        table = new int[width][];
        for (int i = 0; i < width; i++) {
            table[i] = s.table[i].clone();
        }
    }

    public CollorFillState(CollorFillState s, CollorFillAction act) {
        long t = System.currentTimeMillis();
        width = s.width;
        height = s.height;
        minimum = s.minimum;
        color = s.color;
        isInTree = false;
        move = s.move + 1;
        if (Main.death) {
            System.out.println("making new state");
            System.out.println(s.move + "==" + this.move);
            System.out.println("s==this");
        }
        depth = s.depth++;
//        System.out.println("depth: "+depth);
        parent = s;
        table = new int[width][];
        for (int i = 0; i < width; i++) {
            table[i] = s.table[i].clone();
        }

        int base = table[0][0];
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(0, 0));
        boolean flag[][] = new boolean[width][height];
        flag[0][0] = true;
        while (!q.isEmpty()) {
            Pair<Integer, Integer> dequeue = q.remove();
            table[dequeue.getKey()][dequeue.getValue()] = act.getCollor();
            if (dequeue.getValue() + 1 < height && table[dequeue.getKey()][dequeue.getValue() + 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() + 1));
                flag[dequeue.getKey()][dequeue.getValue() + 1] = true;
            }
            if (dequeue.getKey() + 1 < width && table[dequeue.getKey() + 1][dequeue.getValue()] == base) {
                q.add(new Pair<>(dequeue.getKey() + 1, dequeue.getValue()));
                flag[dequeue.getKey() + 1][dequeue.getValue()] = true;
            }
            if (dequeue.getValue() - 1 >= 0 && !flag[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() - 1));
                flag[dequeue.getKey()][dequeue.getValue() - 1] = true;
            }
        }
        if (Main.death) {
            System.out.println(s);
            System.out.println(this);
            System.out.println("__________________________");
        }
        Main.FILLING_TIME += System.currentTimeMillis() - t;
    }

    @Override
    public boolean isNotTerminal() {
        return hasChild();
    }

    @Override
    public boolean hasChild() {
        int base = table[0][0];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (table[i][j] != base) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Value getValue() {
        if (isNotTerminal()) {
            return null;
        }
        // gonna change in future
        // changed:D
//        System.out.println("minimum: "+minimum+", move: "+move);
        return new CollorFillValue(-1, minimum / (double) getMove());
    }

    @Override
    protected ArrayList<State> refreshChilds() {
        ArrayList<State> childss = new ArrayList<State>();
        // gonna change thiss too probably more better childs not randomly chosen from all possible colors
//        for (int i = 0; i < color; i++) {
//            if (i != table[0][0]) {
//                childss.add(CollorFillSimulator.simulateX(this, new CollorFillAction(i)));
//            }
//        }
        // changed:P
        int base = table[0][0];
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(0, 0));
        HashSet<Integer> ha = new HashSet<>();
        boolean flag[][] = new boolean[width][height];
        flag[0][0] = true;
        while (!q.isEmpty()) {
            Pair<Integer, Integer> dequeue = q.remove();
            if (dequeue.getValue() + 1 < height && table[dequeue.getKey()][dequeue.getValue() + 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() + 1));
                flag[dequeue.getKey()][dequeue.getValue() + 1] = true;
            } else if (dequeue.getValue() + 1 < height && table[dequeue.getKey()][dequeue.getValue() + 1] != base) {
                ha.add(table[dequeue.getKey()][dequeue.getValue() + 1]);
            }
            if (dequeue.getKey() + 1 < width && table[dequeue.getKey() + 1][dequeue.getValue()] == base) {
                q.add(new Pair<>(dequeue.getKey() + 1, dequeue.getValue()));
                flag[dequeue.getKey() + 1][dequeue.getValue()] = true;
            } else if (dequeue.getKey() + 1 < width && table[dequeue.getKey() + 1][dequeue.getValue()] != base) {
                ha.add(table[dequeue.getKey() + 1][dequeue.getValue()]);
            }
            if (dequeue.getValue() - 1 >= 0 && !flag[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() - 1));
                flag[dequeue.getKey()][dequeue.getValue() - 1] = true;
            } else if (dequeue.getValue() - 1 >= 0 && !flag[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] != base) {
                ha.add(table[dequeue.getKey()][dequeue.getValue() - 1]);
            }
        }
//        System.out.println("ha = " + ha);
//        System.out.println("plays = " + plays);
        ha.forEach((Integer t) -> {
            childss.add(CollorFillSimulator.simulateX(this, new CollorFillAction(t)));
        });
        if (Main.death) {
            System.out.println("childss = " + childss);
        }
        return childss;
    }

    @Override
    public String toString() {
        String s = "\n{";
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                s += table[i][j] + ", ";
            }
            s += (i != width - 1 ? "\n " : "");
        }
        return s + "}\nmove: " + move + "/" + minimum + "\n";
    }

    public void rollDown() {
        Random random = new Random();
        int base = table[0][0];
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(0, 0));
        HashSet<Integer> ha = new HashSet<>();
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        x.add(0);
        y.add(0);
        boolean flag[][] = new boolean[width][height];
        flag[0][0] = true;
        while (!q.isEmpty()) {
            Pair<Integer, Integer> dequeue = q.remove();
            if (dequeue.getValue() + 1 < height && table[dequeue.getKey()][dequeue.getValue() + 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() + 1));
                x.add(dequeue.getKey());
                y.add(dequeue.getValue() + 1);
                flag[dequeue.getKey()][dequeue.getValue() + 1] = true;
            } else if (dequeue.getValue() + 1 < height && table[dequeue.getKey()][dequeue.getValue() + 1] != base) {
                ha.add(table[dequeue.getKey()][dequeue.getValue() + 1]);
            }
            if (dequeue.getKey() + 1 < width && table[dequeue.getKey() + 1][dequeue.getValue()] == base) {
                q.add(new Pair<>(dequeue.getKey() + 1, dequeue.getValue()));
                x.add(dequeue.getKey() + 1);
                y.add(dequeue.getValue());
                flag[dequeue.getKey() + 1][dequeue.getValue()] = true;
            } else if (dequeue.getKey() + 1 < width && table[dequeue.getKey() + 1][dequeue.getValue()] != base) {
                ha.add(table[dequeue.getKey() + 1][dequeue.getValue()]);
            }
            if (dequeue.getValue() - 1 >= 0 && !flag[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() - 1));
                x.add(dequeue.getKey());
                y.add(dequeue.getValue() - 1);
                flag[dequeue.getKey()][dequeue.getValue() - 1] = true;
            } else if (dequeue.getValue() - 1 >= 0 && !flag[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] != base) {
                ha.add(table[dequeue.getKey()][dequeue.getValue() - 1]);
            }
        }
        move++;
        int v = random.nextInt(ha.size());
        int ans = 0;

        for (Integer a : ha) {
            if (ans == v) {
                ans = a;
                break;
            }
            ans++;
        }
        for (int i = 0; i < x.size(); i++) {
            this.table[x.get(i)][y.get(i)] = ans;
        }
        well.add(new CollorFillState(this));
    }

}
