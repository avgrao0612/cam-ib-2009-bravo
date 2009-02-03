package bravo.game;

import java.util.Random;

// a class that gets human input and interfaces this with the game class

public class HumanPlayer extends Player {

	Random r = new Random();

	private boolean inputReady() {
		return true;// (r.nextInt(2) == 0);
		//return false;
	}

	public Turn doTurn() {
		int i=0;
		do {
			System.out.print((side?"white":"black")+" is not ready..." + i + "\r");
			try {
				Thread.sleep(500);
				++i;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!inputReady());

		return board.getTurnFromSkel(getStateSkel());

	}

	private boolean[] getStateSkel() {
		// TODO: get actual user input
		return getRandomTurn();
	}

	private boolean[] getRandomTurn() {
		// testing code ONLY
		boolean[] skel = new boolean[256];

		for (Piece p : board.board) {
			if (p!=null) { skel[p.pos] = true; }
		}

		// pick a random turn
		Turn k = null;
		int s = r.nextInt(board.getValidTurns().size());
		int i = 0;
		for (Turn t : board.getValidTurns()) {
			if (i++ == s) {
				k = t; break;
			}
		}
		System.out.println(k);

		skel[k.src] = !skel[k.src];
		skel[k.dst] = !skel[k.dst];

		return skel;
	}


}
