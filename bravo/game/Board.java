package bravo.game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Random;

// Internal representation of the cell at each turn.

public class Board {

	final static byte OUT_X = 0x08;
	final static byte OUT_Y = -0x80; // 0x80
	final static byte OUT = OUT_X | OUT_Y; // -0x78, 0x88
	final static byte KING_ROW = 0x70;

	final static byte NONE = -0x01; // 0xFF
	final static int B = 0x00FF; // unsigned byte mask, for indexing into array[256]s
	// i know this is a dirty hack. byte me. java sucks balls.
	// i'm not fucking using shorts when bytes will suffice.

	private Piece cell[] = new Piece[256];
	private boolean who; // next mover
	private HashSet<Turn> vt;

	// initialise a cell
	public Board(Piece[] pieces) {
		for (Piece p : pieces) {
			cell[p.pos&B] = p;
		}
		vt = new HashSet<Turn>();
		setValidTurns();
	}

	// prints ASCII graphic of the cell
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
		String cellsep = "+---+---+---+---+---+---+---+---+---+---+\n";
		String rowsep = "+   +---+---+---+---+---+---+---+---+   +\n";

		StringBuffer out = new StringBuffer();
		out.append(cellsep);

		int x, y;
		char ch;
		for (int i=c.length-1; i>=0; --i) {
			y = c[i];
			out.append("|");

			for (int j=0; j<c.length; ++j) {
				x = c[j];

				if (cell[y<<4|x] == null) {
					ch = ' ';
				} else {
					ch = (cell[y<<4|x].side)? 'w': 'b';
					// CAPS for king
					if (cell[y<<4|x].isKing()) { ch -= 32; }
				}
				//ch = (inPlay((byte)(y<<4|x)))? 'Y': 'N';
				//ch = (levelUp((byte)(y<<4|x)))? 'Y': 'N';
				out.append(" ").append(ch).append(y<8||x==9?" |":"  ");

			}
			out.append(" ").append(y).append("\n").append(y==8?cellsep:rowsep);

		}
		out.append("  8   0   1   2   3   4   5   6   7   9 x\\y\n");
		out.append((who)?"white to move\n":"black to move\n");

