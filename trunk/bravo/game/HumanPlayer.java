package bravo.game;

import java.util.Random;

// a class that gets human input and interfaces this with the game class

public class HumanPlayer extends Player {

	final double pollint;

	public HumanPlayer(double p) {
		pollint = p; // TODO: debug only, remove later
	}

	private boolean inputReady() {
		// TODO SPEC: player has made a turn
		return true;
	}

	public boolean doTurn() {
		int i=0;
		do {
			System.out.print((side?"w":"b")+" not ready: " + i + "\r");
			try {
				Thread.sleep((int)(pollint * 1000));
				++i;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!inputReady());

		doRandomTurn();
		return true;

	}

	Random rdx = new Random();
	private void doRandomTurn() {
		// pick a random turn
		Turn k = null;
		int s = rdx.nextInt(game.board.getValidTurns().size());
		int i = 0;
		for (Turn t : game.board.getValidTurns()) {
			if (i++ == s) {
				k = t; break;
			}
		}
		game.board.setStateSkel(k.src, k.dst);
	}


}
