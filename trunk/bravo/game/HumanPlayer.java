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
			System.err.print((side?"w":"b")+" not ready: " + i + "\r");
			try {
				Thread.sleep((int)(pollint * 1000));
				++i;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!inputReady());

		getTurn();
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

	private void getTurn() {
		try {
			byte[] in = new byte[8192];
			System.err.print("enter the move: ");
			int s = System.in.read(in);
			int srcy = Byte.parseByte(new String(in, 0, 1), 16);
			int srcx = Byte.parseByte(new String(in, 1, 1), 16);
			int dsty = Byte.parseByte(new String(in, 3, 1), 16);
			int dstx = Byte.parseByte(new String(in, 4, 1), 16);
			game.board.setStateSkel((byte)(srcy<<4|srcx), (byte)(dsty<<4|dstx));
		} catch (java.io.IOException e) {
			System.out.println("IO Error");
			try {
				Thread.sleep(4000);
			} catch (InterruptedException f) {
				f.printStackTrace();
			}
		} catch (NumberFormatException e) {
			System.err.println("Random");
			doRandomTurn();
		}
	}


}
