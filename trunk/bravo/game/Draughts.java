package bravo.game;

// Main class that will be run and supplied command line arguments

public class Draughts {

	static Board GameBoard;

	public static void main(String[] args) {

		byte[] red = {0x77, 0x71, 0x66, 0x64, 0x44, 0x42, 0x24, 0x22, 0x62};
		byte[] black = {0x00, 0x02, 0x04, 0x06, 0x11, 0x13, 0x15, 0x17, 0x20, 0x26, 0x57, 0x55};
		GameBoard = new Board(black, red);
		Piece a = GameBoard.board[0x57];
		a.flags = -1;
		/*System.out.println(a.isDead()? "yes": "no");
		System.out.println(a.isKing()? "yes": "no");
		byte b = (byte) 0xFF;
		System.out.printf("0x%x\n", b);
		b = -1;
		System.out.printf("0x%x\n", b);
		b += 2;
		System.out.printf("0x%x\n", b);
		System.out.printf("0x%x\n", 1<<2|1);
		System.out.printf("0x%x\n", 3+5 >> 2);*/
		System.out.print(GameBoard);
		System.out.println("Available Moves:");
		java.util.HashSet<Turn> ts = GameBoard.getValidTurns();
		for (Turn t : ts) {
			System.out.println(t);
		}
		System.out.println(ts.contains(new Turn(GameBoard.board[0x13], true, new byte[]{0x13, 0x31, 0x53, 0x35, 0x13})));
		System.out.println(ts.contains(new Turn(GameBoard.board[0x13], true, new byte[]{0x13, 0x31, 0x53, 0x35, 0x14})));

	}

}


/*

 *
 *
 * only kings can move backwards
 *
 * move - diagonal 1
 * jump - diagonal 2
 *   multijump - diagonal 2, any direction by any piece
 *
 * king - if non-king lands on back row, turn ends
 *
 *
 * game ends when other player has no valid moves left (this includes no pieces)
 * ie. only need to check Board.getValidMoves().size() == 0
 *
 *
 */
