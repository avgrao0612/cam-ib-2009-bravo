
// This module is exactly the same as moveForward except
// that the states in the case statement below have been
// reversed (so they will drive the motor in the opposite
// direction).

`include "defines.v"

module moveBackward(
	input clk,
	input go,
	input [11:0] steps,
	input [3:0] old_state,
	input boundary,
	output [3:0] state,
	output done
	);
	
	reg [3:0] mystate = 4'b1100;
	assign state = mystate;
	
	reg [23:0] counter = 0;
	
	reg [23:0] speed = `speed;
	
	reg [11:0] mysteps = 0;
	
	reg im_done = 0;
	assign done = im_done;
	
	always@(posedge clk) begin
	if (go && boundary) im_done <= 1;
	else if (go && !im_done) begin
		if (mysteps	< steps) begin
			counter <= counter+1;
			if (counter == speed) begin
				mysteps <= mysteps+1;
				case (mystate)
					4'b1100: mystate <= 4'b1001;
					4'b1001: mystate <= 4'b0011;
					4'b0011: mystate <= 4'b0110;
					4'b0110: mystate <= 4'b1100;
				endcase
				counter <= 0;
			end
		end
		else im_done <= 1;
	end
	else if (!go) begin
		im_done <= 0;
		mysteps <= 0;
		mystate <= old_state;
	end
	end
endmodule