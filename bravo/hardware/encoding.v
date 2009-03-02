
/*
This module encodes data from the board and user inputs so that they can be sent
through the serial bus via the async_transmitter module. It listens to output
signals from all of the relevant modules and whenever one of them goes high,
it encodes that signal into dataStream (which is connected to the transmitter)
and sets data_start high to signal that valid data is being transmitted.
Note that sometimes unwanted signals may be sent (for example, "movement done"
signals are sent in between each row of scan data), but these are filtered out
by the Java receiver.
*/

module encoding(
	input clk,
	input user_turn_done,
	input movement_done,
	input reset_done,
	input offset_done,
	input [7:0] input_stream,
	input sending_scan_left,
	input sending_scan_right,
	input resign,
	input draw,
	input [4:0] pieces,
	input new_game,
	input [2:0] black_setting,
	input [2:0] white_setting,
	output reg [7:0] dataStream,
	output reg data_start
	);
	
	always@(posedge clk) begin
	//human (R)esponse
		//(T)urn done (implies decline draw)
		if (user_turn_done) begin
			data_start <= 1;
			dataStream <= 8'b00000000;
		end
		//offer (D)raw, or accept draw
		else if (draw) begin
			data_start <= 1;
			dataStream <= 8'b00010000;
		end
		//r(E)sign
		else if (resign) begin
			data_start <= 1;
			dataStream <= 8'b00100000;
		end
		//reset done
		else if (reset_done) begin
			data_start <= 1;
			dataStream <= 8'b01111111;
		end
		//offset move done
		else if (offset_done) begin
			data_start <= 1;
			dataStream <= 8'b01111001;
		end
	//(M)ovement complete
	//The dataStream[5:3] bits will indicate which direction the
	//move was in.
		else if (movement_done) begin
			data_start <= 1;
			dataStream[7:6] <= 2'b01;
			dataStream[5:3] <= input_stream[5:3];
			dataStream[2:0] <= 3'b000;
		end
	//(S)can data
		//left side of the row (5 squares)
		else if (sending_scan_left) begin
			data_start <= 1;
			dataStream[7:5] <= 3'b101;		//LSB is a 1 for left side
			dataStream[4:0] <= pieces;
		end
		//right side of the row (5 squares)
		else if (sending_scan_right) begin
			data_start <= 1;
			dataStream[7:5] <= 3'b100;		//LSB is a 0 for right side
			dataStream[4:0] <= pieces;
		end
	//(N)ew game
	//The setting values are:
	// 0: human
	// 1: easy AI
	// 2: normal AI
	// 3: hard AI
		else if (new_game) begin
			data_start <= 1;
			dataStream[7:6] <= 2'b11;
			dataStream[5:3] <= black_setting;
			dataStream[2:0] <= white_setting;
		end
		else data_start <= 0;
	end
endmodule