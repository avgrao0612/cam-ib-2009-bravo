
/*
This module is exactly the same as move_forward except that the states
in the case statement below have been reversed (so they will drive the
motor in the opposite direction). See move_forward for details on how
the module works.
*/

`include "defines.v"

module move_backward(
	input clk,
	input go,
	input [11:0] steps,
	input [3:0] old_state,
	input boundary,
	output [3:0] state,
	output reg done
	);
	
	// state is the signal to the motor though its 4 input wires
	reg [3:0] mystate = 4'b1100;
	assign state = mystate;
	
	//counter for speed, counts the clock cycles between changing pulses
	reg [23:0] counter = 0;
	
	//counter for steps done so far
	reg [11:0] mysteps = 0;
	
	always@(posedge clk) begin
	// on the edge, can't move any further in that direction so stop
		if (go && boundary) done <= 1;
		else if (go && !done) begin
			if (mysteps	< steps) begin
			// count up clock cycles since last step
				counter <= counter+1;
				if (counter == `speed) begin
					mysteps <= mysteps+1;
					// change state to move a step and increase step counter
					case (mystate)
						4'b1100: mystate <= 4'b1001;
						4'b1001: mystate <= 4'b0011;
						4'b0011: mystate <= 4'b0110;
						4'b0110: mystate <= 4'b1100;
					endcase
					// reset counter because a step was taken
					counter <= 0;					
				end
			end
			// number of steps reached
			else done <= 1;
		end
		// if not moving or done reset registers
		else if (!go) begin
			done <= 0;
			mysteps <= 0;
			mystate <= old_state;
		end
	end
endmodule