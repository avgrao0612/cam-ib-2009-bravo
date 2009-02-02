package bravo.game;

// Main class that will be run and supplied command line arguments

public class Draughts {


	static Board GameBoard;

	public static void main(String[] args) {

		GameBoard = new Board();
		Piece a = GameBoard.board[0];
		a.flags = -1;
		System.out.println(a.isDead()? "yes": "no");
		System.out.println(a.isKing()? "yes": "no");
		byte b = (byte) 0xFF;
		System.out.printf("0x%x\n", b);
		b = -1;
		System.out.printf("0x%x\n", b);
		b += 2;
		System.out.printf("0x%x\n", b);

		System.out.printf("0x%x\n", (1 << 1) + 1);
		System.out.print(GameBoard);


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
 * king - non-king landing on back row, turn ends
 *
 *
 * game ends when other player has no valid moves left (this includes no pieces)
 * ie. only need to check Board.getValidMoves().size == 0
 *
 *
 */
