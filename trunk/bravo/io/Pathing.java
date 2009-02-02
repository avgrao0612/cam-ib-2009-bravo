// Engine that works out the best way for the hardware to move around the board, given a Move
// Should note requirements spec, in particular need for timing and not disturbing other pieces
// Should use constants for direction defined in HWInterface
//
// Depends heavily on HWInterface's headMove and magenet* methods.
// Depended on by Draughts

public abstract class Pathing {

// Constructor requires no arguments

// Takes a move, calculates the best way to carry it out, given the current board, and makes calls to the hardware interface.  
// Returns when it has completed the move.
// If it is not possible to make a move, (should never be the case?) should return false.
// Can move Pieces in the dead space as necessary
// Should update the Board
public abstract boolean processMove (Move move, Board board);

}
