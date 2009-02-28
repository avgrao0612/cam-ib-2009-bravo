
// This module is for general motor control. It is
// instantiated once for each of the two motors used
// for the board.
//
// Input: The module takes a direction(forward/backward),
// the number of steps to be moved, and an activation 
// signal.
//
// Output: The module outputs the appropriate sequence
// on state to drive the motors. It also pulses high
// a done signal when it has finished the inputted move.
//
// The module works be creating an instance of both the
// moveForward and moveBackward modules and activating
// one depending on the inputted direction.
//
// Note: after numerous modifications to this code, it
// seems that this module might be a bit superfluous and
// could potentially be moved mostly into the main module,
// but I have left it as it is for simplicity's sake.

`include "defines.v"

module motorControl(
	input clk,
	input go,
	input direction,
	input [11:0] steps,
	input boundary1,
	input boundary2,
	output [3:0] state,
	output done
	);
	
	reg [3:0] mystate = 4'b1100;
	
	reg im_done = 0;
	assign done = im_done;
	
	wire [3:0] state_forward;
	wire [3:0] state_backward;
	
	reg go_forward = 0;
	reg go_backward = 0;
	
	reg [3:0] output_state;
	assign state = output_state;
	
	
	// At every clock tick, check if this module
	// has been activated. If it has, then activate
	// either the moveForward or moveBackward module
	// and listen to that modules done signal.
	always@(posedge clk) begin
	if (!direction) im_done <= done_forward;
	else if (direction) im_done <= done_backward;
	if (go) begin
		output_state <= mystate;
		if (!direction) begin
			go_forward <= 1;
			mystate <= state_forward;
		end
		else if (direction) begin
			go_backward <= 1;
			mystate <= state_backward;
		end
	end
	else begin
		go_forward <= 0;
		go_backward <= 0;
		output_state <= 0;
	end
	end
	
	moveForward mf(
		.clk(clk),
		.go(go_forward),
		.steps(steps),
		.old_state(mystate),
		.boundary(boundary1),
		.state(state_forward),
		.done(done_forward)
		);
		
	moveBackward mb(
		.clk(clk),
		.go(go_backward),
		.steps(steps),
		.old_state(mystate),
		.boundary(boundary2),
		.state(state_backward),
		.done(done_backward)
		);
		
endmodule