
// This module creates two instances of the motorControl
// module, one for the main motor driving the bar and
// one for the motor controlling the electromagnet. It
// activates these motors based on signals from the Java
// interface.
// 
// Input: It is assumed that there will be 8 wires (or
// equivalent) to signal the direction that the motors
// should go (north, south, etc). These should pulse high
// once for the desired direction and then stay low until
// the 'done' signal is sent from this module. Then the
// next direction signal may be sent.
//
// Output: The module outputs signals to GPIO_1 which
// controls the motors' movements. Also, there is a 'done'
// signal that will be pulsed high when a move has been
// completed.
//
// Parameters: can be controlled via the defines.v file.
// These include pin numbers for motor control, speed of the
// motor, and the number of motor steps required to move in
// a certain direction.
//
// Note: for testing purposes, the direction input wires
// from the Java interface have instead been connected to
// the SW switches on the altera board. (see comments
// in the code below for where to change)
//
// To do: the parameters in defines.v have been more or less 
// arbitrarily chosen at the moment (except for the 'main' pins).
// These values will depend on the size of the physical board.

`include "defines.v"

module motorMain(
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
	inout [35:0] GPIO_1,
	output done,
	output reg reset_done,
	output reg offset_done,
	
	output [17:0] LEDR,
	output [7:0] LEDG
	);
	
	assign LEDR[11:0] = steps_main;
	assign LEDG[0] = dir_main;
	assign LEDG[1] = dir_magnet;
	assign LEDG[2] = done_main;
	assign LEDG[3] = done_magnet;
	
	/*
	assign LEDR[17] = state_main[3];
	assign LEDR[16] = state_main[2];
	assign LEDR[15] = state_main[1];
	assign LEDR[14] = state_main[0];
	
	assign LEDR[13] = state_magnet[3];
	assign LEDR[12] = state_magnet[2];
	assign LEDR[11] = state_magnet[1];
	assign LEDR[10] = state_magnet[0];
	
	assign LEDR[0] = go_main;
	assign LEDR[1] = go_magnet;
	assign LEDR[2] = done_main;
	assign LEDR[3] = done_magnet;
	assign LEDR[4] = im_done;
	
	assign LEDR[6] = boundary_main_1;
	assign LEDR[7] = boundary_main_2;
	assign LEDR[8] = boundary_magnet_1;
	assign LEDR[9] = boundary_magnet_2;
	*/
	
	
	wire wire_N = direction[0];
	wire wire_NW = direction[1];
	wire wire_W = direction[2];
	wire wire_SW = direction[3];
	wire wire_S = direction[4];
	wire wire_SE = direction[5];
	wire wire_E = direction[6];
	wire wire_NE = direction[7];
	
	wire [3:0] state_main;
	wire [3:0] state_magnet;
	
	reg go_main = 0;
	reg go_magnet = 0;
	
	reg [11:0] steps_main;
	reg [11:0] steps_magnet;
	
	reg dir_main;
	reg dir_magnet;
	
	reg im_done = 0;
	assign done = im_done;
	
	reg resetting = 0;
	reg offsetting = 0;
	
	reg [11:0] straight_steps = `straight_steps;
	reg [11:0] diagonal_steps = `diagonal_steps;
	reg [11:0] scan_offset = `scan_offset;
	reg [11:0] horizontal_steps = `horizontal_steps;
	
	assign GPIO_1[`red_main] = state_main[3];
	assign GPIO_1[`blue_main] = state_main[2];
	assign GPIO_1[`yellow_main] = state_main[1];
	assign GPIO_1[`orange_main] = state_main[0];
	
	assign GPIO_1[`red_magnet] = state_magnet[3];
	assign GPIO_1[`blue_magnet] = state_magnet[2];
	assign GPIO_1[`yellow_magnet] = state_magnet[1];
	assign GPIO_1[`orange_magnet] = state_magnet[0];
	
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
		if (resetting) reset_done <= (done_main && done_magnet);
		else if (offsetting) offset_done <= done_magnet;
		else if (go_main && go_magnet) im_done <= (done_main && done_magnet);
		else if (go_main) im_done <= done_main;
		else if (go_magnet) im_done <= done_magnet;
		if (!go_main && !go_magnet) begin
			im_done <= 0;
			reset_done <= 0;
			offset_done <= 0;
		end
		if (im_done || reset_done || offset_done) begin
			go_main <= 0;
			go_magnet <= 0;
			resetting <= 0;
			offsetting <= 0;
		end
		else if (reset) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 0;
			dir_magnet <= 0;
			steps_main <= 12'b1111_1111_1111;
			steps_magnet <= 12'b1111_1111_1111;
			resetting <= 1;
		end
		else if (scan_offset_move) begin
			go_main <= 1;
			dir_main <= 1;
			steps_main <= scan_offset;
		end
		else if (scan_move) begin
			go_main <= 1;
			dir_main <= 1;
			steps_main <= straight_steps;
		end
		else if (horizontal_offset) begin
			go_magnet <= 1;
			dir_magnet <= 1;
			steps_magnet <= horizontal_steps;
			offsetting <= 1;
		end
		else if (wire_N) begin
			go_main <= 1;
			dir_main <= 0;
			steps_main <= straight_steps;
		end
		else if (wire_S) begin
			go_main <= 1;
			dir_main <= 1;
			steps_main <= straight_steps;
		end
		else if (wire_E) begin
			go_magnet <= 1;
			dir_magnet <= 0;
			steps_magnet <= straight_steps;
		end
		else if (wire_W) begin
			go_magnet <= 1;
			dir_magnet <= 1;
			steps_magnet <= straight_steps;
		end
		else if (wire_NE) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 0;
			dir_magnet <= 0;
			steps_main <= diagonal_steps;
			steps_magnet <= diagonal_steps;
		end
		else if (wire_SE) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 1;
			dir_magnet <= 0;
			steps_main <= diagonal_steps;
			steps_magnet <= diagonal_steps;
		end
		else if (wire_SW) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 1;
			dir_magnet <= 1;
			steps_main <= diagonal_steps;
			steps_magnet <= diagonal_steps;
		end
		else if (wire_NW) begin
			go_main <= 1;
			go_magnet <= 1;
			dir_main <= 0;
			dir_magnet <= 1;
			steps_main <= diagonal_steps;
			steps_magnet <= diagonal_steps;
		end
	end
	
	motorControl mainMotor(
		.clk(clk),
		.go(go_main),
		.direction(dir_main),
		.steps(steps_main),
		.boundary1(boundary_main_1),
		.boundary2(boundary_main_2),
		.state(state_main),
		.done(done_main)
	);
	
	motorControl magnetMotor(
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