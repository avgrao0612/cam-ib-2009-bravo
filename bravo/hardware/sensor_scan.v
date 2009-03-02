
/*
This module controls the scan of all the pieces on the board. All
functions related to the scan are implemented internally here, so
Java simply needs to activate this module, and the module will take
care of the offset move, moving between each row, and handling the
scan data. The module does, however, assume that the electromagnet
starts at position (0,0), meaning a reset must be called before
requesting a scan.
*/

`include "defines.v"

module sensor_scan(
	input clk,
	input start_scan,
	input [9:0] scan_data,
	input scan_move_done,
	input TxD_busy,
	output reg sending_scan_left,
	output reg sending_scan_right,
	output reg [4:0] pieces,
	output reg scan_offset_move,
	output reg scan_move
	);
	
	reg scanning = 0;
	reg done = 0;
	// counter for the row currently being scanned
	reg [4:0] position = 0;
	reg begun_move = 0;
	reg sending_move = 0;
	// goes high once that part of the scan is finished and sent to Java
	reg sent_left = 0;
	reg sent_right = 0;
	reg sent_move = 0;
	
	always@(posedge clk) begin
		//Set move requests back to 0 if they are high. This ensures that
		//they are pulsed high for only one clock cycle as required by the
		//motor control modules.
		if (scan_move) scan_move <= 0;
		if (scan_offset_move) scan_offset_move <= 0;
		
		//If we have requested a move, wait until it is finished.
		if (begun_move) begin
			if (scan_move_done) begin
				begun_move <= 0;
				sending_move <= 1;
			end
		end
		
		//If we are transmitting data to Java...
		else if (TxD_busy) begin
			//If we are transmitting a movement complete signal, we have
			//completed a move, so increment our position, stop
			//transmitting, and indicate that it has been sent.
			if (sending_move) begin
				sending_move <= 0;
				sent_move <= 1;
				position <= position+1;
			end
			//If we are transmitting the scan data for the left side of a
			//row then stop transmitting and indicate that it has been sent.
			else if (sending_scan_left) begin
				sending_scan_left <= 0;
				sent_left <= 1;
			end
			//Similarly for right side.
			else if (sending_scan_right) begin
				sending_scan_right <= 0;
				sent_right <= 1;
			end
		end
		//Otherwise (not waiting for move to complete and not transmitting
		//data), we are free to proceed.
		
		//Scan requested. Start the scan.
		else if (start_scan) begin
			scanning <= 1;
			position <= 5'd0;
		end
		//We are in the middle of scanning.
		else if (scanning) begin
			//Do an offset move to align the sensors to the middle of the squares at the start
			if (position == 0) begin
				scan_offset_move <= 1;
				begun_move <= 1;
			end
			//Scan the left side.
			else if (sent_move) begin
				sent_move <= 0;
				sending_scan_left <= 1;
				pieces[0] <= scan_data[0];
				pieces[1] <= scan_data[1];
				pieces[2] <= scan_data[2];
				pieces[3] <= scan_data[3];
				pieces[4] <= scan_data[4];
			end
			//Scan the right side.
			else if (sent_left) begin
				sent_left <= 0;
				sending_scan_right <= 1;
				pieces[0] <= scan_data[5];
				pieces[1] <= scan_data[6];
				pieces[2] <= scan_data[7];
				pieces[3] <= scan_data[8];
				pieces[4] <= scan_data[9];
			end
			//Row scanned finished. Move to the next row and repeat scanning process.
			else if (sent_right) begin
				sent_right <= 0;
				// at the end, so finish the scan
				if (position == 5'd10) scanning <= 0;
				else begin
					scan_move <= 1;
					begun_move <= 1;
				end
			end
		end
	end
	
endmodule
