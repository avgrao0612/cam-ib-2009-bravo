
/*
This module creates two instances of the motor_control module, one for the 
main motor driving the bar and one for the motor controlling the electromagnet.
It activates these motors based on signals from Java.

Input: There is an 8-bit input to signal the direction that the motors should go
(north, south, etc) for a normal move. There is also seperate inputs for a scan 
offset move (to align the sensors) and a horizontal offset move (to align the
electromagnet). Each of these should pulse high once to signal the desired move and
then stay low until the 'done' signal is sent from this module. Then the next
movement request signal may be sent. The direction assignments for the 8-bit
normal move are detailed in the decoding module.

Output: The module outputs signals to GPIO_0 which controls the motors' movements.
There are also three seperate done signals to indicate whether the module has
completed a normal move, a scan offset move, or a horizontal offset move.

Parameters: can be controlled via the defines.v file. These include pin numbers 
for motor control, speed of the motor, and the number of motor steps required to
move in a certain direction.
*/

`include "defines.v"

module motor_main(
	input clk,
	input [7:0] direction,
	input scan_offset_move,
	input scan_move,
	input horizontal_offset,
	input boundary_main_1,
	input boundary_main_2,
	input boundary_magnet_1,
	input boundary_magnet_2,
	input reset,
	inout [35:0] GPIO_0,
	output reg movement_done,
	output reg reset_done,
	output reg offset_done
	);	
	
	// if a movement is called, only the wire for the correct direction will be high
	wire wire_N = direction[0];
	wire wire_NE = direction[1];
	wire wire_E = direction[2];
	wire wire_SE = direction[3];
	wire wire_S = direction[4];
	wire wire_SW = direction[5];
	wire wire_W = direction[6];
	wire wire_NW = direction[7];
	
	// the signal to the motors to get them to rotate
	wire [3:0] state_main;
	wire [3:0] state_magnet;
	
	// signal the motor to move or not
	reg go_main = 0;
	reg go_magnet = 0;
	
	// the number of steps a motor is to move
	reg [11:0] steps_main;
	reg [11:0] steps_magnet;
	
	// signals which direction the motor is to move
	// 1 for main is south, 0 north
	// 1 for magnet is east, 0 west
	reg dir_main;
	reg dir_magnet;
	
	reg resetting = 0;
	reg offsetting = 0;
	
	
	//tanslated signal to the stepper motors
	assign GPIO_0[`red_main] = state_main[3];
	assign GPIO_0[`blue_main] = state_main[2];
	assign GPIO_0[`yellow_main] = state_main[1];
	assign GPIO_0[`orange_main] = state_main[0];
	
	assign GPIO_0[`red_magnet] = state_magnet[3];
	assign GPIO_0[`blue_magnet] = state_magnet[2];
	assign GPIO_0[`yellow_magnet] = state_magnet[1];
	assign GPIO_0[`orange_magnet] = state_magnet[0];
	
	// This is the important part of the module. At every
	// clock tick, the module first checks if the motors are
	// finished moving. If they are, then it deactivates the 
	// motors and waits for the next input signal. Otherwise,
	// it activates one or both of the motors depending on what
	// direction is specified (for example, N requires only one
	// motor whereas NE requires both). These compass directions
	// are converted into forward/backward directions for each
	// motor.
	always@(posedge clk) begin
		//Assign the done signals according to what type of move
		//has been requested.
		if (resetting) reset_done <= (done_main && done_magnet);
		else if (offsetting) offset_done <= done_magnet;
		else if (go_main && go_magnet) movement_done <= (done_main && done_magnet);
		else if (go_main) movement_done <= done_main;
		else if (go_magnet) movement_done <= done_magnet;
		
		//If we are no longer moving, reset all done signals to 0.
		if (!go_main && !go_magnet) begin
			movement_done <= 0;
			reset_done <= 0;
			offset_done <= 0;
		end
		//If we are done, deactivate the motors.
		if (movement_done || reset_done || offset_done) begin
			go_main <= 0;
			go_magnet <= 0;
			resetting <= 0;
			offsetting <= 0;
		end
		//Otherwise, activate the motors based on what type of move has
		//been requested:
		
		//Reset the position of the electromagnet to (0,0),
		//which is the northwest corner of the board.
		else if (reset) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 0;
			dir_magnet <= 0;
			steps_main <= 12'b1111_1111_1111;
			steps_magnet <= 12'b1111_1111_1111;
			resetting <= 1;
		end
		//scan offset move (aligns sensors with centers
		//of squares)
		else if (scan_offset_move) begin
			go_main <= 1;
			dir_main <= 1;
			steps_main <= `scan_offset_steps;
		end
		//normal scan move (essentially a "move south")
		else if (scan_move) begin
			go_main <= 1;
			dir_main <= 1;
			steps_main <= `straight_steps;
		end
		//horizontal offset move (aligns electromagnet with
		//center of squares)
		else if (horizontal_offset) begin
			go_magnet <= 1;
			dir_magnet <= 1;
			steps_magnet <= `horizontal_offset_steps;
			offsetting <= 1;
		end
		//move north
		else if (wire_N) begin
			go_main <= 1;
			dir_main <= 0;
			steps_main <= `straight_steps;
		end
		//move south
		else if (wire_S) begin
			go_main <= 1;
			dir_main <= 1;
			steps_main <= `straight_steps;
		end
		//move west
		else if (wire_W) begin
			go_magnet <= 1;
			dir_magnet <= 0;
			steps_magnet <= `straight_steps;
		end
		//move east
		else if (wire_E) begin
			go_magnet <= 1;
			dir_magnet <= 1;
			steps_magnet <= `straight_steps;
		end
		//move northwest
		else if (wire_NW) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 0;
			dir_magnet <= 0;
			steps_main <= `straight_steps;
			steps_magnet <= `straight_steps;
		end
		//move southwest
		else if (wire_SW) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 1;
			dir_magnet <= 0;
			steps_main <= `straight_steps;
			steps_magnet <= `straight_steps;
		end
		//move southeast
		else if (wire_SE) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 1;
			dir_magnet <= 1;
			steps_main <= `straight_steps;
			steps_magnet <= `straight_steps;
		end
		//move northeast
		else if (wire_NE) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 0;
			dir_magnet <= 1;
			steps_main <= `straight_steps;
			steps_magnet <= `straight_steps;
		end
	end
	
	motor_control mainMotor(
		.clk(clk),
		.go(go_main),
		.direction(dir_main),
		.steps(steps_main),
		.boundary1(boundary_main_1),
		.boundary2(boundary_main_2),
		.state(state_main),
		.done(done_main)
	);
	
	motor_control magnetMotor(
		.clk(clk),
		.go(go_magnet),
		.direction(dir_magnet),
		.steps(steps_magnet),
		.boundary1(boundary_magnet_1),
		.boundary2(boundary_magnet_2),
		.state(state_magnet),
		.done(done_magnet)
	);
	
endmodule