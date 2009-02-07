package bravo.game;

// Represents the movement of one piece from a start square to an end square.
// Not necessarily the entirity of a turn, as many pieces may need to be moved.
// Should never need to actually change parameters of move, hence no set methods.
//
// Depends on Piece
// Depended on by most of the software side, in particular Board, AI and Pathing

public class Move {

	final byte src, dst;

	public Move(byte s, byte d) {
		src = s;
		dst = d;
	}

}
