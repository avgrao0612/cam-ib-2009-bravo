// Contains the AI engine for playing draughts.
// Should abide by all the rules of Draughts given in the requirements specification.
// Arguments that could be passed in nextTurn are instead given in constructor and should be stored.
// This makes an instance of AI behave like a 'virtual person' who has certain characteristics and is playing one side on one board.
// Timer value gives maximum time that AI can operate for.  Can act as a pseudo difficulty setting by giving the AI less time to think to make the game easier, and to enforce the specification.
//
// Depends on Board, Move, Piece
// Depended on by Draughts

package bravo.game;

public class AIPlayer extends Player{

	protected double score = 0.0;

	public AIPlayer(double p, int t) {
		pollint = p; // TODO: debug only, remove later
		tough = t;
	}

	// initialise variables
	protected int tough=5;

	protected double scoreForTree(boolean side, Board b, int depth){
		if (depth == 0) { return b.piecesRatio(); }
		double lowest = 1.0;

		Turn k = null;
		for (Turn t: b.getValidTurns())
		{
			double s = scoreForTree(side, b.nextState(t), depth-1);
			if (s<lowest) { k = t; lowest = s; }
			if (lowest <= 0.0) { break; } // optimise
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
		double highest = 0;
		double lowest = 1.0;

		for (Turn t: b.getValidTurns())
		{
			System.err.print(t + ", score: ");
			double s = scoreForTree(b.who(), b.nextState(t), depth-1);
			System.err.printf("%.4f", 1.0-s);

			// if turn is null then it's a guaranteed loss
			if (turn == null || s < lowest) { turn = t; lowest = s; }

			System.err.print((turn == null)? "\r": " | " + turn + "\r");
			if (lowest <= 0.0) { break; } // optimise
		}

		return turn;
		//}
	}


	final double pollint;

	public boolean doTurn(){

		try {
			Thread.sleep((int)(pollint * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Turn bestTurn = bestTree(game.board, tough);
		if (bestTurn!=null){
			game.board.setStateSkel(bestTurn.src, bestTurn.dst);
		}
		return true;
	}

}
