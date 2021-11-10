/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_sokoban;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import main.Main;
import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class SokobanState extends State {

//    public static HashSet<SokobanState> set = new HashSet<>();
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Arrays.deepHashCode(this.table);
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
        final SokobanState other = (SokobanState) obj;
        if (!Arrays.deepEquals(this.table, other.table)) {
            return false;
        }
        return true;
    }

    public boolean hashed;
    public int hash;
    public int size;
    public int goalSize;

    public int[] targetX;
    public int[] targetY;

    public int[] boxX;
    public int[] boxY;

    public int[][] table;

    public int meX;
    public int meY;

    public SokobanState(SokobanState st, SokobanAction act) {
        this.size = st.size;
        this.depth = st.depth++;
        this.goalSize = st.goalSize;
        this.targetX = st.targetX.clone();
        this.targetY = st.targetY.clone();
        this.boxX = st.boxX.clone();
        this.boxY = st.boxY.clone();
        this.table = new int[size][];
        for (int i = 0; i < size; i++) {
            this.table[i] = st.table[i].clone();
        }
        this.meX = st.meX + act.x;
        this.meY = st.meY + act.y;
        if (this.table[this.meX][this.meY] == 0) {
            this.table[st.meX][st.meY] = 0;
            this.table[this.meX][this.meY] = 5;
        } else if (this.table[this.meX][this.meY] == 3) {
            this.table[st.meX][st.meY] = 0;
            this.table[this.meX][this.meY] = 5;
            this.table[this.meX + act.x][this.meY + act.y] = 3;
            for (int i = 1; i <= this.goalSize; i++) {
                if (this.boxX[i] == this.meX && this.boxY[i] == this.meY) {
                    this.boxX[i] = this.meX + act.x;
                    this.boxY[i] = this.meY + act.y;
                    break;
                }
            }
        }
        this.isInTree = false;

//        System.out.println(this);
//        if (!set.contains(this)) {
//            this.isInTree = false;
//            set.add(this);
//        } else {
//            this.isInTree = true;
//        }
    }

    public int[] getBoxX() {
        return boxX;
    }

    public int[] getBoxY() {
        return boxY;
    }

    public int getMeX() {
        return meX;
    }

    public int getMeY() {
        return meY;
    }

    public State getParent() {
        return parent;
    }

    public int[] getTargetX() {
        return targetX;
    }

    public int[] getTargetY() {
        return targetY;
    }

    public int getGoalSize() {
        return goalSize;
    }

    public SokobanState(int level) {
        File file;
        file = new File(Main.LEVEL_PATH + "/level_" + level);
        try {
            Scanner sc;
            sc = new Scanner(file);
            //what if injori bebinam ke jabe ha khodeshon harkat mikonan faghat bayad rastesh baz bashe age mikhad bere chap va multi agent!!!! na nemishe unless...!
            size = sc.nextInt();
            goalSize = sc.nextInt();
            this.targetY = new int[goalSize + 1];
            this.targetX = new int[goalSize + 1];
            for (int i = 1; i <= goalSize; i++) {
                targetX[i] = sc.nextInt();
                targetY[i] = sc.nextInt();
            }
            table = new int[size][size];

            this.boxY = new int[goalSize + 1];
            this.boxX = new int[goalSize + 1];

            int m = 0;

            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    table[i][j] = sc.nextInt();
                    switch (table[i][j]) {
                        case 5:
                            this.meX = i;
                            this.meY = j;
                            break;
                        case 3:
                            this.boxX[++m] = i;
                            this.boxY[m] = j;
                            break;
                        default:
                            break;
                    }
                }
            }
            parent = null;
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        set.add(this);
    }

    public SokobanState(SokobanState st) {
        this.size = st.size;
        this.goalSize = st.goalSize;
        this.targetX = st.targetX.clone();
        this.targetY = st.targetY.clone();
        this.boxX = st.boxX.clone();
        this.boxY = st.boxY.clone();
        this.table = new int[size][];
        for (int i = 0; i < size; i++) {
            this.table[i] = st.table[i].clone();
        }
        this.meX = st.meX;
        this.meY = st.meY;
    }

    @Override
    public boolean isNotTerminal() {
        int res = 0;
        for (int i = 1; i <= goalSize; ++i) {
            res += isNear(i) ? 1 : 0;
        }
        if (res == goalSize) {
            return false;
        }
        if (!hasChild()) {
            return false;
        }

        return true;
    }

    public boolean hasChild() {
        int flag = 0;
        for (int k = 1; k <= goalSize; k++) {
            if (targetX[k] == boxX[k] && targetY[k] == boxY[k]) {
                flag++;
            }
            // I have doubts about this child
            int sag = 0;
            for (int i = -1; i < 2; ++i) {
                for (int j = (i == 0 ? -1 : 0); j < (i == 0 ? 2 : 1); ++j) {
                    if (table[boxX[k] + i][boxY[k] + j] == 0 || table[boxX[k] + i][boxY[k] + j] == 5) {
                        sag++;
                    }
                    if (table[boxX[k] + i][boxY[k] + j] == 3) {
                        int sas = 0;
                        for (int f = -1; f < 2; ++f) {
                            for (int s = (f == 0 ? -1 : 0); s < (f == 0 ? 2 : 1); ++s) {
                                if (-i != f && -j != s) {
                                    if (table[boxX[k] + i + f][boxY[k] + j + s] == 0 || table[boxX[k] + i + f][boxY[k] + j + s] == 5) {
                                        sas++;
                                    }
                                }
                            }
                        }

                        if (sas > 2) {
                            sag++;
                        }
                        if (sas == 2) {
                            if ((table[boxX[k] + i + 1][boxY[k] + j] == 0 && table[boxX[k] + i - 1][boxY[k] + j] == 5) || (table[boxX[k] + i + 1][boxY[k] + j] == 5 && table[boxX[k] + i - 1][boxY[k] + j] == 0)
                                    || (table[boxX[k] + i + 1][boxY[k] + j] == 0 && table[boxX[k] + i - 1][boxY[k] + j] == 0) || (table[boxX[k] + i + 1][boxY[k] + j] == 0 && table[boxX[k] + i - 1][boxY[k] + j] == 0)) {
                                sag++;
                            }
                            if ((table[boxX[k] + i][boxY[k] + j + 1] == 0 && table[boxX[k] + i][boxY[k] + j - 1] == 5) || (table[boxX[k] + i][boxY[k] + j + 1] == 5 && table[boxX[k] + i][boxY[k] + j - 1] == 0)
                                    || (table[boxX[k] + i][boxY[k] + j + 1] == 0 && table[boxX[k] + i][boxY[k] + j - 1] == 0) || (table[boxX[k] + i][boxY[k] + j + 1] == 0 && table[boxX[k] + i][boxY[k] + j - 1] == 0)) {
                                sag++;
                            }
                        }
                        if (Main.death) {
                            System.out.println("sas = " + sas);
                        }
                    }
                }
            }
            if (Main.death) {
                System.out.println("k = " + k);
                System.out.println("bot K x: " + this.boxX[k]);
                System.out.println("bot K y: " + this.boxY[k]);
                System.out.println("sag = " + sag);
            }
            if (sag < 2) {
                boolean fl = false;
                if (Main.death) {
                    System.out.println("///////////////");
                }
                for (int o = 1; o <= this.goalSize; o++) {
                    if (Main.death) {
                        System.out.println("this.targetX[o] = " + this.targetX[o]);
                        System.out.println("this.targetY[o] = " + this.targetY[o]);
                    }
                    if (this.targetX[o] == this.boxX[k] && this.targetY[o] == this.boxY[k]) {
                        fl = true;
                        break;
                    }
                }
                if (fl) {
                    if (Main.death) {
                        System.out.println("hmmmm are");
                    }
                    continue;
                }
                return false;
            }
            if (sag == 2) {
                if ((table[boxX[k] + 1][boxY[k]] == 0 && table[boxX[k] - 1][boxY[k]] == 5) || (table[boxX[k] + 1][boxY[k]] == 5 && table[boxX[k] - 1][boxY[k]] == 0)
                        || (table[boxX[k] + 1][boxY[k]] == 0 && table[boxX[k] - 1][boxY[k]] == 0) || (table[boxX[k] + 1][boxY[k]] == 0 && table[boxX[k] - 1][boxY[k]] == 0)) {
                    if (Main.death) {
                        System.out.println("DAFAQ");
                    }
                    continue;
                }
                if ((table[boxX[k]][boxY[k] + 1] == 0 && table[boxX[k]][boxY[k] - 1] == 5) || (table[boxX[k]][boxY[k] + 1] == 5 && table[boxX[k]][boxY[k] - 1] == 0)
                        || (table[boxX[k]][boxY[k] + 1] == 0 && table[boxX[k]][boxY[k] - 1] == 0) || (table[boxX[k]][boxY[k] + 1] == 0 && table[boxX[k]][boxY[k] - 1] == 0)) {
                    if (Main.death) {
                        System.out.println("QAFAD");
                    }
                    continue;
                }
                boolean fl = false;
                if (Main.death) {
                    System.out.println("///////////////");
                }
                for (int o = 1; o <= this.goalSize; o++) {
                    if (Main.death) {
                        System.out.println("this.targetX[o] = " + this.targetX[o]);
                        System.out.println("this.targetY[o] = " + this.targetY[o]);
                    }
                    if (this.targetX[o] == this.boxX[k] && this.targetY[o] == this.boxY[k]) {
                        fl = true;
                        break;
                    }
                }
                if (fl) {
                    if (Main.death) {
                        System.out.println("hmmmm are");
                    }
                    continue;
                }
                return false;
            }
        }
        if (flag == goalSize) {
            return false;
        }
        for (int i = -1; i < 2; ++i) {
            for (int j = (i == 0 ? -1 : 0); j < (i == 0 ? 2 : 1); ++j) {
                if ((table[meX + i][meY + j] == 0
                        || (table[meX + i][meY + j] == 3
                        && table[meX + i + i][meY + j + j] == 0))) {
                    return true;
                }
            }
        }
        return false;
    }

    private int childNumber() {
        int ans = 0;
        for (int i = -1; i < 2; ++i) {
            for (int j = (i == 0 ? -1 : 0); j < (i == 0 ? 2 : 1); ++j) {
                if ((table[meX + i][meY + j] == 0
                        || (table[meX + i][meY + j] == 3
                        && table[meX + i + i][meY + j + j] == 0))) {
                    ans++;
                }
            }
        }
        return ans;
    }

    private boolean isNear(int index) {
        return (boxX[index] == targetX[index]) && (boxY[index] == targetY[index]);
    }

    @Override
    public Value getValue() {
        if (isNotTerminal()) {
            return null;
        }
        double res = 0;
        boolean m[] = new boolean[goalSize + 1];
        for (int i = 1; i <= goalSize; ++i) {
            res += isNear(i) ? 1 : 0;
            // res -= isNear(i) ? (double) depth / (2 * size * size) : 0;
            m[i] = isNear(i);
        }

        return new SokobanValue(-1, res / (double) goalSize, m);
    }

    @Override
    protected ArrayList<State> refreshChilds() {
        ArrayList<State> childss = new ArrayList<State>();
        for (int i = -1; i < 2; ++i) {
            for (int j = (i == 0 ? -1 : 0); j < (i == 0 ? 2 : 1); ++j) {
                if ((table[meX + i][meY + j] == 0
                        || (table[meX + i][meY + j] == 3
                        && (table[meX + i + i][meY + j + j] == 0 || table[meX + i + i][meY + j + j] == 1)))) {
                    childss.add(SokobanSimulator.simulateX(this, new SokobanAction(i, j)));
                }
            }
        }
        return childss;
    }

    @Override
    public String toString() {
        String s = "\n{";
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                s += table[i][j] + ", ";
            }
            s += (i != size - 1 ? "\n " : "");
        }
        return s + "}\n";
    }

    public void rollDown() {

    }
}
