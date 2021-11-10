/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_lightUp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class LightUpState extends State {

    int size;
    int number_stones;
    int number_guides;
    int table[][];
    ArrayList<Point> bulbs;
    Point stones[];
    public boolean failNode;
    Point guides[];
    public ArrayList<LightUpState> well= new ArrayList<>();


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Arrays.deepHashCode(this.table);
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
        final LightUpState other = (LightUpState) obj;
        if (!Arrays.deepEquals(this.table, other.table)) {
            return false;
        }
        return true;
    }
    
    
    
    public LightUpState(int level) {
        long t = System.currentTimeMillis();
        try {
            File file;
            this.depth = 0;
            failNode = false;
            file = new File(Main.LEVEL_PATH + "/level_" + level);
            Scanner sc;
            sc = new Scanner(file);
            size = sc.nextInt();
            number_guides = sc.nextInt();
            guides = new Point[number_guides];
            number_stones = sc.nextInt();
            bulbs = new ArrayList<>();
            stones = new Point[number_stones];
            int l = 0;
            int fer = 0;
            this.table = new int[size][size];
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    this.table[i][j] = sc.nextInt();
                    if (this.table[i][j] == -5) {
                        stones[l++] = new Point(i, j);
                    } else if (this.table[i][j] < 0) {
                        guides[fer++] = new Point(i, j);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LightUpState.class.getName()).log(Level.SEVERE, null, ex);
        }
        long tt = System.currentTimeMillis();
        oneTimePreprocess();
        multiplePreprocess(3);
        System.out.println("asd = " + (System.currentTimeMillis() - tt) / (double) 1000);
        int sum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (table[i][j] == 2 || table[i][j] == 1) {
                    sum++;
                }
            }
        }
        System.out.println("sum = " + sum);
        Main.FILLING_TIME += System.currentTimeMillis() - t;
    }

    public LightUpState(LightUpState s) {
        size = s.size;
        number_guides = s.number_guides;
        number_stones = s.number_stones;
        failNode = s.failNode;
        stones = s.stones.clone();
        guides = s.guides.clone();
        bulbs = new ArrayList<>(s.bulbs);
        isInTree = s.isInTree;
        depth = s.depth;
        parent = s.parent;
        table = new int[size][];
        for (int i = 0; i < size; i++) {
            table[i] = s.table[i].clone();
        }
    }

    public LightUpState(LightUpState s, LightUpAction act) {
        long t = System.currentTimeMillis();
        size = s.size;
        isInTree = false;
        number_guides = s.number_guides;
        number_stones = s.number_stones;
        stones = s.stones.clone();
        guides = s.guides.clone();
        bulbs = new ArrayList<>(s.bulbs);
        bulbs.add(new Point(act.x, act.y));
        if (Main.death) {
            System.out.println("making new state");
            System.out.println("s==this");
        }
        depth = s.depth++;
        failNode = s.failNode;
        parent = s;
        table = new int[size][];
        for (int i = 0; i < size; i++) {
            table[i] = s.table[i].clone();
        }
        table[act.x][act.y] = 2;
        int i, j;
        // right
        i = act.x;
        j = act.y + 1;
        while (true) {
            if (j > size - 1) {
                break;
            }
            if (table[i][j] == 0) {
                table[i][j] = 1;
                j++;
            } else if (table[i][j] == 1) {
                j++;
            } else if (table[i][j] < 0) {
                break;
            }
        }

        // left
        i = act.x;
        j = act.y - 1;
        while (true) {
            if (j < 0) {
                break;
            }
            if (table[i][j] == 0) {
                table[i][j] = 1;
                j--;
            } else if (table[i][j] == 1) {
                j--;
            } else if (table[i][j] < 0) {
                break;
            }
        }

        // down
        i = act.x + 1;
        j = act.y;
        while (true) {
            if (i > size - 1) {
                break;
            }
            if (table[i][j] == 0) {
                table[i][j] = 1;
                i++;
            } else if (table[i][j] == 1) {
                i++;
            } else if (table[i][j] < 0) {
                break;
            }
        }

        // up
        i = act.x - 1;
        j = act.y;
        while (true) {
            if (i < 0) {
                break;
            }
            if (table[i][j] == 0) {
                table[i][j] = 1;
                i--;
            } else if (table[i][j] == 1) {
                i--;
            } else if (table[i][j] < 0) {
                break;
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
        for (int i = 0; i < number_guides; i++) {
            int x, y, sum, space;
            x = guides[i].x;
            y = guides[i].y;
            int sis = Math.abs(table[x][y]) % 6;
            sum = 0;
            space = 0;
            if (y + 1 < size && table[x][y + 1] == 2) {
                sum++;
            }
            if (y + 1 < size && table[x][y + 1] == 0) {
                space++;
            }
            if (x + 1 < size && table[x + 1][y] == 2) {
                sum++;
            }
            if (x + 1 < size && table[x + 1][y] == 0) {
                space++;
            }
            if (y - 1 >= 0 && table[x][y - 1] == 2) {
                sum++;
            }
            if (y - 1 >= 0 && table[x][y - 1] == 0) {
                space++;
            }
            if (x - 1 >= 0 && table[x - 1][y] == 2) {
                sum++;
            }
            if (x - 1 >= 0 && table[x - 1][y] == 0) {
                space++;
            }
            if (sis < sum) {
                return false;
            }
            if (sum < sis && space == 0) {
                return false;
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (table[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Value getValue() {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (table[i][j] == 2 || table[i][j] == 1) {
                    sum++;
                }
            }
        }
        double satisfied=0;
        for(int i = 0;i<number_guides; i++){
            int howmany = 0;
            int x = guides[i].x;
            int y = guides[i].y;
            int sis = Math.abs(table[x][y]) % 6;
            if (y + 1 < size && table[x][y + 1] == 2) {
                howmany++;
            }
            if (x + 1 < size && table[x + 1][y] == 2) {
                howmany++;
            }
            if (y - 1 >= 0 && table[x][y - 1] == 2) {
                howmany++;
            }
            if (x - 1 >= 0 && table[x - 1][y] == 2) {
                howmany++;
            }
            if (sis == howmany) {
                satisfied++;
            }
        }
//        System.out.println("sum = " + sum);
        return new LightUpValue(-1, 0.7*(sum / (double) (size * size - number_guides - number_stones))+0.3*(satisfied/number_guides));
    }

    @Override
    protected ArrayList<State> refreshChilds() {
        ArrayList<State> childss = new ArrayList<>();
        boolean fullfill = true;
        for (int i = 0; i < number_guides; i++) {
            int x, y, sum;
            x = guides[i].x;
            y = guides[i].y;
            if (Main.death) {
                System.out.println("x = " + x);
                System.out.println("y = " + y);
            }
            int sis = Math.abs(table[x][y]) % 6;
            if (Main.death) {
                System.out.println("sis = " + sis);
            }
            sum = 0;
            if (y + 1 < size && table[x][y + 1] == 2) {
                sum++;
            }
            if (x + 1 < size && table[x + 1][y] == 2) {
                sum++;
            }
            if (y - 1 >= 0 && table[x][y - 1] == 2) {
                sum++;
            }
            if (x - 1 >= 0 && table[x - 1][y] == 2) {
                sum++;
            }
            if (Main.death) {
                System.out.println("sum = " + sum);
                System.out.println("===========================");
            }
            if (sis > sum) {
                fullfill = false;
                break;
            }
        }
        if (Main.death) {
            System.out.println("fullfill = " + fullfill);
        }
        if (!fullfill) {
            for (int i = 0; i < number_guides; i++) {
                int x, y, sum;
                x = guides[i].x;
                y = guides[i].y;
                boolean right = false, left = false, up = false, down = false;
                int sis = Math.abs(table[x][y]) % 6;
                sum = 0;
                if (y + 1 < size && table[x][y + 1] == 2) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 2) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 2) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 2) {
                    sum++;
                    up = true;
                }
                if (sis > sum) {
                    if (!up && x - 1 >= 0 && table[x - 1][y] == 0) {
                        childss.add(LightUpSimulator.simulateX(this, new LightUpAction(x - 1, y)));
                    }
                    if (!down && x + 1 < size && table[x + 1][y] == 0) {
                        childss.add(LightUpSimulator.simulateX(this, new LightUpAction(x + 1, y)));
                    }
                    if (!left && y - 1 >= 0 && table[x][y - 1] == 0) {
                        childss.add(LightUpSimulator.simulateX(this, new LightUpAction(x, y - 1)));
                    }
                    if (!right && y + 1 < size && table[x][y + 1] == 0) {
                        childss.add(LightUpSimulator.simulateX(this, new LightUpAction(x, y + 1)));
                    }
                }
            }
        } else {
            boolean fuel = false;
            int toReturn = -1;
            boolean left = false;
            boolean right = false;
            boolean up = false;
            boolean down = false;
            for (int i = 0; i < number_guides; i++) {
                int x, y, sum;
                x = guides[i].x;
                y = guides[i].y;
                if (Main.death) {
                    System.out.println("x = " + x);
                    System.out.println("y = " + y);
                }
                int sis = Math.abs(table[x][y]) % 6;
                if (Main.death) {
                    System.out.println("sis = " + sis);
                }
                sum = 0;
                if (y + 1 < size && table[x][y + 1] == 0) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    sum++;
                    up = true;
                }
                if (Main.death) {
                    System.out.println("sum = " + sum);
                    System.out.println("===========================");
                }
                if (sum > 0) {
                    fuel = true;
                    toReturn = i;
                    break;
                }
            }
            if (!fuel) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                        }
                    }
                }
            } else {
                int x = guides[toReturn].x;
                int y = guides[toReturn].y;
                if (left) {
                    table[x][y - 1] = 1;
                    int i, j;
                    // cant go right we already know what is there
                    // left
                    i = x;
                    j = y - 1;
                    while (true) {
                        if (j < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            j--;
                        } else if (table[i][j] == 1) {
                            j--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // up
                    i = x;
                    j = y - 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            i--;
                        } else if (table[i][j] == 1) {
                            i--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // down
                    i = x;
                    j = y - 1;
                    while (true) {
                        if (i > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            i++;
                        } else if (table[i][j] == 1) {
                            i++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                }
                if (right) {
                    table[x][y + 1] = 1;
                    int i, j;
                    // cant go left we already know what is there
                    // right
                    i = x;
                    j = y + 1;
                    while (true) {
                        if (j > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            j++;
                        } else if (table[i][j] == 1) {
                            j++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // up
                    i = x;
                    j = y + 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            i--;
                        } else if (table[i][j] == 1) {
                            i--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // down
                    i = x;
                    j = y + 1;
                    while (true) {
                        if (i > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            i++;
                        } else if (table[i][j] == 1) {
                            i++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                }
                if (up) {
                    table[x - 1][y] = 1;
                    int i, j;
                    // cant go down we already know what is there
                    // right
                    i = x - 1;
                    j = y;
                    while (true) {
                        if (j > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            j++;
                        } else if (table[i][j] == 1) {
                            j++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // up
                    i = x - 1;
                    j = y;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            i--;
                        } else if (table[i][j] == 1) {
                            i--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // left
                    i = x - 1;
                    j = y;
                    while (true) {
                        if (j < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            j--;
                        } else if (table[i][j] == 1) {
                            j--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                }
                if (down) {
                    table[x + 1][y] = 1;
                    int i, j;
                    // cant go up we already know what is there
                    // right
                    i = x + 1;
                    j = y;
                    while (true) {
                        if (j > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            j++;
                        } else if (table[i][j] == 1) {
                            j++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // down
                    i = x + 1;
                    j = y;
                    while (true) {
                        if (i > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            i++;
                        } else if (table[i][j] == 1) {
                            i++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // left
                    i = x + 1;
                    j = y;
                    while (true) {
                        if (j < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            j--;
                        } else if (table[i][j] == 1) {
                            j--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                }
                if (childss.isEmpty()) {
                    //got it it has no answer but we are forcing to fill the board I know it has no answer :(
//                    System.out.println("wtf");
//                    System.out.println("table = " + this);
//                    System.out.println("left = " + left);
//                    System.out.println("right = " + right);
//                    System.out.println("toReturn = " + toReturn);
//                    System.out.println("up = " + up);
//                    System.out.println("down = " + down);
//                    System.out.println("x = " + x);
//                    System.out.println("y = " + y);
                    if (left) {
                        table[x][y - 1] = 0;
                    }
                    if (right) {
                        table[x][y + 1] = 0;
                    }
                    if (up) {
                        table[x - 1][y] = 0;
                    }
                    if (down) {
                        table[x + 1][y] = 0;
                    }
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            if (table[i][j] == 0) {
                                childss.add(LightUpSimulator.simulateX(this, new LightUpAction(i, j)));
                            }
                        }
                    }
                }
            }
        }
        return childss;
    }

    void rollDown() {
        Random r = new Random();
        ArrayList<Point> childss = new ArrayList<>();
        boolean fullfill = true;
        for (int i = 0; i < number_guides; i++) {
            int x, y, sum;
            x = guides[i].x;
            y = guides[i].y;
            if (Main.death) {
                System.out.println("x = " + x);
                System.out.println("y = " + y);
            }
            int sis = Math.abs(table[x][y]) % 6;
            if (Main.death) {
                System.out.println("sis = " + sis);
            }
            sum = 0;
            if (y + 1 < size && table[x][y + 1] == 2) {
                sum++;
            }
            if (x + 1 < size && table[x + 1][y] == 2) {
                sum++;
            }
            if (y - 1 >= 0 && table[x][y - 1] == 2) {
                sum++;
            }
            if (x - 1 >= 0 && table[x - 1][y] == 2) {
                sum++;
            }
            if (Main.death) {
                System.out.println("sum = " + sum);
                System.out.println("===========================");
            }
            if (sis > sum) {
                fullfill = false;
                break;
            }
        }
        if (Main.death) {
            System.out.println("fullfill = " + fullfill);
        }
        if (!fullfill) {
            for (int i = 0; i < number_guides; i++) {
                int x, y, sum;
                x = guides[i].x;
                y = guides[i].y;
                boolean right = false, left = false, up = false, down = false;
                int sis = Math.abs(table[x][y]) % 6;
                sum = 0;
                if (y + 1 < size && table[x][y + 1] == 2) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 2) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 2) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 2) {
                    sum++;
                    up = true;
                }
                if (sis > sum) {
                    if (!up && x - 1 >= 0 && table[x - 1][y] == 0) {
                        childss.add(new Point(x - 1, y));
                    }
                    if (!down && x + 1 < size && table[x + 1][y] == 0) {
                        childss.add(new Point(x + 1, y));
                    }
                    if (!left && y - 1 >= 0 && table[x][y - 1] == 0) {
                        childss.add(new Point(x, y - 1));
                    }
                    if (!right && y + 1 < size && table[x][y + 1] == 0) {
                        childss.add(new Point(x, y + 1));
                    }
                }
            }
        } else {
            boolean fuel = false;
            int toReturn = -1;
            boolean left = false;
            boolean right = false;
            boolean up = false;
            boolean down = false;
            for (int i = 0; i < number_guides; i++) {
                int x, y, sum;
                x = guides[i].x;
                y = guides[i].y;
                if (Main.death) {
                    System.out.println("x = " + x);
                    System.out.println("y = " + y);
                }
                int sis = Math.abs(table[x][y]) % 6;
                if (Main.death) {
                    System.out.println("sis = " + sis);
                }
                sum = 0;
                if (y + 1 < size && table[x][y + 1] == 0) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    sum++;
                    up = true;
                }
                if (Main.death) {
                    System.out.println("sum = " + sum);
                    System.out.println("===========================");
                }
                if (sum > 0) {
                    fuel = true;
                    toReturn = i;
                    break;
                }
            }
            if (!fuel) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                        }
                    }
                }
            } else {
                int x = guides[toReturn].x;
                int y = guides[toReturn].y;
                if (left) {
                    table[x][y - 1] = 1;
                    int i, j;
                    // cant go right we already know what is there
                    // left
                    i = x;
                    j = y - 1;
                    while (true) {
                        if (j < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            j--;
                        } else if (table[i][j] == 1) {
                            j--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // up
                    i = x;
                    j = y - 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            i--;
                        } else if (table[i][j] == 1) {
                            i--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // down
                    i = x;
                    j = y - 1;
                    while (true) {
                        if (i > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            i++;
                        } else if (table[i][j] == 1) {
                            i++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                }
                if (right) {
                    table[x][y + 1] = 1;
                    int i, j;
                    // cant go left we already know what is there
                    // right
                    i = x;
                    j = y + 1;
                    while (true) {
                        if (j > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            j++;
                        } else if (table[i][j] == 1) {
                            j++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // up
                    i = x;
                    j = y + 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            i--;
                        } else if (table[i][j] == 1) {
                            i--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // down
                    i = x;
                    j = y + 1;
                    while (true) {
                        if (i > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            i++;
                        } else if (table[i][j] == 1) {
                            i++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                }
                if (up) {
                    table[x - 1][y] = 1;
                    int i, j;
                    // cant go down we already know what is there
                    // right
                    i = x - 1;
                    j = y;
                    while (true) {
                        if (j > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            j++;
                        } else if (table[i][j] == 1) {
                            j++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // up
                    i = x - 1;
                    j = y;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            i--;
                        } else if (table[i][j] == 1) {
                            i--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // left
                    i = x - 1;
                    j = y;
                    while (true) {
                        if (j < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            j--;
                        } else if (table[i][j] == 1) {
                            j--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                }
                if (down) {
                    table[x + 1][y] = 1;
                    int i, j;
                    // cant go up we already know what is there
                    // right
                    i = x + 1;
                    j = y;
                    while (true) {
                        if (j > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            j++;
                        } else if (table[i][j] == 1) {
                            j++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // down
                    i = x + 1;
                    j = y;
                    while (true) {
                        if (i > size - 1) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            i++;
                        } else if (table[i][j] == 1) {
                            i++;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                    // left
                    i = x + 1;
                    j = y;
                    while (true) {
                        if (j < 0) {
                            break;
                        }
                        if (table[i][j] == 0) {
                            childss.add(new Point(i, j));
                            j--;
                        } else if (table[i][j] == 1) {
                            j--;
                        } else if (table[i][j] < 0) {
                            break;
                        }
                    }
                }
                if (childss.isEmpty()) {
                    //got it it has no answer but we are forcing to fill the board I know it has no answer :(
//                    System.out.println("wtf");
//                    System.out.println("table = " + this);
//                    System.out.println("left = " + left);
//                    System.out.println("right = " + right);
//                    System.out.println("toReturn = " + toReturn);
//                    System.out.println("up = " + up);
//                    System.out.println("down = " + down);
//                    System.out.println("x = " + x);
//                    System.out.println("y = " + y);
                    if (left) {
                        table[x][y - 1] = 0;
                    }
                    if (right) {
                        table[x][y + 1] = 0;
                    }
                    if (up) {
                        table[x - 1][y] = 0;
                    }
                    if (down) {
                        table[x + 1][y] = 0;
                    }
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            if (table[i][j] == 0) {
                                childss.add(new Point(i, j));
                            }
                        }
                    }

                }
            }
        }
        int v = r.nextInt(childss.size());
        LightUpAction act = new LightUpAction(childss.get(v).x, childss.get(v).y);
        table[act.x][act.y] = 2;
        int i, j;
        // right
        i = act.x;
        j = act.y + 1;
        while (true) {
            if (j > size - 1) {
                break;
            }
            if (table[i][j] == 0) {
                table[i][j] = 1;
                j++;
            } else if (table[i][j] == 1) {
                j++;
            } else if (table[i][j] < 0) {
                break;
            }
        }

        // left
        i = act.x;
        j = act.y - 1;
        while (true) {
            if (j < 0) {
                break;
            }
            if (table[i][j] == 0) {
                table[i][j] = 1;
                j--;
            } else if (table[i][j] == 1) {
                j--;
            } else if (table[i][j] < 0) {
                break;
            }
        }

        // down
        i = act.x + 1;
        j = act.y;
        while (true) {
            if (i > size - 1) {
                break;
            }
            if (table[i][j] == 0) {
                table[i][j] = 1;
                i++;
            } else if (table[i][j] == 1) {
                i++;
            } else if (table[i][j] < 0) {
                break;
            }
        }

        // up
        i = act.x - 1;
        j = act.y;
        while (true) {
            if (i < 0) {
                break;
            }
            if (table[i][j] == 0) {
                table[i][j] = 1;
                i--;
            } else if (table[i][j] == 1) {
                i--;
            } else if (table[i][j] < 0) {
                break;
            }
        }
    }

    @Override
    public String toString() {
        String s = "\n{";
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                s += String.format("%2s", table[i][j] + "") + ", ";
            }
            s += (i != size - 1 ? "\n " : "");
        }
        return s + "}failnode: "+failNode+"\n";
    }

    private void multiplePreprocess(int n) {
        if (n == 0) {
            return;
        }
        for (int l = 0; l < number_guides; l++) {
            int x, y, sum = 0;
            x = guides[l].x;
            y = guides[l].y;
            int numberTwo = 0;
            int sis = Math.abs(table[x][y]) % 6;
            if (sis == 3) {
                boolean up = false, down = false, left = false, right = false;
                if (y + 1 < size && table[x][y + 1] == 0) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    sum++;
                    up = true;
                }

                if (y + 1 < size && table[x][y + 1] == 2) {
                    numberTwo++;
                }
                if (x + 1 < size && table[x + 1][y] == 2) {
                    numberTwo++;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 2) {
                    numberTwo++;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 2) {
                    numberTwo++;
                }
                if (sum == 4) {
                    if (table[x + 1][y + 1] == -1) {
                        LightUpAction act = new LightUpAction(x - 1, y);
                        table[act.x][act.y] = 2;

                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        act = new LightUpAction(x, y - 1);
                        table[act.x][act.y] = 2;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }
                        if (y + 2 < size) {
                            table[x + 1][y + 2] = 1;
                        }
                        if (x + 2 < size) {
                            table[x + 2][y + 1] = 1;
                        }
                    } else if (table[x + 1][y - 1] == -1) {
                        LightUpAction act = new LightUpAction(x - 1, y);
                        table[act.x][act.y] = 2;

                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        act = new LightUpAction(x, y + 1);
                        table[act.x][act.y] = 2;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }
                        if (y - 2 >= 0) {
                            table[x + 1][y - 2] = 1;
                        }
                        if (x + 2 < size) {
                            table[x + 2][y - 1] = 1;
                        }
                    } else if (table[x - 1][y - 1] == -1) {
                        LightUpAction act = new LightUpAction(x + 1, y);
                        table[act.x][act.y] = 2;

                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        act = new LightUpAction(x, y + 1);
                        table[act.x][act.y] = 2;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }
                        if (y - 2 >= 0) {
                            table[x - 1][y - 2] = 1;
                        }
                        if (x - 2 >= 0) {
                            table[x - 2][y - 1] = 1;
                        }
                    } else if (table[x - 1][y + 1] == -1) {
                        LightUpAction act = new LightUpAction(x + 1, y);
                        table[act.x][act.y] = 2;

                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        act = new LightUpAction(x, y - 1);
                        table[act.x][act.y] = 2;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }
                        if (y + 2 < size) {
                            table[x - 1][y + 2] = 1;
                        }
                        if (x - 2 >= 0) {
                            table[x - 2][y + 1] = 1;
                        }
                    }
                }
                if (sum + numberTwo == 3) {
                    if (up) {
                        LightUpAction act = new LightUpAction(x - 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (down) {
                        LightUpAction act = new LightUpAction(x + 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (left) {
                        LightUpAction act = new LightUpAction(x, y - 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (right) {
                        LightUpAction act = new LightUpAction(x, y + 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                }

            }
        }

        for (int l = 0; l < number_guides; l++) {
            int x, y, sum = 0;
            x = guides[l].x;
            y = guides[l].y;
            int numberTwo = 0;
            int sis = Math.abs(table[x][y]) % 6;
            if (sis == 2) {
                boolean up = false, down = false, left = false, right = false;
                if (y + 1 < size && table[x][y + 1] == 0) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    sum++;
                    up = true;
                }
                if (y + 1 < size && table[x][y + 1] == 2) {
                    numberTwo++;
                }
                if (x + 1 < size && table[x + 1][y] == 2) {
                    numberTwo++;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 2) {
                    numberTwo++;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 2) {
                    numberTwo++;
                }
                if (sum + numberTwo == 2) {
                    if (up) {
                        LightUpAction act = new LightUpAction(x - 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (down) {
                        LightUpAction act = new LightUpAction(x + 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (left) {
                        LightUpAction act = new LightUpAction(x, y - 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (right) {
                        LightUpAction act = new LightUpAction(x, y + 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                }

            }
        }

        for (int l = 0; l < number_guides; l++) {
            int x, y, sum = 0;
            x = guides[l].x;
            y = guides[l].y;
            int numberTwo = 0;
            int sis = Math.abs(table[x][y]) % 6;
            if (sis == 1) {
                boolean up = false, down = false, left = false, right = false;
                if (y + 1 < size && table[x][y + 1] == 0) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    sum++;
                    up = true;
                }
                if (y + 1 < size && table[x][y + 1] == 2) {
                    numberTwo++;
                }
                if (x + 1 < size && table[x + 1][y] == 2) {
                    numberTwo++;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 2) {
                    numberTwo++;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 2) {
                    numberTwo++;
                }
                if (sum == 1 && numberTwo == 0) {
                    if (up) {
                        LightUpAction act = new LightUpAction(x - 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (down) {
                        LightUpAction act = new LightUpAction(x + 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (left) {
                        LightUpAction act = new LightUpAction(x, y - 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (right) {
                        LightUpAction act = new LightUpAction(x, y + 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                }

            }
        }

        multiplePreprocess(n - 1);
    }

    private void oneTimePreprocess() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (table[i][j] == 0 && i + 1 < size && table[i + 1][j] < 0 && i - 1 >= 0 && table[i - 1][j] < 0 && j + 1 < size && table[i][j + 1] < 0 && j - 1 >= 0 && table[i][j - 1] < 0) {
                    table[i][j] = 2;
                }
                if (i == 0 && table[i][j] == 0 && table[i + 1][j] < 0 && j + 1 < size && table[i][j + 1] < 0 && j - 1 >= 0 && table[i][j - 1] < 0) {
                    table[i][j] = 2;
                }
                if (i == size - 1 && table[i][j] == 0 && table[i - 1][j] < 0 && j + 1 < size && table[i][j + 1] < 0 && j - 1 >= 0 && table[i][j - 1] < 0) {
                    table[i][j] = 2;
                }
                if (j == 0 && table[i][j] == 0 && table[i][j + 1] < 0 && i + 1 < size && table[i + 1][j] < 0 && i - 1 >= 0 && table[i - 1][j] < 0) {
                    table[i][j] = 2;
                }
                if (j == size - 1 && table[i][j] == 0 && table[i][j - 1] < 0 && i + 1 < size && table[i + 1][j] < 0 && i - 1 >= 0 && table[i - 1][j] < 0) {
                    table[i][j] = 2;
                }
            }
        }
        for (int l = 0; l < number_guides; l++) {
            int x, y, sum = 0;
            x = guides[l].x;
            y = guides[l].y;
            int sis = Math.abs(table[x][y]) % 6;
            if (sis == 4) {
                LightUpAction act = new LightUpAction(x + 1, y);
                //cant be blocked by the nature of problem
                table[act.x][act.y] = 2;
                int i, j;
                // right
                i = act.x;
                j = act.y + 1;
                while (true) {
                    if (j > size - 1) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        j++;
                    } else if (table[i][j] == 1) {
                        j++;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // left
                i = act.x;
                j = act.y - 1;
                while (true) {
                    if (j < 0) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        j--;
                    } else if (table[i][j] == 1) {
                        j--;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // down
                i = act.x + 1;
                j = act.y;
                while (true) {
                    if (i > size - 1) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        i++;
                    } else if (table[i][j] == 1) {
                        i++;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // up
                i = act.x - 1;
                j = act.y;
                while (true) {
                    if (i < 0) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        i--;
                    } else if (table[i][j] == 1) {
                        i--;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                act = new LightUpAction(x, y + 1);
                table[act.x][act.y] = 2;
                // right
                i = act.x;
                j = act.y + 1;
                while (true) {
                    if (j > size - 1) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        j++;
                    } else if (table[i][j] == 1) {
                        j++;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // left
                i = act.x;
                j = act.y - 1;
                while (true) {
                    if (j < 0) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        j--;
                    } else if (table[i][j] == 1) {
                        j--;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // down
                i = act.x + 1;
                j = act.y;
                while (true) {
                    if (i > size - 1) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        i++;
                    } else if (table[i][j] == 1) {
                        i++;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // up
                i = act.x - 1;
                j = act.y;
                while (true) {
                    if (i < 0) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        i--;
                    } else if (table[i][j] == 1) {
                        i--;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                act = new LightUpAction(x, y - 1);
                table[act.x][act.y] = 2;
                // right
                i = act.x;
                j = act.y + 1;
                while (true) {
                    if (j > size - 1) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        j++;
                    } else if (table[i][j] == 1) {
                        j++;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // left
                i = act.x;
                j = act.y - 1;
                while (true) {
                    if (j < 0) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        j--;
                    } else if (table[i][j] == 1) {
                        j--;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // down
                i = act.x + 1;
                j = act.y;
                while (true) {
                    if (i > size - 1) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        i++;
                    } else if (table[i][j] == 1) {
                        i++;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // up
                i = act.x - 1;
                j = act.y;
                while (true) {
                    if (i < 0) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        i--;
                    } else if (table[i][j] == 1) {
                        i--;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                act = new LightUpAction(x - 1, y);
                table[act.x][act.y] = 2;
                // right
                i = act.x;
                j = act.y + 1;
                while (true) {
                    if (j > size - 1) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        j++;
                    } else if (table[i][j] == 1) {
                        j++;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // left
                i = act.x;
                j = act.y - 1;
                while (true) {
                    if (j < 0) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        j--;
                    } else if (table[i][j] == 1) {
                        j--;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // down
                i = act.x + 1;
                j = act.y;
                while (true) {
                    if (i > size - 1) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        i++;
                    } else if (table[i][j] == 1) {
                        i++;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }

                // up
                i = act.x - 1;
                j = act.y;
                while (true) {
                    if (i < 0) {
                        break;
                    }
                    if (table[i][j] == 0) {
                        table[i][j] = 1;
                        i--;
                    } else if (table[i][j] == 1) {
                        i--;
                    } else if (table[i][j] < 0) {
                        break;
                    }
                }
            }
        }

        for (int l = 0; l < number_guides; l++) {
            int x, y, sum = 0;
            x = guides[l].x;
            y = guides[l].y;
            int numberTwo = 0;
            int sis = Math.abs(table[x][y]) % 6;
            if (sis == 3) {
                boolean up = false, down = false, left = false, right = false;
                if (y + 1 < size && table[x][y + 1] == 0) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    sum++;
                    up = true;
                }

                if (y + 1 < size && table[x][y + 1] == 2) {
                    numberTwo++;
                }
                if (x + 1 < size && table[x + 1][y] == 2) {
                    numberTwo++;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 2) {
                    numberTwo++;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 2) {
                    numberTwo++;
                }

                if (sum + numberTwo == 3) {
                    if (up) {
                        LightUpAction act = new LightUpAction(x - 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (down) {
                        LightUpAction act = new LightUpAction(x + 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (left) {
                        LightUpAction act = new LightUpAction(x, y - 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (right) {
                        LightUpAction act = new LightUpAction(x, y + 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                }

            }
        }

        for (int l = 0; l < number_guides; l++) {
            int x, y, sum = 0;
            x = guides[l].x;
            y = guides[l].y;
            int numberTwo = 0;
            int sis = Math.abs(table[x][y]) % 6;
            if (sis == 2) {
                boolean up = false, down = false, left = false, right = false;
                if (y + 1 < size && table[x][y + 1] == 0) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    sum++;
                    up = true;
                }
                if (y + 1 < size && table[x][y + 1] == 2) {
                    numberTwo++;
                }
                if (x + 1 < size && table[x + 1][y] == 2) {
                    numberTwo++;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 2) {
                    numberTwo++;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 2) {
                    numberTwo++;
                }
                if (sum + numberTwo == 2) {
                    if (up) {
                        LightUpAction act = new LightUpAction(x - 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (down) {
                        LightUpAction act = new LightUpAction(x + 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (left) {
                        LightUpAction act = new LightUpAction(x, y - 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (right) {
                        LightUpAction act = new LightUpAction(x, y + 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                }

            }
        }

        for (int l = 0; l < number_guides; l++) {
            int x, y, sum = 0;
            x = guides[l].x;
            y = guides[l].y;
            int numberTwo = 0;
            int sis = Math.abs(table[x][y]) % 6;
            if (sis == 1) {
                boolean up = false, down = false, left = false, right = false;
                if (y + 1 < size && table[x][y + 1] == 0) {
                    sum++;
                    right = true;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    sum++;
                    down = true;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    sum++;
                    left = true;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    sum++;
                    up = true;
                }
                if (y + 1 < size && table[x][y + 1] == 2) {
                    numberTwo++;
                }
                if (x + 1 < size && table[x + 1][y] == 2) {
                    numberTwo++;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 2) {
                    numberTwo++;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 2) {
                    numberTwo++;
                }
                if (sum == 1 && numberTwo == 0) {
                    if (up) {
                        LightUpAction act = new LightUpAction(x - 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (down) {
                        LightUpAction act = new LightUpAction(x + 1, y);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (left) {
                        LightUpAction act = new LightUpAction(x, y - 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                    if (right) {
                        LightUpAction act = new LightUpAction(x, y + 1);
                        table[act.x][act.y] = 2;
                        int i, j;
                        // right
                        i = act.x;
                        j = act.y + 1;
                        while (true) {
                            if (j > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j++;
                            } else if (table[i][j] == 1) {
                                j++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // left
                        i = act.x;
                        j = act.y - 1;
                        while (true) {
                            if (j < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                j--;
                            } else if (table[i][j] == 1) {
                                j--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // down
                        i = act.x + 1;
                        j = act.y;
                        while (true) {
                            if (i > size - 1) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i++;
                            } else if (table[i][j] == 1) {
                                i++;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                        // up
                        i = act.x - 1;
                        j = act.y;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            if (table[i][j] == 0) {
                                table[i][j] = 1;
                                i--;
                            } else if (table[i][j] == 1) {
                                i--;
                            } else if (table[i][j] < 0) {
                                break;
                            }
                        }

                    }
                }

            }
        }
        for (int l = 0; l < number_guides; l++) {
            int x, y;
            x = guides[l].x;
            y = guides[l].y;
            int sis = Math.abs(table[x][y]) % 6;
            if (sis == 0) {
                if (y + 1 < size && table[x][y + 1] == 0) {
                    this.table[x][y + 1] = 1;
                }
                if (x + 1 < size && table[x + 1][y] == 0) {
                    this.table[x + 1][y] = 1;
                }
                if (y - 1 >= 0 && table[x][y - 1] == 0) {
                    this.table[x][y - 1] = 1;
                }
                if (x - 1 >= 0 && table[x - 1][y] == 0) {
                    this.table[x - 1][y] = 1;
                }
            }
        }

    }

}
