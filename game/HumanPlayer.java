package bravo.game;

import java.util.Random;

// a class that gets human input and interfaces this with the game class

public class HumanPlayer extends Player {

	public HumanPlayer() { }

	public boolean doTurn() {
		int i=0;

		try {
			byte[] in = new byte[8192];
			System.err.print("enter the move, or nothing for random: ");
			int s = System.in.read(in);
			int srcy = Byte.parseByte(new String(in, 0, 1), 16);
			int srcx = Byte.parseByte(new String(in, 1, 1), 16);
			int dsty = Byte.parseByte(new String(in, 3, 1), 16);
			int dstx = Byte.parseByte(new String(in, 4, 1), 16);
			game.board.setStateSkel((byte)(srcy<<4|srcx), (byte)(dsty<<4|dstx));
		} catch (java.io.IOException e) {
			System.err.println("IO Error");
			try {
				Thread.sleep(4000);
			} catch (InterruptedException f) {
				f.printStackTrace();
			}
		} catch (NumberFormatException e) {
			doRandomTurn();
		}

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
