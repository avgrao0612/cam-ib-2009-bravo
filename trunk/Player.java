package bravo.game;

public abstract class Player {

	protected Draughts game;
	protected boolean side;
	// TODO: code resigning

	public Player sit(Draughts g, boolean s) {
		game = g;
		side = s;
		return this;
	}

	public abstract Player doTurn();


}
