package bravo.game;

// Represents the movement of one piece from a start square to an end square.
// Not necessarily the entirity of a turn, as many pieces may need to be moved.
// Should never need to actually change parameters of move, hence no set methods.
//
// Depends on Piece
// Depended on by most of the software side, in particular Board, AI and Pathing

public class Move {

// Constructor should take (int From, int To, Piece piece)

	final Piece p;
	final byte src, dst;

	public Move(Piece piece, byte s, byte d) {
		p = piece;
		src = s;
		dst = d;
	}

	//public abstract Piece getPiece();

	//public abstract int getFrom();

	//public abstract int getTo();

}

