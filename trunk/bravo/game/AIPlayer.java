// Contains the AI engine for playing draughts.
// Should abide by all the rules of Draughts given in the requirements specification.
// Arguments that could be passed in nextTurn are instead given in constructor and should be stored.
// This makes an instance of AI behave like a 'virtual person' who has certain characteristics and is playing one side on one board.
// Timer value gives maximum time that AI can operate for.  Can act as a pseudo difficulty setting by giving the AI less time to think to make the game easier, and to enforce the specification.
//
// Depends on Board, Move, Piece
// Depended on by Draughts

package bravo.game;

import bravo.game.Draughts.*;
import bravo.game.Board.*;

public class AIPlayer extends Player{

	public AIPlayer(int t) {
		tough = t;
	}

	protected int tough=5;

	protected double scoreForTree(Board b, int depth, String pre){
		if (depth == 0) { return b.piecesRatio(); }
		double lowest = 2.0; // if no moves valid (ie. lost), will return score as -1.0

		for (Turn t: b.getValidTurns())
		{
			double s = scoreForTree(b.nextState(t), depth-1, pre + " |");
			// if (turns > 8 && depth > 3) { System.err.println(pre + "-" + t + " " + s); }
			if (s<lowest) { lowest = s; }
			if (lowest < -Double.MIN_VALUE) { break; } // optimisation
			// must NOT break when == 0, since calculation of deeper trees may reveal preceding trees with score=0
			// which may result in an infinite loop, or a slower winning move
		}

		//System.err.print(depth + " " + k + ", score: " + (1.0-lowest) + "\r");
		return 1.0 - lowest;

	}

	java.util.Random rdx = new java.util.Random();
	protected Turn bestTree(Board b, int depth){
		//Board highestBoard = null;
		//if (tough==0){
			//doRandomTurn();
		//return null;
		//}
		//else{
		Turn turn = null;
		double lowest = 2.0;
		int c = 0;

		// optimise
		if (b.getValidTurns().size() == 1) { for (Turn t: b.getValidTurns()) { return t; } }

		for (Turn t: b.getValidTurns())
		{
			System.err.print(t + ", score: ");
			double s = scoreForTree(b.nextState(t), depth-1, " |");
			// System.err.print(t + ", score: ");
			System.err.printf("%.4f", 1.0-s);

			if (turn == null) { turn = t; lowest = s; c = 1; }
			else if (s-lowest < Double.MIN_VALUE && lowest-s < Double.MIN_VALUE) {
				// if scores are the same, pick a random turn
				if (rdx.nextInt(++c) == 0) { turn = t; }
			} else if (s < lowest) { turn = t; lowest = s; c = 1; }

			System.err.print((turn == null)? "\r": " | " + turn + "\r");
			if (lowest < -Double.MIN_VALUE) { break; } // optimisation
			// must NOT break when == 0, since calculation of deeper trees may reveal preceding trees with score=0
			// which may result in an infinite loop or a slower winning move
		}

		return turn;
		//}
	}

	public EndTurn doTurn(GameState state, BoardState bstate){
		switch (state) {
		default: case NORMAL:
			switch (bstate) {
			case NORMAL:
			case NO_CHANGES:
				Turn bestTurn = bestTree(game.board, tough);
				System.err.println("");
				game.board.executePhysical(bestTurn);
				break;
			default:
				game.hwi.proceed(bstate);
			}
			return EndTurn.NORMAL; // TODO: find better draw conditions

		case DRAWOFFER:
			return EndTurn.NORMAL; // TODO: find better draw conditions
		}
	}

}
