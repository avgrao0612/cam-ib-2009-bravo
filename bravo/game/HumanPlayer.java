package bravo.game;

import bravo.game.Draughts.*;
import bravo.game.Board.*;
import bravo.io.HWInterface;
import java.util.Random;

// a class that gets human input and interfaces this with the game class

public class HumanPlayer extends Player {

	public HumanPlayer() { }

	public EndTurn doTurn(GameState state) {
/*
		BoardState bs = BoardState.NORMAL;
		EndTurn resp;
		do {
			resp = game.hwi.proceed(bs);
			if (resp != EndTurn.NORMAL) { return resp; }
			bs = game.board.applyBoardState(game.hwi.scan());
		} while (bs != BoardState.NORMAL);*/

		
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
		game.board.applyBoardState(skel);
		
		return EndTurn.NORMAL;
	}

	Random rdx = new Random();
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
