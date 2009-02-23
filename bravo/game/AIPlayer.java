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

public class AIPlayer extends Player{

	public AIPlayer(int t) {
		tough = t;
	}

	protected int tough=5;

	protected double scoreForTree(Board b, int depth, String pre){
		if (depth == 0) { return b.piecesRatio(); }
		double lowest = 1.0;

		for (Turn t: b.getValidTurns())
		{
			double s = scoreForTree(b.nextState(t), depth-1, pre + " |");
			// if (turns > 8 && depth > 3) { System.err.println(pre + "-" + t + " " + s); }
			if (s<lowest) { lowest = s; }
			if (lowest <= 0.0) { break; } // optimisation
		}

		//System.err.print(depth + " " + k + ", score: " + (1.0-lowest) + "\r");
		return 1.0 - lowest;

	}

	protected Turn bestTree(Board b, int depth){
		//Board highestBoard = null;
		//if (tough==0){
			//doRandomTurn();
		//return null;
		//}
		//else{
		Turn turn = null;
		double lowest = 1.0;

		for (Turn t: b.getValidTurns())
		{
			System.err.print(t + ", score: ");
			double s = scoreForTree(b.nextState(t), depth-1, " |");
			// System.err.print(t + ", score: ");
			System.err.printf("%.4f", 1.0-s);

			// if turn is null then it's a guaranteed loss
			if (turn == null || s < lowest) { turn = t; lowest = s; }

			System.err.print((turn == null)? "\r": " | " + turn + "\r");
			if (lowest <= 0.0) { break; } // optimisation
		}

		return turn;
		//}
	}

	public EndTurn doTurn(GameState state){
		switch (state) {
		default: case NORMAL:
			Turn bestTurn = bestTree(game.board, tough);
			System.err.println("");

			// TODO: make it actually move
			boolean[] skel = game.board.getStateSkel(bestTurn.src, bestTurn.dst);
			game.board.applyBoardState(skel);
			return EndTurn.NORMAL; // TODO: find better draw conditions

		case DRAWOFFER:
			return EndTurn.NORMAL; // TODO: find better draw conditions
		}
	}

}
