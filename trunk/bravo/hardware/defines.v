
// pin numbers of corresponding wires
`define red_main 		10	
`define blue_main 		12
`define yellow_main 	14
`define orange_main 	16

`define red_magnet		1
`define blue_magnet		3
`define yellow_magnet	5
`define orange_magnet	7

// higher number = slower
`define speed	24'b0000_0001_1111_1111_1111_1111
//`define speed	24'b1111_1111_1111_1111_1111_1111

// Number of steps for a straight or diagonal move.
// Note that for the diagonal move, the number should
// be the steps taken in a straight direction, so for
// example, setting diagonal_move = 100 would result in
// a diagonal move of about 144 (100 steps along each side
// of a right triangle).
`define straight_steps 12'd188
`define diagonal_steps 12'd188

`define scan_offset 12'd220

`define horizontal_steps 12'd83
//`define horizontal_steps 12'd300

`define sensor_0 6'd26
`define sensor_1 6'd27
`define sensor_2 6'd29
`define sensor_3 6'd31
`define sensor_4 6'd33
`define sensor_5 6'd35
`define sensor_6 6'd34
`define sensor_7 6'd32
`define sensor_8 6'd30
`define sensor_9 6'd28

`define reset_y_1 4'd2
`define reset_y_2 4'd4
`define reset_x_1 4'd6
`define reset_x_2 4'd8

`define magnet 4'd11