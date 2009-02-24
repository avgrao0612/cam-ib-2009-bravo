package bravo.game;

import bravo.game.Draughts.*;
import bravo.game.Board.*;

// a class that gets human input and interfaces this with the game class

public class HumanPlayer extends Player {

	public HumanPlayer() { }

	public EndTurn doTurn(GameState state, BoardState bstate) {
		return game.hwi.proceed(bstate);
	}

}
