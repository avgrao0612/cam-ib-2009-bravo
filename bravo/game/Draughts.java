package bravo.game;

// A class which deals with the turn logic. Also runnable as a standalone program

import bravo.io.HWInterface;

public class Draughts {

	final static byte NONE = Board.NONE;

	public enum GameState { NORMAL, DRAWOFFER }
	public enum EndTurn { NORMAL, DRAW, RESIGN }
	public enum EndGame { NONE, BLACK, WHITE, DRAW }

	Board board;
	HWInterface hwi;
	private Player black;
	private Player white;
	GameState state;

	private Turn[] history; // TODO to be implemented

	public Draughts(HWInterface h, Player b, Player w) {
		black = b.sit(this, false);
		white = w.sit(this, true);
		hwi = h;
		board = new Board(h);
		state = GameState.NORMAL;
	}

	public EndGame play() {
		while (board.hasValidTurns()) {
			hwi.nextRound(board.who(), state);
			switch(nextTurn()) {
			case NORMAL:
				if (state == GameState.NORMAL) {
					//board.applyBoardState();
				} else {
					state = GameState.NORMAL;
				}
				break;
			case DRAW:
				if (state == GameState.DRAWOFFER) {
					return EndGame.DRAW;
				} else {
					state = GameState.DRAWOFFER;
				}
				break;
			case RESIGN:
				return board.who()? EndGame.BLACK: EndGame.WHITE;
			}
		}
		return board.who()? EndGame.BLACK: EndGame.WHITE;
	}

	private EndTurn nextTurn() {
		return board.who()? white.doTurn(state): black.doTurn(state);
	}

	private Draughts handleWinner(EndGame end) {
		hwi.gameOver(end);
		return this;
	}


	public static void main(String[] args) {
		testSuite();

		HWInterface h = new DummyHWInterface("none", 115200);
		int gameopts = h.gameStart();


		
		try {
			Player b = (args[0].equals("H"))? new HumanPlayer(): new AIPlayer(Integer.parseInt(args[0]));
			Player w = (args[1].equals("H"))? new HumanPlayer(): new AIPlayer(Integer.parseInt(args[1]));
			Draughts game = new Draughts(h, b, w);
			game.handleWinner(game.play());

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Usage: Draughts [black] [white]");
			System.err.println("H means a human player; a number means an AI player with that toughness (recommended 7).");
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
	
	public static class DummyHWInterface extends HWInterface {
	
	public DummyHWInterface(String s, int b) { super(s,b); }
	
	public void gameOver(EndGame g) {
		switch (g) {
		case BLACK: System.out.println("black wins"); return;
		case WHITE: System.out.println("white wins"); return;
		case DRAW: System.out.println("draw"); return;
		}
	}
	
	private void transmit(String method, byte signal) { }
	private int receive(String method, byte[] validSignal) { return 0; }
	private int receive(String method, byte signalType) { return 0; }
	
	}

}
