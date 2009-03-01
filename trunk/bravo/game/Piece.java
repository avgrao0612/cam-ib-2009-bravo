package bravo.game;

// Internal representation of a piece on the board
// Piece is immutable, to reflect the properties of the physical piece

public class Piece {

	final boolean side;
	final boolean king;

	byte pos;

	public Piece(boolean s, boolean k, byte p) {
		side = s;
		king = k;
		pos = p;
	}

}
