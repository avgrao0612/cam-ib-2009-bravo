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


	/* calculate all possible jumps from the src position
	 *
	 * src: source position
	 * cap: last captured piece
	 * pos: current position
	 * cptr: all captured pieces so far
	 */
	private Turn[] getAvailableJumps(byte src, byte cap, byte pos, byte[] cptr) {

		if (pos == NONE) { return new Turn[]{}; }

		// add last captured piece into already-captured array
		// we do it here to avoid having to do it 4 times below
		if (cap != NONE) {
			cptr = Arrays.copyOf(cptr, cptr.length+1);
			cptr[cptr.length-1] = cap;
		}

		// stop if non-king lands on the king row
		if (!levelUp(pos) || board[src].isKing()) {

			short frs = halfTestValidJump(pos, true, true, src, cptr);
			short brs = halfTestValidJump(pos, true, false, src, cptr);
			short fls = halfTestValidJump(pos, false, true, src, cptr);
			short bls = halfTestValidJump(pos, false, false, src, cptr);
			Turn[] fra = getAvailableJumps(src, (byte)(frs>>8), (byte)frs, cptr);
			Turn[] bra = getAvailableJumps(src, (byte)(brs>>8), (byte)brs, cptr);
			Turn[] fla = getAvailableJumps(src, (byte)(fls>>8), (byte)fls, cptr);
			Turn[] bla = getAvailableJumps(src, (byte)(bls>>8), (byte)bls, cptr);

			int len = fra.length + bra.length + fla.length + bla.length;
			if (len > 0) {
				Turn[] cps = new Turn[len];
				int i = 0;
				for (Turn p : fra) { cps[i] = p; ++i; }
				for (Turn p : bra) { cps[i] = p; ++i; }
				for (Turn p : fla) { cps[i] = p; ++i; }
				for (Turn p : bla) { cps[i] = p; ++i; }
				return cps;
			}
		}

		return new Turn[]{new Turn(src, pos, cptr)};
	}
	private Turn[] getAvailableJumps(byte src) {
		return getAvailableJumps(src, NONE, src, new byte[]{});
	}


	// calculate all valid turns from the current position
	public void setValidTurns() {
		vt = new HashSet<Turn>();

		for (Piece p : board) {
			if (p == null || p.side != who || !p.inPlay()) { continue; }

			// check moves forwards
			byte frm = halfTestValidMove(p.pos, true, true);
			byte flm = halfTestValidMove(p.pos, false, true);
			if (frm != NONE) { vt.add(new Turn(p.pos, frm)); }
			if (flm != NONE) { vt.add(new Turn(p.pos, flm)); }

			if (p.isKing()) {
				// check moves backwards
				byte brm = halfTestValidMove(p.pos, true, false);
				byte blm = halfTestValidMove(p.pos, false, false);
				if (brm != NONE) { vt.add(new Turn(p.pos, brm)); }
				if (blm != NONE) { vt.add(new Turn(p.pos, blm)); }

				// check jumps in all directions
				for (Turn j : getAvailableJumps(p.pos)) { vt.add(j); }

			} else {
				// check jumps forwards
				short frs = halfTestValidJump(p.pos, true, true, NONE, new byte[]{});
				short fls = halfTestValidJump(p.pos, false, true, NONE, new byte[]{});
				// check subsequent jumps in all directions
				if ((byte)frs != NONE) {
					Turn[] js = getAvailableJumps(p.pos, (byte)(frs>>8), (byte)frs, new byte[]{});
					for (Turn j : js) { vt.add(j); }
				}
				if ((byte)fls != NONE) {
					Turn[] js = getAvailableJumps(p.pos, (byte)(fls>>8), (byte)fls, new byte[]{});
					for (Turn j : js) { vt.add(j); }
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

	// returns the changes between the current state and s
	private byte[] changedStateSkel(boolean[] skel) {
		byte[] changes = new byte[64]; // max of 64 changes to the playing area
		Arrays.fill(changes, NONE);

		int j=0;
		for (byte i=0; i<256; ++i) {
			// TODO: make game keep track of dead areas...
			if (!inPlay(i)) { continue; } // ignore changes outside the playing area

			if (skel[i] && board[i] == null || !skel[i] && board[i] != null) {
				changes[j++] = i;
			}
		}
		return changes;
	}

	// validates a list of changes against a turn t, and provide a list of
	// removals that need to be made
	private boolean validateStateSkel(Turn t, byte[] changes, byte[] removals) {
		// TODO: maybe have this throw an exception
		assert(vt.contains(t));
		Arrays.fill(removals, NONE);

		// validate the turn first
		for (int j=0; j<64; ++j) {

		}

		// see if there are extraneous things

		return false;
	}

	// TODO validate a skeleton state against all valid turns
	public Turn getTurnFromSkel(boolean[] state) {
		// TODO: resolve conflicts (mutiple turns being valid for the same state)
		byte[] changes = changedStateSkel(state);
		byte[] removals = new byte[64];

		for (Turn t : vt) {
			if (validateStateSkel(t, changes, removals)) {
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
