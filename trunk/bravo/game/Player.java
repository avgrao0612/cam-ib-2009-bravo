package bravo.game;

public abstract class Player {

	protected Board board;
	protected boolean side;

	public Player sit(Board b, boolean s) {
		board = b;
		side = s;
		return this;
	}

	public abstract Turn doTurn();


}
