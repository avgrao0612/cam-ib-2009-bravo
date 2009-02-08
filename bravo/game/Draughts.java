package bravo.game;

// A class which deals with the turn logic. Also runnable as a standalone program

public class Draughts {

	final static byte NONE = Board.NONE;

	Board board;

	private Player black;
	private Player white;

	private Turn[] history; // TODO to be implemented

	public Draughts(Player b, Player w) {
		black = b.sit(this, false);
		white = w.sit(this, true);
		board = new Board();
	}

	public Draughts play() {
		while (board.hasValidTurns()) {
			if (!nextTurn()) { break; }
			board.applyBoardState();
		}
		handleWinner();
		return this;
	}

	private boolean nextTurn() {
		return board.who()? white.doTurn(): black.doTurn();
	}

	private Draughts handleWinner() {
		System.out.println(board.who()?"black wins":"white wins");
		return this;
	}


	public static void main(String[] args) {
		testSuite();

		try {
			// TODO SPEC: make this use HWInterface instead
			Player b = (args[0].equals("H"))? new HumanPlayer(): new AIPlayer(Integer.parseInt(args[0]));
			Player w = (args[1].equals("H"))? new HumanPlayer(): new AIPlayer(Integer.parseInt(args[1]));
			Draughts game = new Draughts(b, w);
			game.play();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Usage: Draughts [black] [white]");
			System.err.println("H means a human player; a number means an AI player with that toughness (pecommended 7).");
			System.exit(2);
		}

	}

	/*public static void printByteArray(String t, byte[] bs) {
		System.err.print(t + ": [ ");
		for (byte b: bs) { System.err.printf("0x%02x ",b); }
		System.err.println("]");
	}*/

	public static void testSuite() {

		int[] test = null;
		/*byte b = -1;
		System.out.println(b & Board.B);
		System.out.println(b);
		/*
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
