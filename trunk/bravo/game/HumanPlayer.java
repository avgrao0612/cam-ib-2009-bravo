package bravo.game;

// a class that gets human input and interfaces this with the game class

public class HumanPlayer extends Player {

	private boolean inputReady() {
		return true;
	}

	public Player doTurn() {
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

		// TODO make basic detection of invalid move (one invalid remove, one invalid add)
		return this;

	}


}
