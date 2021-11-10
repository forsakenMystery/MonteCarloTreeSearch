/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_clickomania;

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
public class ClickomaniaState extends State {
    
    int width;
    int height;
    int color;
    int table[][];
    int score;
    public static int taboo;
    int lastMove;
    public static boolean clickomania;
    public ArrayList<ClickomaniaState> well = new ArrayList<>();
    
    @Override
    public int hashCode() {
        int hash = 13;
        hash = 79 * hash + Arrays.deepHashCode(this.table);
        return hash;
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
        final ClickomaniaState other = (ClickomaniaState) obj;
        if (!Arrays.deepEquals(this.table, other.table)) {
            return false;
        }
        return true;
    }
    
    public ClickomaniaState(int level) {
        long t = System.currentTimeMillis();
        try {
            lastMove = -1;
            File file;
            this.depth = 0;
            score = 0;
            file = new File(Main.LEVEL_PATH + "/level_" + level);
            Scanner sc;
            sc = new Scanner(file);
            String size = sc.next();
            String[] split = size.split("\\*");
            width = Integer.parseInt(split[0]);
            height = Integer.parseInt(split[1]);
            color = sc.nextInt();
            Random r = new Random();
            if (taboo != -1) {
                taboo = r.nextInt(color) + 1;
            }
            this.table = new int[width][height];
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    this.table[i][j] = sc.nextInt();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClickomaniaState.class.getName()).log(Level.SEVERE, null, ex);
        }
        Main.FILLING_TIME += System.currentTimeMillis() - t;
    }
    
    public ClickomaniaState(ClickomaniaState s, ClickomaniaAction act) {
        long t = System.currentTimeMillis();
        width = s.width;
        height = s.height;
        color = s.color;
        isInTree = false;
        if (Main.death) {
            System.out.println("making new state");
            System.out.println("s==this");
        }
        depth = s.depth++;
//        System.out.println("depth: "+depth);
        parent = s;
        table = new int[width][];
        for (int i = 0; i < width; i++) {
            table[i] = s.table[i].clone();
        }
        
        int base = table[act.x][act.y];
        lastMove = base;
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(act.x, act.y));
        boolean flag[][] = new boolean[width][height];
        flag[act.x][act.y] = true;
        int group = 0;
        while (!q.isEmpty()) {
            group++;
            Pair<Integer, Integer> dequeue = q.remove();
            table[dequeue.getKey()][dequeue.getValue()] = 0;
            if (dequeue.getValue() + 1 < height && !flag[dequeue.getKey()][dequeue.getValue() + 1] && table[dequeue.getKey()][dequeue.getValue() + 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() + 1));
                flag[dequeue.getKey()][dequeue.getValue() + 1] = true;
            }
            if (dequeue.getKey() + 1 < width && !flag[dequeue.getKey() + 1][dequeue.getValue()] && table[dequeue.getKey() + 1][dequeue.getValue()] == base) {
                q.add(new Pair<>(dequeue.getKey() + 1, dequeue.getValue()));
                flag[dequeue.getKey() + 1][dequeue.getValue()] = true;
            }
            if (dequeue.getValue() - 1 >= 0 && !flag[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() - 1));
                flag[dequeue.getKey()][dequeue.getValue() - 1] = true;
            }
            if (dequeue.getKey() - 1 >= 0 && !flag[dequeue.getKey() - 1][dequeue.getValue()] && table[dequeue.getKey() - 1][dequeue.getValue()] == base) {
                q.add(new Pair<>(dequeue.getKey() - 1, dequeue.getValue()));
                flag[dequeue.getKey() - 1][dequeue.getValue()] = true;
            }
            
        }
        //same (group_size - 2)^2 is added
        if (Main.death) {
            System.out.println("act = " + act);
            System.out.println("group = " + group);
        }
        group -= 2;
        score = s.score + (group * group) + 1;
        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height; j++) {
                if (table[i][j] != 0 && table[i + 1][j] == 0) {
                    int temp = table[i][j];
                    table[i][j] = table[i + 1][j];
                    table[i + 1][j] = temp;
                    i -= 2;
                    if (i < 0) {
                        i = -1;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < height - 1; i++) {
            if (table[width - 1][i] == 0 && table[width - 1][i + 1] != 0) {
                for (int j = 0; j < width; j++) {
                    int temp = table[j][i];
                    table[j][i] = table[j][i + 1];
                    table[j][i + 1] = temp;
                }
                i -= 2;
                if (i < 0) {
                    i = -1;
                }
            }
        }
        if (Main.death) {
            System.out.println(s);
            System.out.println(this);
            System.out.println("__________________________");
        }
        Main.FILLING_TIME += System.currentTimeMillis() - t;
    }
    
    public ClickomaniaState(ClickomaniaState s) {
        width = s.width;
        height = s.height;
        color = s.color;
        isInTree = s.isInTree;
        depth = s.depth;
        lastMove = s.lastMove;
        score = s.score;
        parent = s.parent;
        table = new int[width][];
        for (int i = 0; i < width; i++) {
            table[i] = s.table[i].clone();
        }
    }
    
    @Override
    public boolean isNotTerminal() {
        return hasChild();
    }
    
    @Override
    public boolean hasChild() {
        // I doubt this :D
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (table[i][j] != 0) {
                    if (i + 1 < width) {
                        if (table[i + 1][j] == table[i][j]) {
                            return true;
                        }
                    }
                    if (j + 1 < height) {
                        if (table[i][j + 1] == table[i][j]) {
                            return true;
                        }
                    }
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
        // 3 variations here
        // click o mania if you finish you get 1000 else 0
        // same game if you finish you get 1000*m*n and what you destroyed is counted too  (groupsize - 2)^2 is summed
        int sum = 0;
        HashSet<Integer> set = new HashSet<>();
        for (int i = width - 1; i >= 0; i--) {
            for (int j = 0; j < height; j++) {
                if (table[i][j] != 0) {
                    set.add(table[i][j]);
                    sum++;
                }
            }
        }
        if (clickomania) {
            return new ClickomaniaValue(-1, (width * height - sum) / (double) (width * height)); // clickomania
        } else {
            return new ClickomaniaValue(-1, score + (sum == 0 ? 1000 : 0)); // same game
        }
    }
    
    @Override
    protected ArrayList<State> refreshChilds() {
        //too much overhead I know that but what can i do i can do some stuff but it takes too much time which is better i dont know :(
        HashSet<State> childss = new HashSet<>();
        boolean visited[][] = new boolean[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (table[i][j] == 0) {
                    visited[i][j] = true;
                }
                if (!visited[i][j]) {
                    visited[i][j] = true;
                    if (i + 1 < width) {
                        if (!visited[i + 1][j] && table[i + 1][j] == table[i][j]) {
                            visited[i + 1][j] = true;
                            State simulateX = ClickomaniaSimulator.simulateX(this, new ClickomaniaAction(i, j));
                            childss.add(simulateX);
                        }
                    }
                    if (j + 1 < height) {
                        if (!visited[i][j + 1] && table[i][j + 1] == table[i][j]) {
                            visited[i][j + 1] = true;
                            State simulateX = ClickomaniaSimulator.simulateX(this, new ClickomaniaAction(i, j));
                            childss.add(simulateX);
                        }
                    }
                }
            }
        }
        ArrayList<State> childsss = new ArrayList<>(childss);
        for (int i = childsss.size() - 1; i >= 0; i--) {
            if (((ClickomaniaState) childsss.get(i)).lastMove == taboo) {
                childsss.remove(i);
            }
        }
        if (childsss.isEmpty()) {
            childsss.addAll(childss);
        }
        return childsss;
    }
    
    public void rollDown() {
        //Imma do nothing for now
        //Imma do something now
        Random r = new Random();
        ArrayList<Pair<Integer, Integer>> actions = new ArrayList<>();
        ArrayList<Pair<Integer, Pair<Integer, Integer>>> col = new ArrayList<>();
        boolean visited[][] = new boolean[width][height];
        parent = new ClickomaniaState(this);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (table[i][j] == 0) {
                    visited[i][j] = true;
                    int base = table[i][j];
                    Queue<Pair<Integer, Integer>> q = new LinkedList();
                    q.add(new Pair<>(i, j));
                    while (!q.isEmpty()) {
                        Pair<Integer, Integer> dequeue = q.remove();
                        if (dequeue.getValue() + 1 < height && !visited[dequeue.getKey()][dequeue.getValue() + 1] && table[dequeue.getKey()][dequeue.getValue() + 1] == base) {
                            q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() + 1));
                            visited[dequeue.getKey()][dequeue.getValue() + 1] = true;
                        }
                        if (dequeue.getKey() + 1 < width && !visited[dequeue.getKey() + 1][dequeue.getValue()] && table[dequeue.getKey() + 1][dequeue.getValue()] == base) {
                            q.add(new Pair<>(dequeue.getKey() + 1, dequeue.getValue()));
                            visited[dequeue.getKey() + 1][dequeue.getValue()] = true;
                        }
                        if (dequeue.getValue() - 1 >= 0 && !visited[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] == base) {
                            q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() - 1));
                            visited[dequeue.getKey()][dequeue.getValue() - 1] = true;
                        }
                        if (dequeue.getKey() - 1 >= 0 && !visited[dequeue.getKey() - 1][dequeue.getValue()] && table[dequeue.getKey() - 1][dequeue.getValue()] == base) {
                            q.add(new Pair<>(dequeue.getKey() - 1, dequeue.getValue()));
                            visited[dequeue.getKey() - 1][dequeue.getValue()] = true;
                        }
                    }
                }
                if (!visited[i][j]) {
                    visited[i][j] = true;
                    Pair<Integer, Integer> pp = new Pair<>(i, j);
                    actions.add(pp);
                    int base = table[i][j];
                    Pair<Integer, Pair<Integer, Integer>> pair = new Pair<>(base, actions.get(actions.size() - 1));
                    col.add(pair);
                    Queue<Pair<Integer, Integer>> q = new LinkedList<>();
                    q.add(new Pair<>(i, j));
                    int qq = 0;
                    while (!q.isEmpty()) {
                        qq++;
                        Pair<Integer, Integer> dequeue = q.remove();
                        if (dequeue.getValue() + 1 < height && !visited[dequeue.getKey()][dequeue.getValue() + 1] && table[dequeue.getKey()][dequeue.getValue() + 1] == base) {
                            q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() + 1));
                            visited[dequeue.getKey()][dequeue.getValue() + 1] = true;
                        }
                        if (dequeue.getKey() + 1 < width && !visited[dequeue.getKey() + 1][dequeue.getValue()] && table[dequeue.getKey() + 1][dequeue.getValue()] == base) {
                            q.add(new Pair<>(dequeue.getKey() + 1, dequeue.getValue()));
                            visited[dequeue.getKey() + 1][dequeue.getValue()] = true;
                        }
                        if (dequeue.getValue() - 1 >= 0 && !visited[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] == base) {
                            q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() - 1));
                            visited[dequeue.getKey()][dequeue.getValue() - 1] = true;
                        }
                        if (dequeue.getKey() - 1 >= 0 && !visited[dequeue.getKey() - 1][dequeue.getValue()] && table[dequeue.getKey() - 1][dequeue.getValue()] == base) {
                            q.add(new Pair<>(dequeue.getKey() - 1, dequeue.getValue()));
                            visited[dequeue.getKey() - 1][dequeue.getValue()] = true;
                        }
                    }
                    if (qq <= 1) {
                        col.remove(pair);
                        actions.remove(pp);
                    }
                }
            }
        }
        for (int i = col.size() - 1; i >= 0; i--) {
            if (col.get(i).getKey() == taboo) {
                col.remove(i);
            }
        }
        Pair<Integer, Integer> act;
        if (col.isEmpty()) {
            act = actions.get(r.nextInt(actions.size()));
        } else {
            act = col.get(r.nextInt(col.size())).getValue();
        }
        int base = table[act.getKey()][act.getValue()];
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(act.getKey(), act.getValue()));
        boolean flag[][] = new boolean[width][height];
        flag[act.getKey()][act.getValue()] = true;
        int group = 0;
        while (!q.isEmpty()) {
            Pair<Integer, Integer> dequeue = q.remove();
            table[dequeue.getKey()][dequeue.getValue()] = 0;
            if (dequeue.getValue() + 1 < height && !flag[dequeue.getKey()][dequeue.getValue() + 1] && table[dequeue.getKey()][dequeue.getValue() + 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() + 1));
                flag[dequeue.getKey()][dequeue.getValue() + 1] = true;
            }
            if (dequeue.getKey() + 1 < width && !flag[dequeue.getKey() + 1][dequeue.getValue()] && table[dequeue.getKey() + 1][dequeue.getValue()] == base) {
                q.add(new Pair<>(dequeue.getKey() + 1, dequeue.getValue()));
                flag[dequeue.getKey() + 1][dequeue.getValue()] = true;
            }
            if (dequeue.getValue() - 1 >= 0 && !flag[dequeue.getKey()][dequeue.getValue() - 1] && table[dequeue.getKey()][dequeue.getValue() - 1] == base) {
                q.add(new Pair<>(dequeue.getKey(), dequeue.getValue() - 1));
                flag[dequeue.getKey()][dequeue.getValue() - 1] = true;
            }
            if (dequeue.getKey() - 1 >= 0 && !flag[dequeue.getKey() - 1][dequeue.getValue()] && table[dequeue.getKey() - 1][dequeue.getValue()] == base) {
                q.add(new Pair<>(dequeue.getKey() - 1, dequeue.getValue()));
                flag[dequeue.getKey() - 1][dequeue.getValue()] = true;
            }
        }
        group -= 2;
        score += (group * group) + 1;
        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height; j++) {
                if (table[i][j] != 0 && table[i + 1][j] == 0) {
                    int temp = table[i][j];
                    table[i][j] = table[i + 1][j];
                    table[i + 1][j] = temp;
                    i -= 2;
                    if (i < 0) {
                        i = -1;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < height - 1; i++) {
            if (table[width - 1][i] == 0 && table[width - 1][i + 1] != 0) {
                for (int j = 0; j < width; j++) {
                    int temp = table[j][i];
                    table[j][i] = table[j][i + 1];
                    table[j][i + 1] = temp;
                }
                i -= 2;
                if (i < 0) {
                    i = -1;
                }
            }
        }
        well.add(new ClickomaniaState(this));
    }
    
    @Override
    public String toString() {
        String s = "\ntaboo is:" + taboo + "\n{";
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                s += table[i][j] + ", ";
            }
            s += (i != width - 1 ? "\n " : "");
        }
        return s + "} score: " + score + "\n";
    }
    
}
