package bravo.game;

import java.util.HashSet;
import java.util.Arrays;

// Provides an internal representation of the board

public class Board {

	final static byte OUT_X = 0x08;
	final static byte OUT_Y = -0x80; // 0x80
	final static byte OUT = OUT_X | OUT_Y; // -0x78, 0x88
	final static byte KING_ROW = 0x70;

	final static byte NONE = -0x01; // 0xFF

	Piece board[] = new Piece[256];
	boolean who; // next mover

	private HashSet<Turn> vt;

	// initialise a starting board
	public Board() {
		// squares across the centre from each other add to 0x77
		byte[] red = {0x77, 0x75, 0x73, 0x71, 0x66, 0x64, 0x62, 0x60, 0x57, 0x55, 0x53, 0x51};

		for (int i=0; i<red.length; ++i) {
			board[0x77-red[i]] = new Piece((byte)i, false, (byte)(0x77-red[i]));
			board[red[i]] = new Piece((byte)i, true, red[i]);
		}

		setValidTurns();
	}

	// initialise a board with some pieces
	public Board(byte[] black, byte[] red) {
		for (int i=0; i<red.length; ++i) { board[red[i]] = new Piece((byte)i, true, red[i]); }
		for (int i=0; i<black.length; ++i) { board[black[i]] = new Piece((byte)i, false, black[i]); }

		setValidTurns();
	}


	// prints ASCII graphic of the board
	public String toString() {
		/*
			+---+---+---+---+---+---+---+---+---+---+
			|152 144 145 146 147 148 149 150 151 153| 9
			+   +---+---+---+---+---+---+---+---+   +
			|120|112|113|114|115|116|117|118|119|121| 7
			+   +---+---+---+---+---+---+---+---+   +
			|104|96 |97 |98 |99 |100|101|102|103|105| 6
			+   +---+---+---+---+---+---+---+---+   +
			|88 |80 |81 |82 |83 |84 |85 |86 |87 |89 | 5
			+   +---+---+---+---+---+---+---+---+   +
			|72 |64 |65 |66 |67 |68 |69 |70 |71 |73 | 4
			+   +---+---+---+---+---+---+---+---+   +
			|56 |48 |49 |50 |51 |52 |53 |54 |55 |57 | 3
			+   +---+---+---+---+---+---+---+---+   +
			|40 |32 |33 |34 |35 |36 |37 |38 |39 |41 | 2
			+   +---+---+---+---+---+---+---+---+   +
			|24 |16 |17 |18 |19 |20 |21 |22 |23 |25 | 1
			+   +---+---+---+---+---+---+---+---+   +
			| 8 | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 9 | 0
			+   +---+---+---+---+---+---+---+---+   +
			|136 128 129 130 131 132 133 134 135 137| 8
			+---+---+---+---+---+---+---+---+---+---+
			  8   0   1   2   3   4   5   6   7   9 x\y

			byte pos = |yyyy|xxxx|
					 = (y<<4) | x
		*/

		int[] c = {8, 0, 1, 2, 3, 4, 5, 6, 7, 9};
		String boardsep = "+---+---+---+---+---+---+---+---+---+---+\n";
		String rowsep = "+   +---+---+---+---+---+---+---+---+   +\n";

		StringBuffer out = new StringBuffer();
		out.append(boardsep);

		int x, y;
		char ch;
		for (int i=c.length-1; i>=0; --i) {
			y = c[i];
			out.append("|");

			for (int j=0; j<c.length; ++j) {
				x = c[j];

				if (board[y<<4|x] == null) {
					ch = ' ';
				} else {
					ch = (board[y<<4|x].side)? 'r': 'b';
					// CAPS for king
					if (board[y<<4|x].isKing()) { ch -= 32; }
				}
				//ch = (inPlay((byte)(y<<4|x)))? 'Y': 'N';
				//ch = (levelUp((byte)(y<<4|x)))? 'Y': 'N';
				out.append(" ").append(ch).append(y<8||x==9?" |":"  ");

			}
			out.append(" ").append(y).append("\n").append(y==8?boardsep:rowsep);

		}
		out.append("  8   0   1   2   3   4   5   6   7   9 x\\y\n");
		out.append((who)?"red to move\n":"black to move\n");

		return out.toString();

	}

