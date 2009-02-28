
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
	input new_game,
	input user_turn_done,
	output [9:0] LEDR
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
	
	assign LEDR[0] = black_play;
	assign LEDR[1] = white_play;
	assign LEDR[2] = black_win;
	assign LEDR[3] = white_win;
	assign LEDR[4] = draw;
	assign LEDR[5] = normal;
	assign LEDR[6] = must_jump;
	assign LEDR[7] = more_jumps;
	assign LEDR[8] = error;
	assign LEDR[9] = draw_offered;
	
	always@(posedge clk) begin
		if (black_to_play) begin
			black_play <= 1;
			white_play <= 0;
		end
		if (white_to_play) begin
			black_play <= 0;
			white_play <= 1;
		end
		if (draw_offer) draw_offered <= 1;
		if (new_game) begin
			black_win <= 0;
			white_win <= 0;
			draw <= 0;
			draw_offered <= 0;
		end
		else begin
			if (black_wins) black_win <= 1;
			else if (white_wins) white_win <= 1;
			else if (draw_game) draw <= 1;
		end
		if (user_turn_done) begin
			normal <= 0;
			must_jump <= 0;
			more_jumps <= 0;
			error <= 0;
			draw_offered <= 0;
		end
		else begin
			if (normal_wait) normal <= 1;
			if (player_must_jump) must_jump <= 1;
			if (more_jumps_available) more_jumps <= 1;
			if (unrecoverable_error) error <= 1;
		end
	end
	
endmodule