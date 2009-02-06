package bravo.game;

// a class that gets human input and interfaces this with the game class

public class HumanPlayer extends Player {

	private boolean inputReady() {
		// TODO SPEC: player has made a turn
		return true;
	}

	public boolean doTurn() {
		int i=0;
		do {
			System.out.print((side?"w":"b")+" not ready: " + i + "\r");
			try {
				Thread.sleep(4000);
				++i;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!inputReady());

		return true;

	}

}
