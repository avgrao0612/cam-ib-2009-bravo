
/*
This module decodes the signals sent from Java following our decoding/encoding
scheme. It receives the 8-bit data stream from the async_receiver module along
with a "data incoming" signal, and converts the data stream into the
appropriate signal (e.g. magnet_on, reset, etc) whenever data_incoming is high.
*/

module decoding(
	input clk,	
	input data_incoming,
	input [7:0] dataStream,
	input new_game,
	input user_turn_done,
	output reg [7:0] direction,
	output reg want_scan,
	output reg magnet_on,
	output reg magnet_off,
	output reg reset,
	output reg black_to_play,
	output reg white_to_play,
	output reg draw_offer,
	output reg black_wins,
	output reg white_wins,
	output reg draw,
	output reg normal_wait,
	output reg player_must_jump,
	output reg more_jumps_available,
	output reg unrecoverable_error,
	output reg did_not_move,
	output reg horizontal_offset
	);
	
	reg old_state = 0;
	reg data_start = 0;

	always@(posedge clk) begin
		//Do not allow the data_incoming signal to be held high. This
		//prevents unintentionally receiving multiples of the same signal.
		if ((data_incoming != old_state) && data_incoming) begin
			old_state <= 1;
			data_start <= 1;
		end
		else if ((data_incoming != old_state) && (!data_incoming)) old_state <= 0;
		else data_start <= 0;
		
		//Receiving data. Decode following scheme.
		if (data_start) begin
			//(M)otor control
			if (dataStream[7:6] == 2'b00) begin
				//move in (D)irection
				if (dataStream[2:0] == 3'b000) begin
					case (dataStream[5:3])
						//The directions go clockwise starting with north
						//at 0 and ending with northwest at 7.
						3'd0: direction <= 8'b00000001; //north
						3'd1: direction <= 8'b00000010; //northeast
						3'd2: direction <= 8'b00000100; //east
						3'd3: direction <= 8'b00001000; //southeast
						3'd4: direction <= 8'b00010000; //south
						3'd5: direction <= 8'b00100000; //southwest
						3'd6: direction <= 8'b01000000; //west
						3'd7: direction <= 8'b10000000; //northwest
					endcase
				end
				//(R)eset to (0,0)
				else if (dataStream[5:0] == 6'b111111) reset <= 1;
				//horizontal offset
				else if (dataStream[5:0] == 6'b111001) horizontal_offset <= 1;
			end
			//ma(G)net control
			else if (dataStream[7:6] == 2'b01) begin
				if (dataStream[5]) magnet_on <= 1;
				else if (!dataStream[5]) magnet_off <= 1;
			end
			//misc game (A)ctions
			else if (dataStream[7:6] == 2'b10) begin
				//(S)can
				if (dataStream[5:0] == 6'b111111) want_scan <= 1;
				//tur(N) start
				else if (dataStream[5:2] == 4'b1000) begin
					//blac(K) to play
					if (dataStream[1:0] == 2'b01) black_to_play <= 1;
					//white(E) to play
					else if (dataStream[1:0] == 2'b10) white_to_play <= 1;
				end
				//draw offered
				else if (dataStream[5:2] == 4'b1010) draw_offer <= 1;
				//game (O)ver
				else if (dataStream[5:2] == 4'b0000) begin
					//(B)lack wins
					if (dataStream[1:0] == 2'b01) black_wins <= 1;
					//(W)hite wins
					else if (dataStream[1:0] == 2'b10) white_wins <= 1;
					//(D)raw
					else if (dataStream[1:0] == 2'b11) draw <= 1;
				end
			end
			//re(Q)uest human response
			else if (dataStream[7:6] == 2'b11) begin
				//normal wait
				if (dataStream[5:0] == 6'b000000) normal_wait <= 1;
				//player must jump
				else if (dataStream[5:0] == 6'b000001) player_must_jump <= 1;
				//more jumps available
				else if (dataStream[5:0] == 6'b000010) more_jumps_available <= 1;
				//unrecoverable error
				else if (dataStream[5:0] == 6'b111111) unrecoverable_error <= 1;
				//player did not move, please move
				else if (dataStream[5:0] == 6'b000011) did_not_move <= 1;
			end
		end
		//When not receiving, reset all signals to 0.
		else begin
			direction <= 0;
			want_scan <= 0;
			magnet_on <= 0;
			magnet_off <= 0;
			reset <= 0;
			horizontal_offset <= 0;
			black_wins <= 0;
			white_wins <= 0;
			draw <= 0;
			white_to_play <= 0;
			black_to_play <= 0;
			draw_offer <= 0;
			normal_wait <= 0;
			player_must_jump <= 0;
			more_jumps_available <= 0;
			unrecoverable_error <= 0;
			did_not_move <= 0;
		end
	end
endmodule