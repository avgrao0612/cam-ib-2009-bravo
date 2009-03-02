
// Pin numbers of corresponding wires. These pins control the motors.
`define red_main 		10	
`define blue_main 		12
`define yellow_main 	14
`define orange_main 	16

`define red_magnet		1
`define blue_magnet		3
`define yellow_magnet	5
`define orange_magnet	7

// This is a constant factor that we use to control the speed of the
// motors. We divide the clock by this constant to get the frequency
// at which we drive the motors, so a higher number here actually
// corresponds to a slower motor speed and vice versa.
`define speed	24'b0000_0001_1111_1111_1111_1111

//The number of motor steps for each move.
`define straight_steps 12'd188  //Normal move of one square length.
`define scan_offset_steps 12'd220  //Align the sensors to the squares.
`define horizontal_offset_steps 12'd83  //Align the magnet to the squares.

//Pin numbers for the reed switches.
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

//Pin numbers for the boundary swithces.
`define reset_y_1 4'd2
`define reset_y_2 4'd4
`define reset_x_1 4'd6
`define reset_x_2 4'd8

//Pin number of the electromagnet.
`define magnet 4'd11

//Assignments for LCD display:

`define A 9'h141
`define B 9'h142
`define C 9'h143
`define D 9'h144
`define E 9'h145
`define F 9'h146
`define G 9'h147
`define H 9'h148
`define I 9'h149
`define J 9'h14A
`define K 9'h14B
`define L 9'h14C
`define M 9'h14D
`define N 9'h14E
`define O 9'h14F
`define P 9'h150
`define Q 9'h151
`define R 9'h152
`define S 9'h153
`define T 9'h154
`define U 9'h155
`define V 9'h156
`define W 9'h157
`define X 9'h158
`define Y 9'h159
`define Z 9'h15A

`define space 9'b100100000
`define exclaim 9'h121

`define i0 9'h130
`define i1 9'h131
`define i2 9'h132
`define i3 9'h133
`define i4 9'h134
`define i5 9'h135
`define i6 9'h136
`define i7 9'h137
`define i8 9'h138
`define i9 9'h139

`define a 9'h161
`define b 9'h162
`define c 9'h163
`define d 9'h164
`define e 9'h165
`define f 9'h166
`define g 9'h167
`define h 9'h168
`define i 9'h169
`define j 9'h16A
`define k 9'h16B
`define l 9'h16C
`define m 9'h16D
`define n 9'h16E
`define o 9'h16F
`define p 9'h170
`define q 9'h171
`define r 9'h172
`define s 9'h173
`define t 9'h174
`define u 9'h175
`define v 9'h176
`define w 9'h177
`define x 9'h178
`define y 9'h179
`define z 9'h17A