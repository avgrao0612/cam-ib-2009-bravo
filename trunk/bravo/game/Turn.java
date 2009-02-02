package bravo.game;

// Internal representation of a turn by a player

public class Turn {


	final static byte MOVE = 0x01;
	final static byte JUMP = 0x02;
	final static byte JUMPX = 0x03;

	Piece subject;
	byte type;
	byte[] path;

}
