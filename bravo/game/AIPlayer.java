package bravo.game;

// Contains the AI engine for playing draughts.
// Should abide by all the rules of Draughts given in the requirements specification.
// Arguments that could be passed in nextTurn are instead given in constructor and should be stored.
// This makes an instance of AI behave like a 'virtual person' who has certain characteristics and is playing one side on one board.
// Timer value gives maximum time that AI can operate for.  Can act as a pseudo difficulty setting by giving the AI less time to think to make the game easier, and to enforce the specification.
//
// Depends on Board, Move, Piece
// Depended on by Draughts
//

import java.util.Collection;

public abstract class AIPlayer {

// Constructor should take (Board board, boolean isBlack, int timer)
// board: reference to Board to play on
// isBlack: is AI playing as black? (or white?)
// timer: millisecond representation of timer

// Calculates the best 'turn' possible within the required time.
// A turn is a collection of Moves, e.g. for a double jump by Black:
// 1) move black piece beyond first white piece
// 2) remove first captured piece
// 3) move black piece to beyond second white piece
// 4) remove second captured piece.
public abstract Collection<Move> nextTurn ();

}
