package bravo.game;

import java.util.Arrays;
// Internal representation of a turn by a player

public class Turn {


	final byte src, dst;
	final byte[] capt;

	public Turn(byte s, byte d, byte[] c) {
		src = s;
		dst = d;
		capt = c;
	}

	public Turn(byte s, byte d) {
		src = s;
		dst = d;
		capt = new byte[]{};
	}

	// returns the list of middle steps, if this is a jump
	public byte[] steps() {
		byte[] c = new byte[capt.length-1];
		// TODO: code
		return c;
	}

	// turn this into Move objects
	public Move[] toMove() {
		return new Move[0];
	}

	// description of the turn
	public String toString() {
		StringBuffer out = new StringBuffer("0x" + Integer.toHexString(src) + (capt.length>0?" jump ":" move ") + "0x" + Integer.toHexString(dst));
		if (capt.length > 0) {
			out.append(" [");
			for (int i=0; i<capt.length-1; ++i) {
				out.append("0x").append(Integer.toHexString(capt[i])).append(", ");
			}
			out.append("0x").append(Integer.toHexString(capt[capt.length-1]));
			out.append("] ");
		}
		return out.toString();
	}

	// override equals and hashCode for better equality testing

	public boolean equals(Object o) {
		if (o instanceof Turn) {
			Turn t = (Turn) o;
			return src == t.src && dst == t.dst && Arrays.equals(capt, t.capt);
		}
		return false;
	}

	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + src;
		hash = hash * 31 + dst;
		hash = hash * 31 + Arrays.hashCode(capt);
		return hash;
	}


}
