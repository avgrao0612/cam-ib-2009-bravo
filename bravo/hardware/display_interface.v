
/*
This module controls the output display part of our primitive user interface.
It will receive signals from Java (through the decoding module) about the
state of the game and set high the following bit on "display" which will then
be sent to the LCD display:

0: black to play
1: white to play
2: black wins
3: white wins
4: draw
5: normal wait
6: player must jump
7: more jumps available
8: unrecoverable error
9: draw offered
10: player did not move (even though "user turn done" button was pressed)

Additional registers are used for each state rather than simply assigning the
display bits directly to the signals from Java because the signals from Java will
only be pulsed high whereas we want the display to stay lit until we receive
some input from the user.
*/

module display_interface(
	input clk,
	input black_to_play,
	input white_to_play,
	input draw_offer,
	input black_wins,
	input white_wins,
	input draw_game,
	input normal_wait,
	input player_must_jump,
	input more_jumps_available,
	input unrecoverable_error,
	input did_not_move,
	input new_game,
	input user_turn_done,
	output [10:0] display
	);
	
	reg black_play = 0;
	reg white_play = 0;
	reg draw_offered = 0;
	reg black_win = 0;
	reg white_win = 0;
	reg draw = 0;
	reg normal = 0;
	reg must_jump = 0;
	reg more_jumps = 0;
	reg error = 0;
	reg no_move = 0;
	
	assign display[0] = black_play;
	assign display[1] = white_play;
	assign display[2] = black_win;
	assign display[3] = white_win;
	assign display[4] = draw;
	assign display[5] = normal;
	assign display[6] = must_jump;
	assign display[7] = more_jumps;
	assign display[8] = error;
	assign display[9] = draw_offered;
	assign display[10] = no_move;
	
	always@(posedge clk) begin
		//It is either black's or white's turn.
		if (black_to_play) begin
			black_play <= 1;
			white_play <= 0;
		end
		if (white_to_play) begin
			black_play <= 0;
			white_play <= 1;
		end
		//Computer offers a draw.
		if (draw_offer) draw_offered <= 1;
		//If we're starting a new game, reset all the "game over"
		//signals to 0.
		if (new_game) begin
			black_win <= 0;
			white_win <= 0;
			draw <= 0;
			draw_offered <= 0;
		end
		//Otherwise check if the game is over.
		else begin
			if (black_wins) black_win <= 1;
			else if (white_wins) white_win <= 1;
			else if (draw_game) draw <= 1;
		end
		//The user has done something. Set all related signals
		//back to 0 and continue the game.
		if (user_turn_done) begin
			normal <= 0;
			must_jump <= 0;
			more_jumps <= 0;
			error <= 0;
			no_move <= 0;
			draw_offered <= 0;
		end
		//If the user hasn't responded yet, alert the user
		//of the state of the game, specifically problems with the
		//game state (e.g. player forgot to take a piece).
		else begin
			if (normal_wait) normal <= 1;
			if (player_must_jump) must_jump <= 1;
			if (more_jumps_available) more_jumps <= 1;
			if (unrecoverable_error) error <= 1;
			if (did_not_move) no_move <= 1;
		end
	end
	
endmodule