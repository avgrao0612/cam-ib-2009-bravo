package bravo.game;

// Internal representation of a piece on the board

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
