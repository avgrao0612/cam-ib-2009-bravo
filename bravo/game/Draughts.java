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
		// TODO SPEC: let player decide who to go first, make the other one AIPlayer

		double pi = (args.length == 0)? 1: Double.parseDouble(args[0]);
		//Draughts game = new Draughts(new HumanPlayer(pi), new AIPlayer(pi));
		Draughts game = new Draughts(new HumanPlayer(pi), new HumanPlayer(pi));
		game.play();

	}

	public static void printByteArray(String t, byte[] bs) {
		System.out.print(t + ": [ ");
		for (byte b: bs) { System.out.printf("0x%02x ",b); }
		System.out.println("]");
	}

	public static void printIntArray(String t, int[] bs) {
		System.out.print(t + ": [ ");
		for (int b: bs) { System.out.printf("0x%02x ",b); }
		System.out.println("]");
	}

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