		return out.toString();
	}

	// whether a given position is inside the playing area
	public static boolean inPlay(byte pos) {
		return (pos & OUT) == 0;
	}

	// whether a given position is on the king row
	public boolean levelUp(byte pos) {
		return inPlay(pos) && ((who)? ((pos & KING_ROW) == 0): ((pos & KING_ROW) == KING_ROW));
	}

	public boolean who() { return who; }


	/*************************************************************************
	 * Calculate valid moves
	 *************************************************************************/

	/*
	 * only kings can move backwards
	 *
	 * move - diagonal 1
	 * jump - diagonal 2
	 *   multijump - diagonal 2, any direction by any piece
	 *
	 * king - if non-king lands on back row, turn ends
	 *
	 * game ends when other player has no valid moves left (this includes no pieces)
	 * ie. only need to check Board.getValidMoves().size() == 0
	 */

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
		return (dst != NONE && cell[dst&B] == null)? dst: NONE;
	}

	// test validity of jump. an appropriate piece is ASSUMED to be at src.
	// returns cap|dst or NONE|NONE if jump is invalid
	private short halfTestValidJump(byte src, boolean hor, boolean ver, byte ignore, byte[] ignores) {

		byte cap = posOf(src, hor, ver, false);
		// make sure the captured piece isn't on the ignore list
		for (byte b : ignores) { if (cap == b) { return NONE<<8|NONE; } }

		byte dst = posOf(src, hor, ver, true);

		return (cap != NONE && dst != NONE &&
			cell[cap&B] != null && cell[cap&B].side != who &&
			(cell[dst&B] == null || dst == ignore))? (short)(cap<<8|dst): NONE<<8|NONE;
	}


	/* calculate all possible jumps from the src position
	**
	** src: source position
	** cap: last captured piece
	** pos: current position
	** cptr: all captured pieces so far
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
		if (!levelUp(pos) || cell[src&B].isKing()) {

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

		return (pos != src)? new Turn[]{new Turn(src, pos, cptr)}: new Turn[]{};
	}
	private Turn[] getAvailableJumps(byte src) {
		return getAvailableJumps(src, NONE, src, new byte[]{});
	}


	// calculate all valid turns from the current position
	public Board setValidTurns() {
		vt.clear();

		boolean jps = false;

		for (Piece p : cell) {
			if (p == null || p.side != who || !inPlay(p.pos)) { continue; }

			// only check moves if there are no jumps
			if (!jps) {
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
				}
			}

			// check jumps
			if (p.isKing()) {
				// check jumps in all directions
				for (Turn j : getAvailableJumps(p.pos)) {
					if (!jps) { vt.clear(); jps = true; } // first jump encountered clears the set
					vt.add(j);
				}

			} else {
				// check jumps forwards
				short frs = halfTestValidJump(p.pos, true, true, NONE, new byte[]{});
				short fls = halfTestValidJump(p.pos, false, true, NONE, new byte[]{});
				// check subsequent jumps in all directions
				if ((byte)frs != NONE) {
					for (Turn j : getAvailableJumps(p.pos, (byte)(frs>>8), (byte)frs, new byte[]{})) {
						if (!jps) { vt.clear(); jps = true; } // first jump encountered clears the set
						vt.add(j);
					}
				}
				if ((byte)fls != NONE) {
					for (Turn j : getAvailableJumps(p.pos, (byte)(fls>>8), (byte)fls, new byte[]{})) {
						if (!jps) { vt.clear(); jps = true; } // first jump encountered clears the set
						vt.add(j);
					}
				}
			}
		}

		System.out.print(this);
		//System.out.println("avail moves:"); for (Turn t : vt) { System.out.println(t); }
		return this;
	}

	public boolean hasValidTurns() {
		return vt.size() > 0;
	}

	// return all valid turns
	@SuppressWarnings("unchecked") // stupid compiler
	public HashSet<Turn> getValidTurns() {
		return (HashSet<Turn>)vt.clone();
	}


	/*************************************************************************
	 * Validate state-skeleton
	 *************************************************************************/

	Random rdx = new Random();
	private boolean[] getRandomTurn() {
		// testing code ONLY
		boolean[] skel = new boolean[256];

		for (Piece p : cell) {
			if (p!=null) { skel[p.pos&B] = true; }
		}

		// pick a random turn
		Turn k = null;
		int s = rdx.nextInt(getValidTurns().size());
		int i = 0;
		for (Turn t : getValidTurns()) {
			if (i++ == s) {
				k = t; break;
			}
		}
		System.out.println(k);

		skel[k.src&B] = !skel[k.src&B];
		skel[k.dst&B] = !skel[k.dst&B];

		return skel;
	}

	private boolean[] getStateSkel() {
		// TODO: get actual user input
		return getRandomTurn();
	}

	// returns the changes between the current state and s
	private byte[] changedStateSkel(boolean[] skel) {
		byte[] changes = new byte[100]; // max of 100 changes to all squares

		int j=0;
		for (byte i=0; i!=-1; ++i) { // can't do <256 since -128 <= byte <= 127
			if (skel[i&B] && cell[i&B] == null || !skel[i&B] && cell[i&B] != null) {
				changes[j++] = i;
			}
		}

		// return a sorted subset that can be searched through with binarySearch
		changes = Arrays.copyOfRange(changes, 0, j);
		Arrays.sort(changes);
		return changes;
	}

	// validates a list of changes against a Turn t, and provide a list of
	// removals that need to be made to fully normalise the cell
	// t is ASSUMED to be a valid turn
	private boolean validateStateSkel(Turn t, byte[] changes) {
		/*for (byte b : changes) { System.out.print("0x" + String.format("%02x", b) + " "); }
		System.out.print("| " + t + "\n");*/
		int i;

		// check src and dst of turn
		i = Arrays.binarySearch(changes, t.dst);
		if (i < 0) { return false; }
		i = Arrays.binarySearch(changes, t.src);
		if (i < 0) { return false; } // this $i is used later

		// detect unrecoverable errors
		for (int j=0; j<changes.length; ++j) {
			if (j == i) { continue; } // src piece is OK to be moved
			if (inPlay(changes[j]) && cell[changes[j]&B] != null) {
				// something has been removed from the playing area
				return false;
			}
		}

		return true;
	}

	// validate a skeleton state against all possible turns
	// if mutiple turns are valid for the same state, then pick the one
	// which does the most captures
	// TODO make return changes too
	private Turn getTurnFromSkel(boolean[] state) {
		byte[] changes = changedStateSkel(state);

		int capts = -1;
		Turn k = null;
		for (Turn t : vt) {
			if (validateStateSkel(t, changes) && t.capt.length > capts) {
				capts = t.capt.length;
				k = t;
			}
		}

		return k;
	}


	/*************************************************************************
	 * Execute turn
	 *************************************************************************/

	public Board applyBoardState() {
		Turn t = getTurnFromSkel(getStateSkel());
		if (t == null) { return restoreBoardState(); }
		updateBoard(t);

		return this;
	}

	private Board restoreBoardState() {
		// restore previous board state
		return this;
	}

	// TODO executes Turn t, updating the cell with the new positions
	// taking into account physical changes already made
	// ASSUME everything in king reserve is a king, elsewhere non-king
	private Board updateBoard(Turn t /*, byte[] changes*/) {
		System.out.println(t);
		assert(vt.contains(t));

		// move from src to dst
		movePiece(t.src, t.dst);

		// king it

		// remove captured pieces
		for (byte b : t.capt) {
			killPiece(b);
		}

		who = !who;
		setValidTurns();
		// for (Turn tt : vt) { System.out.println(tt); }
		return this;
	}

	private Piece movePiece(byte src, byte dst) {
		Piece p = cell[dst&B] = cell[src&B];
		p.pos = dst;
		cell[src&B] = null;
		if (!p.isKing() && levelUp(dst)) {
			p.toKing();
		}
		return p;
	}

	// kill a piece and put it in some cell in the dead area
	private Piece killPiece(byte pos) {
		// TODO make this move it to a dead cell, instead of just setting to null
		cell[pos&B] = null;
		return null;
	}


}
