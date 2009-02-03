package bravo.game;

import java.util.Random;

// a class that gets human input and interfaces this with the game class

public class HumanPlayer extends Player {

	Random r = new Random();

	private boolean inputReady() {
		//return true; //(r.nextInt(4) == 0);
		return false;
	}

	public Turn doTurn() {

		while(!inputReady()) {
			System.out.println((side?"white":"black")+" is not ready...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

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
		for (Turn t : board.getValidTurns()) { k = t; break; }

		skel[k.src] = !skel[k.src];
		skel[k.dst] = !skel[k.dst];

		return skel;
	}


}
