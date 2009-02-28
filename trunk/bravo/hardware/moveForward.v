
// This module is used to make a motor move forward.
// When activated, it rotates the motor by the inputted
// number of steps. It outputs the appropriate sequence
// in 'state' to drive the motor in the correct direction.
//
// Input: activation signal and number of steps.
//
// Output: signals to drive motors held in 'state'. 'Done'
// signal pulsed high when move is finished.

`include "defines.v"

module moveForward(
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
	
	// This code first checks to see if it has been activated.
	// It then uses 2 counters, one to control the motor speed
	// and the other to control the number of steps it takes.
	// If all conditions are met (module activated, move not
	// yet finished, correct number of clock ticks passed)
	// then it will update 'state' to the next signal required
	// to drive the motors.
	always@(posedge clk) begin
	if (go && boundary) im_done <= 1;
	else if (go && !im_done) begin
		if (mysteps	< steps) begin
			counter <= counter+1;
			// Because the system clock is much faster than what
			// the motor can handle, 'counter' is used to slow it
			// down. Essentially, we have created a new clock with
			// frequency = clk/speed
			if (counter == speed) begin
				mysteps <= mysteps+1;
				case (mystate)
					4'b1100: mystate <= 4'b0110;
					4'b0110: mystate <= 4'b0011;
					4'b0011: mystate <= 4'b1001;
					4'b1001: mystate <= 4'b1100;
				endcase
				counter <= 0;
			end
		end
		else im_done <= 1;
	end
	// Reset registers once module is deactivated.
	else if (!go) begin
		im_done <= 0;
		mysteps <= 0;
		mystate <= old_state;
	end
	end
endmodule