	// whether a given position is inside the playing area
	public static boolean inPlay(byte pos) {
		return (pos & OUT) == 0;
	}

	// whether a given position is on the king row
	public boolean levelUp(byte pos) {
		return inPlay(pos) && ((who)? (pos & KING_ROW) == 0: (pos & KING_ROW) == KING_ROW);
	}


	/*************************************************************************
	 * Calculate valid moves
	 *************************************************************************/

	/* calculates the new position of a move from start
	**
	** hor: horizontal direction W.R.T. the current player (0:left, 1:right)
	** ver: vertical direction W.R.T the current player (0:backwards, 1:forwards)
	** jump: whether the move is a jump (0:move, 1:jump)
	*/
	private byte posOf(byte src, boolean hor, boolean ver, boolean jump) {

		if (!inPlay(src)) { return NONE; }
		int offset = (hor == ver)? 17: 15;
		int dir = (who == ver)? -1: 1;

		byte dst = (byte)(src + (jump?2:1)*offset*dir);
		if (!inPlay(dst)) { return NONE; }

		return dst;
	}


	// test validity of move. an appropriate piece is ASSUMED to be at src.
	// returns dst or NONE if move is invalid
	private byte halfTestValidMove(byte src, boolean hor, boolean ver) {
		byte dst = posOf(src, hor, ver, false);
		return (dst != NONE && board[dst] == null)? dst: NONE;
	}

	// test validity of jump. an appropriate piece is ASSUMED to be at src.
	// returns cap|dst or NONE|NONE if jump is invalid
	private short halfTestValidJump(byte src, boolean hor, boolean ver, byte ignore, byte[] ignores) {

		byte cap = posOf(src, hor, ver, false);
		// make sure the captured piece isn't on the ignore list
		for (byte b : ignores) { if (cap == b) { return NONE<<8|NONE; } }

		byte dst = posOf(src, hor, ver, true);

		return (cap != NONE && dst != NONE &&
			board[cap] != null && board[cap].side != who &&
			(board[dst] == null || dst == ignore))? (short)(cap<<8|dst): NONE<<8|NONE;
	}


	// calculate all possible jumps from the src position, keeping track of
	// all pieces that have already been taken
	private byte[][] getAvailableJumps(byte src, byte cap, byte[] path, byte[] cptr) {

		if (src == NONE) { return new byte[][]{}; }
		path = Arrays.copyOf(path, path.length+1);
		path[path.length-1] = src;

		if (cap != NONE) {
			cptr = Arrays.copyOf(cptr, cptr.length+1);
			cptr[cptr.length-1] = cap;
		}

		// stop if non-king lands on the king row
		if (!levelUp(src) || board[path[0]].isKing()) {

			short frs = halfTestValidJump(src, true, true, path[0], cptr);
			short brs = halfTestValidJump(src, true, false, path[0], cptr);
			short fls = halfTestValidJump(src, false, true, path[0], cptr);
			short bls = halfTestValidJump(src, false, false, path[0], cptr);
			byte[][] fra = getAvailableJumps((byte)frs, (byte)(frs>>8), path, cptr);
			byte[][] bra = getAvailableJumps((byte)brs, (byte)(brs>>8), path, cptr);
			byte[][] fla = getAvailableJumps((byte)fls, (byte)(fls>>8), path, cptr);
			byte[][] bla = getAvailableJumps((byte)bls, (byte)(bls>>8), path, cptr);

			int len = fra.length + bra.length + fla.length + bla.length;
			if (len > 0) {
				byte[][] jumps = new byte[len][];
				int i = 0;
				for (byte[] p : fra) { jumps[i] = p; ++i; }
				for (byte[] p : bra) { jumps[i] = p; ++i; }
				for (byte[] p : fla) { jumps[i] = p; ++i; }
				for (byte[] p : bla) { jumps[i] = p; ++i; }
				return jumps;
			}
		}

		return (path.length > 1)? new byte[][]{path}: new byte[][]{};
	}
	private byte[][] getAvailableJumps(byte src) {
		return getAvailableJumps(src, NONE, new byte[]{}, new byte[]{});
	}


