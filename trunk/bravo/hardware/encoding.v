
module encoding(
	input clk,
	input user_turn_done,
	input movement_done,
	input sending_scan_left,
	input sending_scan_right,
	input resign,
	input draw,
	input [4:0] pieces,
	input new_game,
	input [2:0] black_setting,
	input [2:0] white_setting,
	output [7:0] dataStream,
	output data_start,
	output [7:0] LEDG,
	output LEDR
	);
	
	assign LEDG = data;
	assign LEDR = start;
	//assign LEDG[7] = new_game;
	
	reg [7:0] data = 0;
	assign dataStream = data;
	
	reg start = 0;
	assign data_start = start;
	
	always@(posedge clk) begin
	//human (R)esponse
		//(T)urn done (implies decline draw)
		if (user_turn_done) begin
			start <= 1;
			data <= 8'b00000000;
		end
		//offer (D)raw, or accept draw
		else if (draw) begin
			start <= 1;
			data <= 8'b00010000;
		end
		//r(E)sign
		else if (resign) begin
			start <= 1;
			data <= 8'b00100000;
		end
	//(M)ovement complete
		else if (movement_done) begin
			start <= 1;
			data <= 8'b01000000;
		end
	//(S)can data
		//left side
		else if (sending_scan_left) begin
			start <= 1;
			data[7:5] <= 3'b101;		//LSB is a 1 for left side
			data[4:0] <= pieces;
		end
		//right side
		else if (sending_scan_right) begin
			start <= 1;
			data[7:5] <= 3'b100;		//LSB is a 0 for right side
			data[4:0] <= pieces;
		end
	//(N)ew game
		else if (new_game) begin
			start <= 1;
			data[7:6] <= 2'b11;
			data[5:3] <= black_setting;
			data[2:0] <= white_setting;
		end
		else start <= 0;
	end
endmodule