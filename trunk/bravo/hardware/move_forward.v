
/*
This module is used to make a motor move forward. When activated, it rotates
the motor by the inputted number of steps. It outputs the appropriate sequence
in 'state' to drive the motor in the correct direction.

Input: activation signal and number of steps.

Output: signals to drive motors held in 'state'. 'Done' signal pulsed high
when move is finished.
*/

`include "defines.v"

module move_forward(
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
	
	// This code first checks to see if it has been activated.
	// It then uses 2 counters, one to control the motor speed
	// and the other to control the number of steps it takes.
	// If all conditions are met (module activated, move not
	// yet finished, correct number of clock ticks passed)
	// then it will update 'state' to the next signal required
	// to drive the motors.
	always@(posedge clk) begin
	// on the edge, can't move any further in that direction so stop
		if (go && boundary) done <= 1;
		else if (go && !done) begin
			if (mysteps	< steps) begin
				counter <= counter+1;
				// Because the system clock is much faster than what
				// the motor can handle, 'counter' is used to slow it
				// down. Essentially, we have created a new clock with
				// frequency = clk/speed
				if (counter == `speed) begin
					mysteps <= mysteps+1;
					case (mystate)
						//This sequence of states drives the motor
						//in the correct direction. This was given
						//in the specs of the stepper motors.
						4'b1100: mystate <= 4'b0110;
						4'b0110: mystate <= 4'b0011;
						4'b0011: mystate <= 4'b1001;
						4'b1001: mystate <= 4'b1100;
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