	// calculate all valid turns from the current position
	public void setValidTurns() {

		vt = new HashSet<Turn>();

		for (Piece p : board) {
			if (p == null || p.side != who || !p.inPlay()) { continue; }

			// check moves forwards
			byte frm = halfTestValidMove(p.pos, true, true);
			byte flm = halfTestValidMove(p.pos, false, true);
			if (frm != NONE) { vt.add(new Turn(p, false, new byte[]{p.pos, frm})); }
			if (flm != NONE) { vt.add(new Turn(p, false, new byte[]{p.pos, flm})); }

			if (p.isKing()) {
				// check moves backwards
				byte brm = halfTestValidMove(p.pos, true, false);
				byte blm = halfTestValidMove(p.pos, false, false);
				if (brm != NONE) { vt.add(new Turn(p, false, new byte[]{p.pos, brm})); }
				if (blm != NONE) { vt.add(new Turn(p, false, new byte[]{p.pos, blm})); }

				// check jumps in all directions
				byte[][] js = getAvailableJumps(p.pos);
				for (byte[] j : js) { vt.add(new Turn(p, true, j)); }

			} else {
				// check jumps forwards
				short frs = halfTestValidJump(p.pos, true, true, NONE, new byte[]{});
				short fls = halfTestValidJump(p.pos, false, true, NONE, new byte[]{});
				// check subsequent jumps in all directions
				if ((byte)frs != NONE) {
					byte[][] js = getAvailableJumps((byte)frs, (byte)(frs>>8), new byte[]{p.pos}, new byte[]{});
					for (byte[] j : js) { vt.add(new Turn(p, true, j)); }
				}
				if ((byte)fls != NONE) {
					byte[][] js = getAvailableJumps((byte)fls, (byte)(fls>>8), new byte[]{p.pos}, new byte[]{});
					for (byte[] j : js) { vt.add(new Turn(p, true, j)); }
				}

			}

		}

	}

	// return all valid turns
	@SuppressWarnings("unchecked") // stupid compiler
	public HashSet<Turn> getValidTurns() {
		return (HashSet<Turn>)vt.clone();
	}


	/*************************************************************************
	 * Validate state-skeleton
	 *************************************************************************/

	// returns the expected state-skeleton after t is executed
	private boolean[] expectedStateSkel(Turn t) {
		assert(vt.contains(t));
		boolean[] expected = new boolean[256];

		// init skel for current board
		for (Piece p : board) {
			if (p == null || p.side != who || !p.inPlay()) { continue; }
			expected[p.pos] = true;
		}

		// play the turn on the skeleton
		expected[t.path[0]] = false;
		if (t.type) { // captured pieces
			expected[t.sub.pos] = false;
			for (int i=1; i<t.path.length; ++i) {
				int cap = t.path[i]+t.path[i-1] >> 1;
				expected[cap] = false;
			}

		}
		expected[t.path[t.path.length-1]] = true;

		return expected;
	}

	// validates a state skeleton against a turn t, and provide a list of
	// corrections that need to be made
	private boolean validateStateSkel(Turn t, boolean[] state, byte[] corrections) {
		// TODO: maybe have this throw exception
		assert(vt.contains(t));

		boolean[] expected = expectedStateSkel(t);
		return false;
	}

	// validate a skeleton state against all valid turns
	public Turn getTurnFromSkel(boolean[] state) {
		// TODO: resolve conflicts (mutiple turns being valid for the same state)
		byte[] corrections = new byte[256];
		for (Turn t : vt) {
			if (validateStateSkel(t, state, corrections)) {
				return t;
			}
		}
		return null;
	}


	/*************************************************************************
	 * Execute turn
	 *************************************************************************/

	// TODO executes Turn t, updating the board with the new positions
	private boolean executeTurn(Turn t) {

		setValidTurns();
		return false;
	}

	// TODO kill a piece and put it in some cell in the dead area
	private byte killPiece(Piece p) {
		return NONE;
	}


}
