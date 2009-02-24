package bravo.game;

// A class which deals with the turn logic. Also runnable as a standalone program

import bravo.io.HWInterface;
import bravo.game.Board.*;

public class Draughts {

	public enum GameState { NORMAL, DRAWOFFER }
	public enum EndTurn { NORMAL, DRAW, RESIGN }
	public enum EndGame { NONE, BLACK, WHITE, DRAW }

	Board board;
	HWInterface hwi;
	private Player black;
	private Player white;
	GameState state;
	BoardState bstate;

	public Draughts(HWInterface h, Player b, Player w) {
		black = b.sit(this, false);
		white = w.sit(this, true);
		hwi = h;
		board = new Board(h);
		state = GameState.NORMAL;
		bstate = BoardState.NORMAL;
	}

	public EndGame play() {
		while (board.hasValidTurns()) {
			hwi.nextRound(board.who(), state);
			switch(nextTurn()) {
			case NORMAL:
				if (state == GameState.NORMAL) {
					bstate = board.applyBoardState(hwi.scan());
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
		return board.who()? white.doTurn(state, bstate): black.doTurn(state, bstate);
	}

	private Draughts handleWinner(EndGame end) {
		hwi.gameOver(end);
		return this;
	}


	public static void main(String[] args) {
		testSuite();

		HWInterface h = (args.length == 0)? new HWInterface("none", 115200): new DummyHWInterface(args);

		for (;;) {
			int gameopts = h.gameStart();
			Player b = makePlayer((gameopts>>3) & 0x07);
			Player w = makePlayer(gameopts & 0x07);

			Draughts game = new Draughts(h, b, w);
			if (h instanceof DummyHWInterface) { ((DummyHWInterface)h).setGame(game); }

			game.handleWinner(game.play());
		}

	}

	private static Player makePlayer(int opt) {
		return
			(opt&4) != 0? new AIPlayer(7):
			(opt&2) != 0? new AIPlayer(5):
			(opt&1) != 0? new AIPlayer(3): new HumanPlayer();
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

		int opt = -1;
		public DummyHWInterface(String[] args) {
			super("USB1", 115200);
			try {
				int o = 0;
				o |= (args[1].equals("H"))? 0: Integer.parseInt(args[1]);
				o <<= 3;
				o |= (args[0].equals("H"))? 0: Integer.parseInt(args[0]);
				opt = o;
			} catch (Exception e) {
				System.err.println("Usage: Draughts [black] [white]");
				System.err.println("H means a human player; a number means an AI player with that toughness (1=easy,2,3=hard).");
			}
		}

		Draughts game;
		public void setGame(Draughts g) { game = g; }

		private boolean[] skel;
		public void setDummyState(boolean[] s) { skel = s; }

		public int gameStart() {
			if (opt > 0) { int o = opt; opt = -1; return o; }
			for (;;) {
				try {
					byte[] in = new byte[8192];
					System.err.print("enter the game parameters [black][white], or nothing to exit: ");
					int s = System.in.read(in);
					if (s < 2) { System.exit(0); }
					int b = in[0] == 'H'? 0: 1 << (Byte.parseByte(new String(in, 0, 1))-1);
					int w = in[1] == 'H'? 0: 1 << (Byte.parseByte(new String(in, 1, 1))-1);
					return (b<<3|w);
				} catch (java.io.IOException e) {
					System.exit(1);
				} catch (NumberFormatException e) {
					System.err.println("Usage: [black][white]");
					System.err.println("H means a human player; a number means an AI player with that toughness (1=easy,2,3=hard).");
				}
			}
		}
		public void nextRound(boolean player, GameState gstate) {}
		public void gameOver(EndGame g) {
			switch (g) {
			case BLACK: System.out.println("black wins"); return;
			case WHITE: System.out.println("white wins"); return;
			case DRAW: System.out.println("draw"); return;
			}
		}
		public boolean[] scan() { return skel; }
		public EndTurn proceed(BoardState bstate) {
			boolean[] skel;
			try {
				byte[] in = new byte[8192];
				System.err.print("enter the move, or nothing for random: ");
				int s = System.in.read(in);
				int srcy = Byte.parseByte(new String(in, 0, 1), 16);
				int srcx = Byte.parseByte(new String(in, 1, 1), 16);
				int dsty = Byte.parseByte(new String(in, 3, 1), 16);
				int dstx = Byte.parseByte(new String(in, 4, 1), 16);
				skel = game.board.getStateSkel((byte)(srcy<<4|srcx), (byte)(dsty<<4|dstx));
			} catch (java.io.IOException e) {
				skel = doRandomTurn();
			} catch (NumberFormatException e) {
				skel = doRandomTurn();
			}
			setDummyState(skel);

			return EndTurn.NORMAL;
		}
		public void moveHead(int direction) {}
		public void magnetSwitch(boolean power) {}
		public void reset() {
			setDummyState(game.board.getStateSkel());
		}

		java.util.Random rdx = new java.util.Random();
		private boolean[] doRandomTurn() {
			// pick a random turn
			Turn k = null;
			int s = rdx.nextInt(game.board.getValidTurns().size());
			int i = 0;
			for (Turn t : game.board.getValidTurns()) {
				if (i++ == s) {
					k = t; break;
				}
			}
			return game.board.getStateSkel(k.src, k.dst);
		}



	}

}
