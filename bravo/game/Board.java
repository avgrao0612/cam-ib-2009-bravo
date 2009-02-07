package bravo.game;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

// Internal representation of the board at each turn.

// !!!IMPORTANT!!! make sure all accesses to array[256]s using a byte-type index are &B'd!!!
// otherwise, java will treat it as a signed byte, potentially turning it into a negative index
// known arrays which this applies to: cell[], skel[], chg[]

public class Board {

	final static byte OUT_X = 0x08;
	final static byte OUT_Y = -0x80; // 0x80
	final static byte OUT = OUT_X | OUT_Y; // -0x78, 0x88

	final static byte NONE = -0x01; // 0xFF
	final static int B = 0x00FF; // unsigned byte mask, for indexing into array[256]s
	// i know this is a dirty hack. byte me. java sucks balls.
	// i'm not fucking using shorts when bytes will suffice.

	final static byte[][] RES = new byte[][]{
		new byte[]{-0x70, -0x69, -0x68, -0x67, 0x48, 0x49, 0x58, 0x59, 0x68, 0x69, 0x78, 0x79}, // BN
		new byte[]{-0x6F, -0x6E, -0x6D, -0x6C, -0x6B, -0x6A}, // BK
		new byte[]{-0x80, -0x79, -0x78, -0x77, 0x08, 0x09, 0x18, 0x19, 0x28, 0x29, 0x38, 0x39}, // WN
		new byte[]{-0x7F, -0x7E, -0x7D, -0x7C, -0x7B, -0x7A}, // WK
	};

	private Piece cell[] = new Piece[256];
	private boolean who; // next mover
	private HashSet<Turn> vt;

	// initialise a board
	public Board() {
		// starting pieces
		byte[] white = {0x77, 0x75, 0x73, 0x71, 0x66, 0x64, 0x62, 0x60, 0x57, 0x55, 0x53, 0x51,
			-0x7F, -0x7E, -0x7D, -0x7C, -0x7B, -0x7A};
			// 0x81, 0x82, 0x83, 0x84, 0x85, 0x86};
		byte[] black = {0x00, 0x02, 0x04, 0x06, 0x11, 0x13, 0x15, 0x17, 0x20, 0x22, 0x24, 0x26,
			-0x6F, -0x6E, -0x6D, -0x6C, -0x6B, -0x6A};
			// 0x91, 0x92, 0x93, 0x94, 0x95, 0x96};
		for (int i=0; i<18; ++i) {
			cell[black[i]&B] = new Piece(false, !inPlay(black[i]), black[i]);
			cell[white[i]&B] = new Piece(true, !inPlay(white[i]), white[i]);
		}

		vt = new HashSet<Turn>();
		setValidTurns();
	}

	// returns a Board object of t applied to the current Board
	public Board nextState(Turn t) {
		if (!vt.contains(t)) { return null; }
		Board c = new Board();
		c.cell = cell.clone();

		c.cell[t.dst&B] = c.cell[t.src&B];
		c.cell[t.src&B] = null;

		for (byte capt : t.capt) { c.cell[capt&B] = null; }

		if (!cell[t.dst&B].king && c.levelUp(t.dst)) {
			c.cell[t.dst&B] = new Piece(c.who, true, t.dst);
		}

		c.who = !who;
		c.setValidTurns();
		return this;
	}

