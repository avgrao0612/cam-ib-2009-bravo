
module decoding(
	input clk,	
	input data_incoming,
	input [7:0] dataStream,
	input new_game,
	input user_turn_done,
	output [7:0] direction,
	output want_scan,
	output magnet_on,
	output magnet_off,
	output reset,
	output black_to_play,
	output white_to_play,
	output draw_offer,
	output black_wins,
	output white_wins,
	output draw,
	output normal_wait,
	output player_must_jump,
	output more_jumps_available,
	output unrecoverable_error,
	output horizontal_offset
	);
	
	reg [7:0] dir = 0;
	reg scan = 0;
	reg magnetOn = 0;
	reg magnetOff = 0;
	reg reset_signal = 0;
	reg black_turn = 0;
	reg white_turn = 0;
	reg draw_offered = 0;
	reg black_won = 0;
	reg white_won = 0;
	reg draw_game = 0;
	reg normal = 0;
	reg must_jump = 0;
	reg more_jumps = 0;
	reg error = 0;
	reg horiz_offset = 0;
	
	assign direction = dir;
	assign want_scan = scan;
	assign magnet_on = magnetOn;
	assign magnet_off = magnetOff;
	assign reset = reset_signal;
	assign black_to_play = black_turn;
	assign white_to_play = white_turn;
	assign draw_offer = draw_offered;
	assign black_wins = black_won;
	assign white_wins = white_won;
	assign draw = draw_game;
	assign normal_wait = normal;
	assign player_must_jump = must_jump;
	assign more_jumps_available = more_jumps;
	assign unrecoverable_error = error;
	assign horizontal_offset = horiz_offset;
	
	always@(posedge clk) begin
	if (data_incoming) begin
		//(M)otor control
		if (dataStream[7:6] == 2'b00) begin
			//move in (D)irection
			if (dataStream[2:0] == 3'b000) begin
				case (dataStream[5:3])
					3'd0: dir <= 8'b00000001;
					3'd1: dir <= 8'b00000010;
					3'd2: dir <= 8'b00000100;
					3'd3: dir <= 8'b00001000;
					3'd4: dir <= 8'b00010000;
					3'd5: dir <= 8'b00100000;
					3'd6: dir <= 8'b01000000;
					3'd7: dir <= 8'b10000000;
				endcase
			end
			//(R)eset to (0,0)
			else if (dataStream[5:0] == 6'b111111) reset_signal <= 1;
			//horizontal offset
			else if (dataStream[5:0] == 6'b111001) horiz_offset <= 1;
		end
		//ma(G)net control
		else if (dataStream[7:6] == 2'b01) begin
			if (dataStream[5]) magnetOn <= 1;
			else magnetOff <= 1;
		end
		//misc game (A)ctions
		else if (dataStream[7:6] == 2'b10) begin
			//(S)can
			if (dataStream[5:0] == 6'b111111) scan <= 1;
			//tur(N) start
			else if (dataStream[5:2] == 4'b1000) begin
				//blac(K) to play
				if (dataStream[1:0] == 2'b01) black_turn <= 1;
				//white(E) to play
				else if (dataStream[1:0] == 2'b10) white_turn <= 1;
			end
			//draw offered
			else if (dataStream[5:2] == 4'b1010) draw_offered <= 1;
			//game (O)ver
			else if (dataStream[5:2] == 4'b0000) begin
				//(B)lack wins
				if (dataStream[1:0] == 2'b01) black_won <= 1;
				//(W)hite wins
				else if (dataStream[1:0] == 2'b10) white_won <= 1;
				//(D)raw
				else if (dataStream[1:0] == 2'b11) draw_game <= 1;
			end
		end
		//re(Q)uest human response
		else if (dataStream[7:6] == 2'b11) begin
			//normal wait
			if (dataStream[5:0] == 6'b000000) normal <= 1;
			//player must jump
			else if (dataStream[5:0] == 6'b000001) must_jump <= 1;
			//more jumps available
			else if (dataStream[5:0] == 6'b000010) more_jumps <= 1;
			//unrecoverable error
			else if (dataStream[5:0] == 6'b111111) error <= 1;
		end
	end
	else begin
		dir <= 0;
		scan <= 0;
		magnetOn <= 0;
		magnetOff <= 0;
		reset_signal <= 0;
		horiz_offset <= 0;
		
		black_won <= 0;
		white_won <= 0;
		draw_game <= 0;
		draw_offered <= 0;
		white_turn <= 0;
		black_turn <= 0;
		normal <= 0;
		must_jump <= 0;
		more_jumps <= 0;
		error <= 0;
		draw_offered <= 0;
	end
	/*
	if (new_game) begin
		black_won <= 0;
		white_won <= 0;
		draw_game <= 0;
		draw_offered <= 0;
	end
	if (user_turn_done) begin
		white_turn <= 0;
		black_turn <= 0;
		normal <= 0;
		must_jump <= 0;
		more_jumps <= 0;
		error <= 0;
		draw_offered <= 0;
	end
	*/
	end
endmodule