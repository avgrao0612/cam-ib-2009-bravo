package bravo.game;

// Internal representation of a piece on the board

public class Piece {

	final byte id;
	final boolean side;
	final boolean king;

	byte pos;

	public Piece(byte i, boolean s, boolean k, byte p) {
		id = i;
		side = s;
		king = k;
		pos = p;
	}

}