	// returns the ratio of the current player's pieces to the total
	public double piecesRatio(/*boolean side*/) {
		int black = 0, white = 0, total = 0;
		for (Piece p : cell) {
			if (p == null || !inPlay(p.pos)) { continue; }
			if (p.side) { ++white; } else { ++black; }
			++total;
		}
		// return side? white/total: black/total;
		return who? white/total: black/total;
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

		//int[] c = {14, 12, 10, 8, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 15};
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
					if (cell[y<<4|x].king) { ch -= 32; }
				}
				//ch = (inPlay((byte)(y<<4|x)))? 'Y': ' ';
				//ch = (isReserve((byte)(y<<4|x)))? 'Y': ' ';
				//ch = (inKRCol((byte)(y<<4|x)))? 'Y': ' ';
				//ch = (sideOf((byte)(y<<4|x)))? 'W': ' ';
				//ch = (levelUp((byte)(y<<4|x)))? 'Y': ' ';
				out.append(" ").append(ch).append(y<8||x==9?" |":"  ");

			}
			out.append(" ").append(y).append("\n").append(y==8?cellsep:rowsep);

		}
		out.append("  8   0   1   2   3   4   5   6   7   9 x\\y\n");
		//out.append(" 14  12  10   8   0   1   2   3   4   5   6   7   9  11  13  15  x\\y\n");
		out.append((who)?"white to move\n":"black to move\n");

		return out.toString();
	}

	// whether a given cell is inside the playing area
	public static boolean inPlay(byte pos) {
		return (pos & OUT) == 0;
	}

	// whether a given cell is a reserve
	public static boolean isReserve(byte pos) {
		// 100?0??? or 0???100? or 100?100?
		return (pos & 0xE8) == 0x80 || (pos & 0x8E) == 0x08 || (pos & 0xEE) == 0x88;
	}

	// whether a given cell is in the columns of the king reserves
	public static boolean inKRCol(byte pos) {
		// ????0??? and ~????0000 and ~????0111
		return (pos & OUT_X) == 0 && (pos & 0x07) != 0x00 && (pos & 0x07) != 0x07;
	}

	// which side of a board a cell is on - 0:black; 1:white
	public static boolean sideOf(byte pos) {
		// black: 00?????? or 1??0????; white: 01?????? or 1??1????
		return (pos & 0xC0) == 0x40 || (pos & 0x90) == 0x90;
	}

	public boolean who() { return who; }

	// whether a given position is on the opponent's last row
	public boolean levelUp(byte pos) {
		return who? (pos & 0xF0) == 0: (pos & 0xF0) == 0x70;
	}

	// get all reserved cells satisfying a set of properties
	public byte[] getReserves(boolean full, boolean ally, boolean king) {
		byte[] free = new byte[12];
		int h = (who==ally?1:0)*2 + (king?1:0);
		int f = 0;

		if (full) { for (byte b : RES[h]) { if (cell[b&B] != null) { free[f++] = b; } } }
		else { for (byte b : RES[h]) { if (cell[b&B] == null) { free[f++] = b; } } }

		byte[] fr = new byte[f];
		for (--f; f>=0; --f) { fr[f] = free[f]; }

		return fr;
	}

	public static void shuffleByteArray(byte[] array) {
		// from http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
		Random rng = new Random();
		int n = array.length;

		while (n > 1) {
			int k = rng.nextInt(n);
			n--;
			byte temp = array[n];
			array[n] = array[k];
			array[k] = temp;
		}
	}


	/*************************************************************************
	 * Calculate valid moves
	 *************************************************************************/

	/*
	 * only kings can move backwards
	 *
	 * move - diagonal 1
	 * jump - diagonal 2
	 *   multijump - diagonal 2, any direction (even normal pieces)
	 *
	 * king - if non-king lands on back row, turn ends
	 *
	 * game ends when other player has no valid moves left (this includes no pieces)
	 * ie. only need to check hasValidMoves()
	 */

	/* calculates the new position of a move from start
	**
	** hor: horizontal direction W.R.T. the current player (0:left, 1:right)
	** ver: vertical direction W.R.T the current player (0:backwards, 1:forwards)
	** jump: whether the move is a jump (0:move, 1:jump)
	*/
	private byte posOf(byte src, boolean hor, boolean ver, boolean jump) {

		if (!inPlay(src)) { return NONE; }
		int offset = (hor == ver)? 0x11: 0x0F;
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
		if (!levelUp(pos) || cell[src&B].king) {

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

				if (p.king) {
					// check moves backwards
					byte brm = halfTestValidMove(p.pos, true, false);
					byte blm = halfTestValidMove(p.pos, false, false);
					if (brm != NONE) { vt.add(new Turn(p.pos, brm)); }
					if (blm != NONE) { vt.add(new Turn(p.pos, blm)); }
				}
			}

			// check jumps
			if (p.king) {
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

	// TODO: research draw conditions


	/*************************************************************************
	 * Validate state-skeleton
	 *************************************************************************/

	private boolean[] getStateSkel() {
		// TODO SPEC: get actual user input
		return skel;
	}

	private boolean[] skel;
	//REMOVE THIS SHIT LATER
	public void setStateSkel(byte src, byte dst) {
		skel = new boolean[256];
		// turn current state into skeleton
		for (Piece p : cell) {
			if (p!=null) { skel[p.pos&B] = true; }
		}
		// apply new skeleton
		skel[src&B] = false;
		skel[dst&B] = true;
	}

	// returns the changes between the current state and s
	// ASSUME everything inserted to a king-reserve is a king, to a nonking-reserve a non-king

	final static byte C_INS = 0x08;
	final static byte C_LIVE = 0x04;
	final static byte C_KING = 0x02;
	final static byte C_ALLY = 0x01;

	private byte[] getStateSkelChanges(boolean[] skel) {
		byte[] chg = new byte[256];

		byte flags = 0;
		for (int i=0; i<256; ++i) {
			flags = 0;
			if (skel[i] && cell[i] == null) {
				flags |= C_INS;
				if (inPlay((byte)i)) { flags |= C_LIVE; } // else the following flags are meaningless
				if (inKRCol((byte)i)) { flags |= C_KING; }
				if (sideOf((byte)i) != who) { flags |= C_ALLY; } // reserves are on opposite side
			} else if (!skel[i] && cell[i] != null) {
				if (inPlay((byte)i)) { flags |= C_LIVE; }
				if (cell[i].king) { flags |= C_KING; }
				if (cell[i].side == who) { flags |= C_ALLY; }
			} else { chg[i] = NONE; continue; }
			chg[i] = flags;
		}

		return chg;
	}

	/* validates a list of changes against a Turn t, and
	** plans the moves needed to put board into a valid state
	** t is ASSUMED to be a valid turn

	** KEY:
	** rem-live: live squares from which a piece has been removed
	** ins-dead: reserves where a piece has been inserted
	** ins-deadK: enemy King reserves where [etc]
	** ins-deadN: enemy NonKing reserves [etc]
	** ins-deadM: allied NonKing reserves[etc]
	** ins-deadJ: allied King reserves [etc]

	** plan-PHYS: do the physical move (don't update the board)
	** plan-VIRT: update the board status
	*/

	private PendingChanges validateAndPlan(Turn t, byte[] chg, byte[][] fres, int[] highest) {
		ArrayList<Move> phys = new ArrayList<Move>();
		ArrayList<Move> virt = new ArrayList<Move>();

		// create a reverse map of changes so we can keep track of what has already been processed
		@SuppressWarnings("unchecked") // stupid compiler
		HashSet<Byte>[] chgm = new HashSet[16];
		int i;
		for (i=0; i<16; ++i) { chgm[i] = new HashSet<Byte>(); }
		for (i=0; i<256; ++i) { if (chg[i] != NONE) { chgm[chg[i]].add(new Byte((byte)i)); } }

		/* check TURN OK
		** TURN.dst in ins-live
		** TURN.src in rem-live
		** plan-VIRT src-dst
		** X = rem-live intersect TURN.capt
		** pair all K from X with ins-deadK, if can't ABORT
		** pair all N from X with ins-deadN, if can't ABORT
		** plan-PHYS to kill TURN.capt \ X
		** plan-VIRT all TURN.capt

		** if numof not-yet-captured >= highest-so-for, ABORT (keep looping turns)

		** if piece is to be KINGed:
		** plan-VIRT king the piece
		** if rem-deadJ has > ins-deadJ has:
		**   then pair up ONE rem-deadJ with ins-deadM, if can't ABORT
		** else plan-PHYS king the piece
		*/

		byte f, ksrc, kdst; // placeholder for any flag, src, dst, needed

		// match Turn t to changes made to the physical board
		if ((f = chg[t.dst&B]) == NONE || (f & 0x0C) != 0x0C) { return null; }
		chgm[f].remove(t.dst);
		if ((f = chg[t.src&B]) == NONE || (f & 0x0C) != 0x04) { return null; }
		chgm[f].remove(t.src);

		virt.add(new Move(t.src, t.dst));

		// indexs for the free reserves array
		// fres[0]: enemy normal; fres[3]: allied king
		int frENi = 0, frEKi = 0, frANi = 0, frAKi = 0;

		Iterator<Byte> itrb; // placeholder for any iterators needed

		int notcapt = 0;
		for (byte capt : t.capt) {
			Move capture;

			if ((f = chg[capt&B]) == NONE) {
				// not captured yet, assign a free reserve for it
				try {
					kdst = cell[capt&B].king? fres[1][frEKi++]: fres[0][frENi++];
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new NoFreeReserveCellsLeftException(e);
				}

				capture = new Move(capt, kdst);
				phys.add(capture);
				++notcapt;
			} else {
				// already captured
				// find a cell that the piece could have been moved to
				if (chgm[f = (byte)(C_INS|0|(cell[capt&B].king?C_KING:0)|0)].size() == 0) { return null; }
				itrb = chgm[f].iterator();
				kdst = itrb.next(); itrb.remove();

				capture = new Move(capt, kdst);
			}
			virt.add(capture);
		}

		if (notcapt >= highest[0]) { return null; }
		else { highest[0] = notcapt; }

		if (!cell[t.src&B].king && levelUp(t.dst)) {
			Move nrmoff, kingon;
			if (chgm[f = 0|0|C_KING|C_ALLY].size() > chgm[C_INS|0|C_KING|C_ALLY].size()) {
				// king already done
				itrb = chgm[f].iterator();
				ksrc = itrb.next(); itrb.remove();

				// find a cell that the normal piece could have been moved to
				if (chgm[f = C_INS|0|0|C_ALLY].size() == 0) { return null; }
				itrb = chgm[f].iterator();
				kdst = itrb.next(); itrb.remove();

				nrmoff = new Move(t.dst, kdst);
				kingon = new Move(ksrc, t.dst);
			} else {
				// king not done yet
				kdst = fres[2][frANi++];
				byte[] kres = getReserves(true, true, true);
				try {
					ksrc = kres[0];
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new NoKingsLeftException(e);
				}

				nrmoff = new Move(t.dst, kdst);
				kingon = new Move(ksrc, t.dst);
				phys.add(nrmoff);
				phys.add(kingon);
			}
			virt.add(nrmoff);
			virt.add(kingon);
		}

		/* error-correcting, optional extra
		**
		** FIX rem-live
		** pair up rem-liveX with ins-deadX
		** if can't, pair it with a piece from deadX and plan-VIRT rem that piece
		** if can't, ABORT
		** plan-PHYS reverse these pairs
		** NO rem-live left by this stage

		** FIX rem-dead
		** pair up rem-deadX with ins-deadX, if can't, ignore
		** plan-VIRT these pairs

		** FIX ins-live
		** if there are a mixture of rem-dead{J,K,M,N}, ABORT
		** pair up ins-live with rem-dead
		** plan-PHYS reverse these pairs
		** either: ins-live not empty, plan-PHYS kill these, plan-MSG "make sure reserves have correct items in them"
		** either: rem-dead not empty, plan-VIRT rem these

		** FIX ins-dead
		** plan-VIRT ins extra ins-dead
		*/

		// make sure there are no more extraneous changes
		// depending on how advanced the error-correction above is, this could be removed
		for (HashSet<Byte> hb : chgm) { if (hb.size() > 0) { return null; } }

		return new PendingChanges(t, phys.toArray(new Move[phys.size()]), virt.toArray(new Move[virt.size()]));
	}

	// validate a skeleton state against all possible turns
	// if mutiple turns are valid for the same state, then pick the one
	// which requires the least changes
	private PendingChanges getPendingChanges(boolean[] state) {
		byte[] chg = getStateSkelChanges(state);
		int[] high = new int[]{Integer.MAX_VALUE};

		byte[][] fres = new byte[][]{
			getReserves(false, false, false),
			getReserves(false, false, true),
			getReserves(false, true, false),
			getReserves(false, true, true),
		};
		shuffleByteArray(fres[0]); shuffleByteArray(fres[1]);
		shuffleByteArray(fres[2]); shuffleByteArray(fres[3]);

		PendingChanges pc = null, p = null;
		for (Turn t : vt) {
			p = validateAndPlan(t, chg, fres, high);
			if (p != null) { pc = p; }
		}

		return pc;
	}


	/*************************************************************************
	 * Execute turn
	 *************************************************************************/

	public Board applyBoardState() {
		PendingChanges pc = getPendingChanges(getStateSkel());
		if (pc == null) { return restoreBoardState(); }
		updateBoard(pc);

		return this;
	}

	// TODO SPEC: maybe have this throw an exception / warning lights instead
	private Board restoreBoardState() {
		System.out.println("got here, the reset part");
		// restore previous board state
		return this;
	}

	// executes pending changes
	private Board updateBoard(PendingChanges pc) {
		//System.out.println(pc.turn);
		assert(vt.contains(pc.turn));

		pc.execute();

		who = !who;
		setValidTurns();
		return this;
	}

	private Piece movePiece(byte src, byte dst) {
		Piece p = cell[dst&B] = cell[src&B];
		// if (p == null) { throw new RuntimeException(String.format("asked to move a non-existent piece: 0x%02x to 0k%02x", src, dst)); }
		p.pos = dst;
		cell[src&B] = null;
		return p;
	}

	private class PendingChanges {

		Turn turn;
		Move[] phys;
		Move[] virt;

		public PendingChanges(Turn t, Move[] p, Move[] v) {
			turn = t;
			phys = p;
			virt = v;
		}

		public void execute() {

			for (Move p : phys) {
				// TODO SPEC: Pathing.execute(p);
			}

			for (Move v : virt) {
			// move from src to dst
				// System.out.printf("virtual move: 0x%02x to 0x%02x\n", v.src, v.dst);
				movePiece(v.src, v.dst);
			}

		}
	}

	private class NoKingsLeftException extends java.lang.RuntimeException {
		public NoKingsLeftException(Throwable e) { super(e); }
	}

	private class NoFreeReserveCellsLeftException extends java.lang.RuntimeException {
		public NoFreeReserveCellsLeftException(Throwable e) { super(e); }
	}

}
