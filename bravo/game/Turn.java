package bravo.game;

// Internal representation of a turn by a player

public class Turn {


	final Piece subject;
	final boolean type; // 0:move, 1:jump
	final byte[] path;

	public Turn(Piece s, boolean t, byte[] p) {
		subject = s;
		type = t;
		path = p;
		// TODO: check type == 0 => path length == 2 else length > 1
	}

	// turn this into Move objects
	public Move[] toMove() {
		return new Move[0];
	}

	// description of the turn
	public String toString() {
		StringBuffer out = new StringBuffer((subject.side?"RED: ":"BLACK: ") + subject.pos + (type?" JUMP":" MOVE") + " to ");
		for (int i=1; i<path.length-1; ++i) {
			out.append(path[i]).append(", ");
		}
		out.append(path[path.length-1]);
		return out.toString();
	}

}
