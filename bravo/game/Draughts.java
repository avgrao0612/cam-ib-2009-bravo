package bravo.game;

// A class which deals with the turn logic. Also runnable as a standalone program

public class Draughts {

	final static byte NONE = Board.NONE;

	Board board;

	private Player black;
	private Player white;
	private Piece[] ps;

	private Turn[] history; // code this

	public Draughts(Player b, Player w) {
		black = b;
		white = w;
		black.sit(this, false);
		white.sit(this, true);

		// init pieces
		byte[] white = {0x77, 0x75, 0x73, 0x71, 0x66, 0x64, 0x62, 0x60, 0x57, 0x55, 0x53, 0x51,
			-0x6F, -0x6E, -0x6D, -0x6C, -0x6B, -0x6A};
			// 0x91, 0x92, 0x93, 0x94, 0x95, 0x96};
		byte[] black = {0x00, 0x02, 0x04, 0x06, 0x11, 0x13, 0x15, 0x17, 0x20, 0x22, 0x24, 0x26,
			-0x7F, -0x7E, -0x7D, -0x7C, -0x7B, -0x7A};
			// 0x81, 0x82, 0x83, 0x84, 0x85, 0x86};
		ps = new Piece[36];
		for (int i=0; i<18; ++i) {
			ps[i<<1] = new Piece((byte)i, false, black[i]);
			ps[(i<<1)+1] = new Piece((byte)i, true, white[i]);
			if (!board.inPlay(black[i])) { ps[i<<1].toKing(); }
			if (!board.inPlay(white[i])) { ps[(i<<1)+1].toKing(); }
		}
		board = new Board(ps);

	}

	public Draughts play() {
		while (board.hasValidTurns()) {
			nextTurn();
			board.applyBoardState();
		}
		handleWinner();
		return this;
	}


	public Draughts nextTurn() {
		if (board.who()) { white.doTurn(); } else { black.doTurn(); }
		return this;
	}

	public Draughts handleWinner() {
		System.out.println(board.who()?"black wins":"white wins");
		return this;
	}


	public static void main(String[] args) {
		testMain();

		Draughts game = new Draughts(new HumanPlayer(), new HumanPlayer());
		game.play();

	}

	public static void testMain() {

		int[] test = null;
		System.out.println(String.format("%02x", 6));
		/*byte b = -1;
		System.out.println(b & Board.B);
		System.out.println(b);
		/*
		byte[] white = {0x77, 0x71, 0x66, 0x64, 0x44, 0x42, 0x24, 0x22, 0x62};
		byte[] black = {0x00, 0x02, 0x04, 0x06, 0x11, 0x13, 0x15, 0x17, 0x20, 0x26, 0x57, 0x55};
		GameBoard = new Board(black, white);
		Piece a = GameBoard.board[0x57];
		a.flags = -1;
		GameBoard.setValidTurns();
		System.out.println(a.isDead()? "yes": "no");
		System.out.println(a.isKing()? "yes": "no");
		byte b = (byte) 0xFF;
		System.out.printf("0x%x\n", b);
		b = -1;
		System.out.printf("0x%x\n", b);
		b += 2;
		System.out.printf("0x%x\n", b);
		System.out.printf("0x%x\n", 1<<2|1);
		System.out.printf("0x%x\n", 3+5 >> 2);
		System.out.print(GameBoard);
		System.out.println("Available Moves:");
		java.util.HashSet<Turn> ts = GameBoard.getValidTurns();
		for (Turn t : ts) {
			System.out.println(t);
		}
		System.out.println(ts.contains(new Turn((byte)0x13, (byte)0x13, new byte[]{0x24, 0x44, 0x42, 0x22})));
		System.out.println(ts.contains(new Turn((byte)0x13, (byte)0x13, new byte[]{0x22, 0x44, 0x42, 0x24})));
		System.out.println(ts.contains(new Turn((byte)0x13, (byte)0x15, new byte[]{0x24, 0x44, 0x42, 0x22})));
		*/
	}

}
