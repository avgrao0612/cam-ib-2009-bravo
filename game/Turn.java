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

	// returns the intermediate steps, if this is a multi-jump
	public byte[] getPath() {
		if (capt.length == 0) { return new byte[]{}; }
		byte[] c = new byte[capt.length-1];
		// TODO: code this
		return c;
	}

	// turn this into Move objects
	public Move[] toMove() {
		return new Move[0];
	}

	// description of the turn
	public String toString() {
		StringBuffer out = new StringBuffer(String.format("0x%02x", src) + (capt.length>0?" => ":" -> ") + String.format("0x%02x", dst));
		if (capt.length > 0) {
			out.append(String.format(" [0x%02x", capt[0]));
			for (int i=1; i<capt.length; ++i) {
				out.append(String.format(", 0x%02x", capt[i]));
			}
			out.append("]");
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
