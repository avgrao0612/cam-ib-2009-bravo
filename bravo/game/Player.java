package bravo.game;

import bravo.game.Draughts.*;

public abstract class Player {

	protected Draughts game;
	protected boolean side;

	public Player sit(Draughts g, boolean s) {
		game = g;
		side = s;
		return this;
	}

	public abstract EndTurn doTurn(GameState s);

}
