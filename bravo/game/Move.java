package bravo.game;

// Represents the movement of one piece from a start square to an end square.
// Not necessarily the entirety of a turn, as many pieces may need to be moved.
// Should never need to actually change parameters of move, hence no set methods.

public class Move {

	final public byte src, dst;

	public Move(byte s, byte d) {
		src = s;
		dst = d;
	}

}
