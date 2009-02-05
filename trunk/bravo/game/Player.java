package bravo.game;

public abstract class Player {

	protected Draughts game;
	protected boolean side;

	public Player sit(Draughts g, boolean s) {
		game = g;
		side = s;
		return this;
	}

	// if the player resigns, this returns false
	public abstract boolean doTurn();

}
