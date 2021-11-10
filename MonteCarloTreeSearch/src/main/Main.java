package main;

import game_clickomania.ClickomaniaAction;
import game_clickomania.ClickomaniaGame;
import game_clickomania.ClickomaniaSimulator;
import game_clickomania.ClickomaniaState;
import game_clickomania.MonteCarloTreeSearchClickomania;
import game_colorFill.MonteCarloTreeSearchCollorFill;
import game_colorFill.CollorFillAction;
import game_colorFill.CollorFillGame;
import game_colorFill.CollorFillSimulator;
import game_colorFill.CollorFillState;
import game_lightUp.LightUpGame;
import game_lightUp.LightUpSimulator;
import game_lightUp.LightUpState;
import game_lightUp.MonteCarloTreeSearchLightUp;
import game_npuzzle.MonteCarloTreeSearchNPuzzle;
import game_npuzzle.NPuzzleGame;
import game_npuzzle.NPuzzleSimulator;
import game_sokoban.MonteCarloTreeSearchSokoban;
import game_sokoban.SokobanGame;
import game_sokoban.SokobanSimulator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Hamed Khashehchi
 */
public class Main {

    public static String configuration_file = "src/input/configuration/configuration";
    public static final int MODEL_NUMBER = 6;
    public static String game;
    public static boolean fastRollOut;
    public static boolean model[] = new boolean[MODEL_NUMBER];
    public static int levels;
    public static int level;
    public static String LEVEL_PATH = "src/input/testcase/";
    public static boolean path;
    public static boolean death = false;
    public static int time;
    public static long FILLING_TIME = 0;
    public static boolean BackTrack;

    private static void run(Game game, Simulator simulator, TreeSolver treeSolver) {
        FILLING_TIME = 0;
        game.init();
        long startTimes = System.currentTimeMillis();
        if (!path) {
            System.out.println("game = " + game.getState());
        }
        while (game.notEnded()) {
            if (death) {
                System.out.println("choosing best state to play from:");
            }
            State state = game.getState();
            if (death) {
                System.out.println("state = " + state);
            }
            State nextState = treeSolver.getBestNextState(state);
            if (death) {
                System.out.println("nextState = " + nextState);
            }
//            a.setMove(a.getMove()+1);
            if (path) {
                System.out.println("state:\n" + state + "value:" + state.value);
            }
            game.updateState(nextState);
        }
        System.out.println("FinalState: \n" + game.getState());
        System.out.println("ModelNUmber: " + Value.modelNumber + " Time: " + (System.currentTimeMillis() - startTimes) / (double) 1_000
                + " Ratio: " + game.getState().getValue() + " Depth: " + game.getState().getDepth());
        System.out.println("FILLING_TIME = " + FILLING_TIME / (double) 1_000);
    }

