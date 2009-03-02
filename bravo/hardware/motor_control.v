
/*
This module is for general motor control. It is instantiated once for
each of the two motors used for the board.

Input: The module takes a direction(forward/backward), the number of
steps to be moved, and an activation signal.

Output: The module outputs the appropriate sequence on "state" to
drive the motors. It also pulses high a done signal when it has 
finished the inputted move.

The module works be creating an instance of both the move_forward and
move_backward modules and activating one depending on the inputted direction.
*/

`include "defines.v"

module motor_control(
	input clk,
	input go,
	input direction,
	input [11:0] steps,
	input boundary1,
	input boundary2,
	output reg [3:0] state,
	output reg done
	);
	
	// state is the signal to the motor though its 4 input wires
	reg [3:0] mystate = 4'b1100;
	
	wire [3:0] state_forward;
	wire [3:0] state_backward;
	
	// high when the motor are to move in that direction
	reg go_forward = 0;
	reg go_backward = 0;	
	
	// At every clock tick, check if this module
	// has been activated. If it has, then activate
	// either the moveForward or moveBackward module
	// and listen to that module's done signal.
	always@(posedge clk) begin
		//check if forwards movement is done
		if (!direction) done <= done_forward;
		//check if backwards movement is done
		else if (direction) done <= done_backward;
		if (go) begin
			state <= mystate;
			//to move forward
			if (!direction) begin
				go_forward <= 1;
				mystate <= state_forward;
			end
			//to move backwards
			else if (direction) begin
				go_backward <= 1;
				mystate <= state_backward;
			end
		end
		// no movement
		else begin
			go_forward <= 0;
			go_backward <= 0;
			state <= 0;
		end
	end
	
	move_forward mf(
		.clk(clk),
		.go(go_forward),
		.steps(steps),
		.old_state(mystate),
		.boundary(boundary1),
		.state(state_forward),
		.done(done_forward)
		);
		
	move_backward mb(
		.clk(clk),
		.go(go_backward),
		.steps(steps),
		.old_state(mystate),
		.boundary(boundary2),
		.state(state_backward),
		.done(done_backward)
		);
		
endmodule