package main;

import java.util.Scanner;

import spf.*;

public class Main {
	public static boolean sysIn;
	public static boolean gcIsOn;
	public static boolean debug;

	public static void main(String[] args) {
		// Game game = new XO_Game();
		// Simulator simulator = new XO_Simulator();
		// Scanner sc = new Scanner(System.in);
		sysIn = false;
		gcIsOn = false;
		debug = false;
		Game game = new SPF_Game();
		Simulator simulator = new SPF_Simulator();
		TreeSolver mcts = new MonteCarloTreeSearch(game, simulator);
		for (int i = 1; i <= 6; ++i) {
			Value.modelNumber = i;
			run(game, simulator, mcts);
		}
	}

	private static void run(Game game, Simulator simulator, TreeSolver treeSolver) {
		game.init();
		long startTimes = System.currentTimeMillis();
		while (game.notEnded()) {
			State state = game.getState();
			if (Main.debug)
				System.out.println("gameState: \n" + state);
			long startTime = System.currentTimeMillis();
			State nextState = treeSolver.getBestNextState(state);
			if (Main.debug)
				System.out.println(System.currentTimeMillis() - startTime);
			game.updateState(nextState);
		}
		System.out.println("FinalState: \n" + game.getState());
		System.out.println("ModelNUmber: " + Value.modelNumber + " Time: " + (System.currentTimeMillis() - startTimes)
				+ " Ratio: " + game.getState().value);
	}
}