    public static void main(String[] args) {
        configure();
        if (Main.levels != 0) {
            Main.level = Main.levels;
            Game game;
            Simulator simulator;
            TreeSolver mcts;
            if (Main.game.equals("collorfill")) {
                game = new CollorFillGame();
                simulator = new CollorFillSimulator();
                for (int i = 1; i <= 6; ++i) {
                    mcts = new MonteCarloTreeSearchCollorFill(game, simulator);
                    Value.modelNumber = i;
                    if (model[i - 1]) {
                        run(game, simulator, mcts);
                    }
                    System.out.println("============================\n");
                }
            } else if (Main.game.equals("clickomania")) {
                game = new ClickomaniaGame();
                simulator = new ClickomaniaSimulator();
                for (int i = 1; i <= 6; ++i) {
                    mcts = new MonteCarloTreeSearchClickomania(game, simulator);
                    Value.modelNumber = i;
                    if (model[i - 1]) {
                        run(game, simulator, mcts);
                    }
                    System.out.println("============================\n");
                }
            } else if (Main.game.equals("npuzzle")) {
                game = new NPuzzleGame();
                simulator = new NPuzzleSimulator();
                for (int i = 1; i <= 6; ++i) {
                    mcts = new MonteCarloTreeSearchNPuzzle(game, simulator);
                    Value.modelNumber = i;
                    if (model[i - 1]) {
                        run(game, simulator, mcts);
                    }
                    System.out.println("============================\n");
                }
            } else if (Main.game.equals("lightup")) {
                game = new LightUpGame();
                simulator = new LightUpSimulator();
                for (int i = 1; i <= 6; ++i) {
                    mcts = new MonteCarloTreeSearchLightUp(game, simulator);
                    Value.modelNumber = i;
                    if (model[i - 1]) {
                        run(game, simulator, mcts);
                    }
                    System.out.println("============================\n");
                }
            } else if (Main.game.equals("lightupchange")) {
                game = new LightUpGame();
                simulator = new LightUpSimulator();
                for (int i = 1; i <= 6; ++i) {
                    mcts = new MonteCarloTreeSearchLightUp(game, simulator);
                    Value.modelNumber = i;
                    if (model[i - 1]) {
                        runChange(game, mcts);
                    }
                    System.out.println("============================\n");
                }
            } else if (Main.game.equals("clickomaniachange")) {
                ClickomaniaState.clickomania = true;
                ClickomaniaState.taboo = -1;
                game = new ClickomaniaGame();
                simulator = new ClickomaniaSimulator();
                for (int i = 1; i <= 6; ++i) {
                    mcts = new MonteCarloTreeSearchClickomania(game, simulator);
                    Value.modelNumber = i;
                    if (model[i - 1]) {
                        runChangeWOW(game, mcts);
                    }
                    System.out.println("============================\n");
                }
            } else if (Main.game.equals("sokoban")) {
                game = new SokobanGame();
                simulator = new SokobanSimulator();
                for (int i = 1; i <= 6; ++i) {
                    mcts = new MonteCarloTreeSearchSokoban(game, simulator);
                    Value.modelNumber = i;
                    if (model[i - 1]) {
                        run(game, simulator, mcts);
                    }
                    System.out.println("============================\n");
                }
            } else {
                System.out.println("you dumb! first introduce the puzzle!");
            }
        }
    }

