package bravo.game;

import java.util.Arrays;
// Internal representation of a turn by a player

public class Turn {


	final Piece sub;
	final boolean type; // 0:move, 1:jump
	final byte[] path;

	public Turn(Piece s, boolean t, byte[] p) {
		assert(s.pos == p[0]);
		// TODO: check type == 0 => path length == 2 else length > 1
		// throw exception and prevent object creation otherwise

		sub = s;
		type = t;
		path = p;
	}

	// turn this into Move objects
	public Move[] toMove() {
		return new Move[0];
	}

	// description of the turn
	public String toString() {
		StringBuffer out = new StringBuffer((sub.side?"RED: ":"BLACK: ") + "0x" + Integer.toHexString(sub.pos) + (type?" JUMP":" MOVE") + " to ");
		for (int i=1; i<path.length-1; ++i) {
			out.append("0x").append(Integer.toHexString(path[i])).append(", ");
		}
		out.append("0x").append(Integer.toHexString(path[path.length-1]));
		return out.toString();
	}

	// override equals and hashCode for better equality testing

	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (!(o instanceof Turn)) { return false; }
		Turn t = (Turn) o;
		return type == t.type && Arrays.equals(path, t.path);
	}

	public int hashCode() {
		return type? Arrays.hashCode(path): ~Arrays.hashCode(path);
	}

}
