package bravo.game;

// Internal representation of a piece on the board

public class Piece {

	final static byte KING = 0x02;
	final static byte DEAD = 0x01;

	final byte id;
	final boolean side;

	byte pos;
	byte flags;

	public Piece(byte i, boolean s, byte p) {
		id = i;
		side = s;
		pos = p;
	}

	public boolean isDead() { return (flags & DEAD) != 0; }
	public boolean isKing() { return (flags & KING) != 0; }

	public void toKing() { flags |= DEAD; }
	public void toDead() { flags |= DEAD; }

	public boolean inPlay() { return Board.inPlay(pos); }

}
