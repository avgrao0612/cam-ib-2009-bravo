package bravo.game;

import java.util.HashSet;

// Provides an internal representation of the board

public class Board {

// Constructor takes no arguments as there is no feasible way of passing an initial representation of a board.

	final static byte OUT_X = 0x08;
	final static byte OUT_Y = -0x7F; // 0x80
	final static byte OUT = -0x77; // 0x88

	final static byte NONE = -1; // 0xFF

	Piece board[] = new Piece[256];
	boolean next; // next mover

	// initialise a starting board
	public Board() {

		//byte[] black = {0, 2, 4, 6, 17, 19, 21, 23, 32, 34, 36, 38};
		// squares across the centre from each other add to 119
		byte[] red = {119, 117, 115, 113, 102, 100, 98, 96, 87, 85, 83, 81};

		for (int i=0; i<red.length; ++i) {
			board[119-red[i]] = new Piece((byte)i, false, (byte)(119-red[i]));
			board[red[i]] = new Piece((byte)i, true, red[i]);
		}

	}

	// whether a given position is inside the playing area
	public static boolean inPlay(byte pos) {
		return (pos & OUT) == 0;
	}

	/* calculates the new position of a move from start
	**
	** side: which side the piece is on (0:black, 1:red)
	** hor: horizontal direction W.R.T. the player (0:left, 1:right)
	** ver: vertical direction W.R.T the player (0:backwards, 1:forwards)
	** jump: whether the move is a jump (0:move, 1:jump)
	*/
	public static byte posOf(byte start, boolean side, boolean hor, boolean ver, boolean jump) {

		if (!inPlay(start)) { return NONE; }
		int offset = (hor == ver)? 17: 15;
		int dir = (side == ver)? -1: 1;

		byte newpos = (byte)(start + (jump?2:1)*offset*dir);
		if (!inPlay(newpos)) { return NONE; }

		return newpos;
	}


	public HashSet<Turn> getValidTurns(boolean[] newpos)  {

		HashSet<Turn> hs = new HashSet<Turn>();

		for (Piece i : board) {

			if (i == null || i.side != next) { continue; }

			// check moves forwards
			//if (pos(i.pos, next, false;

			// if king, check moves backwards


			// if not king, check jumps forwards
				// if there are, then for each of them, check all jumps
			// else check all jumps
				// if there are, then for each of them, check all jumps


		}

		return hs;

	}

	// prints ASCII graphic of the board
	public String toString() {
		/*
			+---+---+---+---+---+---+---+---+---+---+
			|152|144|145|146|147|148|149|150|151|153| 9
			+---+---+---+---+---+---+---+---+---+---+
			|120|112|113|114|115|116|117|118|119|121| 7
			+---+---+---+---+---+---+---+---+---+---+
			|104|96 |97 |98 |99 |100|101|102|103|105| 6
			+---+---+---+---+---+---+---+---+---+---+
			|88 |80 |81 |82 |83 |84 |85 |86 |87 |89 | 5
			+---+---+---+---+---+---+---+---+---+---+
			|72 |64 |65 |66 |67 |68 |69 |70 |71 |73 | 4
			+---+---+---+---+---+---+---+---+---+---+
			|56 |48 |49 |50 |51 |52 |53 |54 |55 |57 | 3
			+---+---+---+---+---+---+---+---+---+---+
			|40 |32 |33 |34 |35 |36 |37 |38 |39 |41 | 2
			+---+---+---+---+---+---+---+---+---+---+
			|24 |16 |17 |18 |19 |20 |21 |22 |23 |25 | 1
			+---+---+---+---+---+---+---+---+---+---+
			| 8 | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 9 | 0
			+---+---+---+---+---+---+---+---+---+---+
			|136|128|129|130|131|132|133|134|135|137| 8
			+---+---+---+---+---+---+---+---+---+---+
			  8   0   1   2   3   4   5   6   7   9 x\y

			byte pos = |yyyy|xxxx|
					 = (y<<4) + x
		*/

		int[] c = {8, 0, 1, 2, 3, 4, 5, 6, 7, 9};
		String rowsep = "+---+---+---+---+---+---+---+---+---+---+\n";

		StringBuffer out = new StringBuffer();
		out.append(rowsep);

		int x, y;
		char ch;
		for (int i=c.length-1; i>=0; --i) {
			y = c[i];
			out.append("|");

			for (int j=0; j<c.length; ++j) {
				x = c[j];

				ch = (board[(y<<4)+x] == null)? ' ':
					(board[(y<<4)+x].side)? 'R': 'B';
				out.append(" ").append(ch).append(" |");

			}
			out.append("\n").append(rowsep);

		}
		out.append((next)?"red to move\n":"black to move\n");

		return out.toString();

	}


}