    private static void configure() {
        try {
            File file;
            file = new File(configuration_file);
            Scanner sc;
            sc = new Scanner(file);
            Main.game = sc.next();
            if (Main.game.equals("samegame")) {
                Main.game = "clickomania";
                ClickomaniaState.clickomania = false;
                ClickomaniaState.taboo = 0;
            } else if (Main.game.equals("clickomania")) {
                ClickomaniaState.clickomania = true;
                ClickomaniaState.taboo = -1;
            }
            Main.LEVEL_PATH += Main.game;
            Main.BackTrack = sc.nextBoolean();
            Main.time = sc.nextInt();
            Main.path = sc.nextBoolean();
            Main.fastRollOut = sc.nextBoolean();
            for (int i = 0; i < MODEL_NUMBER; i++) {
                Main.model[i] = sc.nextBoolean();
            }
            Main.levels = sc.nextInt();
            // 0 means all levels
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void runChange(Game game, TreeSolver treeSolver) {
        FILLING_TIME = 0;
        game.init();
        long startTimes = System.currentTimeMillis();
        if (!path) {
            System.out.println("game = " + game.getState());
        }
        boolean first = true;
        State track = null;
        boolean par = false;
        ArrayList<State> ffs = new ArrayList<>();
        while (first || Main.BackTrack) {
            if (!first && par) {
                if (ffs.isEmpty()) {
                    par = false;
                    System.out.println("dafaq");
                } else {
                    game.myState = ffs.get(ffs.size() - 1);
                    ffs.remove(ffs.size() - 1);
                }
            }
            while (game.notEnded()) {
                State nextState = ((MonteCarloTreeSearchLightUp) treeSolver).getBestNextState(game.myState, first);
                if (path) {
                    System.out.println("state:\n" + game.myState + "value:" + game.myState.value);
                }
                game.updateState(nextState);
            }
            if (first) {
                track = game.myState;
            }
            first = false;
            System.out.println("FinalState: \n" + game.getState() + "value:" + game.myState.value);
            if (game.getState().getValue().value == 1) {
                System.out.println("Solved!");
                break;
            } else {
                System.out.println("fuck didn't find it. let's back up");
                if (track.parent != null) {
                    if (!par) {
                        ArrayList<State> childs = track.parent.childs;
//                        for(int i = 0; i<childs.size();i++){
//                            childs.get(i).childs=null;
//                            childs.get(i).value=null;
//                        }
                        childs.remove(track);
                        game.myState = track.parent;
                        track = track.parent;
                        if (track.parent == null) {
                            par = true;
                            ffs.addAll(childs);
                            System.out.println("par par");
                        }
                    }
                } else {
                    if (!par) {
                        game.myState = track;
                        first = true;
                        game.myState.reset();
                        track = null;
                        System.out.println("az aval");
                    }
                }
//                System.out.println("game.myState.childs.size() = " + game.myState.childs.size());
                // ye moteghaier e sevom begir ke bache ha berizi tosh ro ona mizani
                // baad ro in miri bala :D
                // I think it works
//                System.out.println("fuck didn't find it. let's get back up");
//                System.out.println("game.myState = " + game.myState);
//                while (game.myState != null) {
//                    game.myState = game.myState.parent;
//                    System.out.println("game.myState = " + game.myState);
//                }
//                ArrayList<State> flow = ((LightUpGame) game).flow;
//                game.myState = flow.get(flow.size() - 2);
//                ArrayList<State> childs = game.myState.getChilds();
//                for (int i = 0; i < childs.size(); i++) {
//                    if (childs.get(i).equals(flow.get(flow.size() - 1))) {
//                        ((LightUpState) childs.get(i)).failNode = true;
//                    }
//                }
//                flow.remove(flow.size() - 1);
            }
        }
        System.out.println("ModelNUmber: " + Value.modelNumber + " Time: " + (System.currentTimeMillis() - startTimes) / (double) 1_000
                + " Ratio: " + game.getState().getValue() + " Depth: " + game.getState().getDepth());
        System.out.println("FILLING_TIME = " + FILLING_TIME / (double) 1_000);
    }

    private static void runChangeWOW(Game game, TreeSolver treeSolver) {
        FILLING_TIME = 0;
        game.init();
        long startTimes = System.currentTimeMillis();
        if (!path) {
            System.out.println("game = " + game.getState());
        }
        int track = -1;
        int tes = -1;
        boolean first = true;
        while (first || Main.BackTrack) {
            System.out.println("track = " + track);
            first = false;
            while (game.notEnded()) {
                State nextState = treeSolver.getBestNextState(game.myState);
                if (path) {
                    System.out.println("state:\n" + game.myState + "value:" + game.myState.value);
                }
                game.updateState(nextState);

            }
            System.out.println("FinalState: \n" + game.getState());
            if (game.getState().getValue().value == 1) {
                System.out.println("Solved!");
                break;
            } else {
                System.out.println("didn't solve!\n let's go up");
//                System.out.println(((LightUpGame) game).flow);
                ArrayList<State> flow = ((ClickomaniaGame) game).flow;
                if (track == -1) {
                    track = flow.size() - 1;
                    flow.remove(track);
                    game.myState = flow.get(track - 1);
                } else {
                    track--;
                    System.out.println("flow.size() = " + flow.size());
                    for (int i = flow.size() - 1; track > 0 && i >= track; i--) {
                        flow.remove(i);
                    }
                    try {
                        game.myState = flow.get(track - 1);
                    } catch (Exception e) {
                        System.out.println("here");
                        track = flow.size() - 1;
                        flow.remove(track);
                        game.myState = flow.get(track - 1);
                    }
                }
                System.out.println("decided on:");
                System.out.println("state: " + game.myState);
            }
        }
        System.out.println("ModelNUmber: " + Value.modelNumber + " Time: " + (System.currentTimeMillis() - startTimes) / (double) 1_000
                + " Ratio: " + game.getState().getValue() + " Depth: " + game.getState().getDepth());
        System.out.println("FILLING_TIME = " + FILLING_TIME / (double) 1_000);
    }

}
