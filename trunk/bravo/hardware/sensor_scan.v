
`include "defines.v"

module sensor_scan(
	input clk,
	input start_scan,
	input [9:0] scan_data,
	input scan_move_done,
	input TxD_busy,
	output sending_scan_left,
	output sending_scan_right,
	output [4:0] pieces,
	output scan_offset_move,
	output scan_move,
	
	output [7:0] LEDG
	);
	
	assign LEDG = position;
	
	reg scanning = 0;
	reg done = 0;
	reg offset_move = 0;
	reg move = 0;
	reg [4:0] position = 0;
	reg begun_move = 0;

	reg sending_left = 0;
	reg sending_right = 0;
	reg sending_move = 0;
	reg [4:0] values;
	
	reg sent_left = 0;
	reg sent_right = 0;
	reg sent_move = 0;
	
	assign scan_offset_move = offset_move;
	assign scan_move = move;
	assign sending_scan_left = sending_left;
	assign sending_scan_right = sending_right;
	assign pieces = values;
	
	always@(posedge clk) begin
		if (move) move <= 0;
		if (offset_move) offset_move <= 0;
		if (begun_move) begin
			// if we've begun a move, wait until we receive scan_move_done
			if (scan_move_done) begin
				begun_move <= 0;
				sending_move <= 1;
			end
		end
		else if (TxD_busy) begin
			// we are sending something
			if (sending_move) begin
				sending_move <= 0;
				sent_move <= 1;
				position <= position+1;
			end
			else if (sending_left) begin
				sending_left <= 0;
				sent_left <= 1;
			end
			else if (sending_right) begin
				sending_right <= 0;
				sent_right <= 1;
			end
		end
		// otherwise we are free to proceed
		else if (start_scan) begin
			scanning <= 1;
			position <= 5'd0;
		end
		else if (scanning) begin
			if (position == 0) begin
				offset_move <= 1;
				begun_move <= 1;
			end
			else if (sent_move) begin
				sent_move <= 0;
				sending_left <= 1;
				values[0] <= scan_data[0];
				values[1] <= scan_data[1];
				values[2] <= scan_data[2];
				values[3] <= scan_data[3];
				values[4] <= scan_data[4];
			end
			else if (sent_left) begin
				sent_left <= 0;
				sending_right <= 1;
				values[0] <= scan_data[5];
				values[1] <= scan_data[6];
				values[2] <= scan_data[7];
				values[3] <= scan_data[8];
				values[4] <= scan_data[9];
			end
			else if (sent_right) begin
				sent_right <= 0;
				if (position == 5'd10) scanning <= 0;
				else begin
					move <= 1;
					begun_move <= 1;
				end
			end
		end
	end
	
endmodule
