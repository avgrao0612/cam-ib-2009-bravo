
/*
This is the top level module. It just instantiates other modules and
connects these instances to each other and to the board inputs/outputs.

Input: The main input will come from the Java software through the serial
port. The module will also receive some input from the GPIO port, which
will send it the values of the reed switches during a scan. There is also
some user input required which is handled via the KEY buttons and the SW
switches on the Altera board. These functions are:

KEY[0] - user turn done
KEY[1] - new game
KEY[2] - resign
KEY[3] - draw (accept or offer)

SW[2:0] - white setting (see encoding module for details)
SW[5:3] - black setting (see encoding module for details)

Output: This module will transmit data to the Java software through the
serial port. It will also send signals to the motors and the electromagnet
through the GPIO port. The user will be alerted to various game state (e.g.
black wins, white to play, etc) via the LCD display as detailed in the
display_interface module.
*/

`include "defines.v"

module Main(
	input CLOCK_50,
	input [3:0] KEY,
	input [5:0] SW,
	inout [35:0] GPIO_0,
	input UART_RXD,
	output UART_TXD,
	inout [7:0] LCD_DATA,
	output LCD_ON,
	output LCD_BLON,
	output LCD_RW,
	output LCD_EN,
	output LCD_RS
	);

	reg [3:0] posKEY;
	reg [3:0] edgeKEY;
	
	wire [7:0] input_stream;
	wire [7:0] output_stream;
	wire [7:0] direction;
	wire [4:0] pieces;
	wire [9:0] sensorData;
	wire [10:0] display;
	wire boundary_S;
	wire boundary_N;
	wire boundary_E;
	wire boundary_W;
	
// converts the key input into a single pulse so when pressed and even held
// only a single signal is sent instead of one every clock cycle
// if the button is pressed a register goes high saying its held so the posKey is set to
// 0 on the next clock cycle
always @ (posedge CLOCK_50) begin
	if(!KEY[0] & !edgeKEY[0]) begin
		posKEY[0] <= 1;
		edgeKEY[0] <= 1;
	end
	else if (KEY[0]) edgeKEY[0] <= 0;
	else posKEY[0] <= 0;
	
	if(!KEY[1] & !edgeKEY[1]) begin
		posKEY[1] <= 1;
		edgeKEY[1] <= 1;
	end
	else if (KEY[1]) edgeKEY[1] <= 0;
	else posKEY[1] <= 0;
	
	if(!KEY[2] & !edgeKEY[2]) begin
		posKEY[2] <= 1;
		edgeKEY[2] <= 1;
	end
	else if (KEY[2]) edgeKEY[2] <= 0;
	else posKEY[2] <= 0;
	
	if(!KEY[3] & !edgeKEY[3]) begin
		posKEY[3] <= 1;
		edgeKEY[3] <= 1;
	end
	else if (KEY[3]) edgeKEY[3] <= 0;
	else posKEY[3] <= 0;
end
	


//Inputs from the GPIO port are threaded through a shift register
//to filter out noise:

shift Bound_N(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`reset_y_1]),
	.ANDShift(boundary_N)
	);

shift Bound_S(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`reset_y_2]),
	.ANDShift(boundary_S)
	);

shift Bound_W(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`reset_x_1]),
	.ANDShift(boundary_W)
	);

shift Bound_E(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`reset_x_2]),
	.ANDShift(boundary_E)
	);

shift Sensor1(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_0]),
	.ANDShift(sensorData[0])
	);
			
shift Sensor2(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_1]),
	.ANDShift(sensorData[1])
	);

shift Sensor3(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_2]),
	.ANDShift(sensorData[2])
	);

shift Sensor4(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_3]),
	.ANDShift(sensorData[3])
	);

shift Sensor5(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_4]),
	.ANDShift(sensorData[4])
	);

shift Sensor6(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_5]),
	.ANDShift(sensorData[5])
	);

shift Sensor7(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_6]),
	.ANDShift(sensorData[6])
	);

shift Sensor8(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_7]),
	.ANDShift(sensorData[7])
	);

shift Sensor9(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_8]),
	.ANDShift(sensorData[8])
	);

shift Sensor10(
	.clk(CLOCK_50),
	.WireIn(!GPIO_0[`sensor_9]),
	.ANDShift(sensorData[9])
	);

motor_main m(
	.clk(CLOCK_50),
	.direction(direction),
	.scan_offset_move(scan_offset_move),
	.scan_move(scan_move),
	.horizontal_offset(horizontal_offset),
	.boundary_main_1(boundary_S),
	.boundary_main_2(boundary_N),
	.boundary_magnet_1(boundary_E),
	.boundary_magnet_2(boundary_W),
	.reset(reset),
	.GPIO_0(GPIO_0),
	.movement_done(movement_done),
	.reset_done(reset_done),
	.offset_done(offset_done)
	);

sensor_scan s(
	.clk(CLOCK_50),
	.start_scan(want_scan),
	.scan_data(sensorData),
	.scan_move_done(movement_done),
	.TxD_busy(TxD_busy),
	.sending_scan_left(sending_scan_left),
	.sending_scan_right(sending_scan_right),
	.pieces(pieces),
	.scan_offset_move(scan_offset_move),
	.scan_move(scan_move),
	);

magnet_control ma(
	.clk(CLOCK_50),
	.magnet_on(magnet_on),
	.magnet_off(magnet_off),
	.magnet_state(GPIO_0[`magnet])
	);

display_interface di(
	.clk(CLOCK_50),
	.black_to_play(black_to_play),
	.white_to_play(white_to_play),
	.draw_offer(draw_offer),
	.black_wins(black_wins),
	.white_wins(white_wins),
	.draw_game(draw_game),
	.normal_wait(normal_wait),
	.player_must_jump(player_must_jump),
	.more_jumps_available(more_jumps_available),
	.unrecoverable_error(unrecoverable_error),
	.did_not_move(did_not_move),
	.new_game(posKEY[1]),
	.user_turn_done(posKEY[0]),
	.display(display)
	);

encoding e(
	.clk(CLOCK_50),
	.user_turn_done(posKEY[0]),
	.movement_done(movement_done),
	.reset_done(reset_done),
	.offset_done(offset_done),
	.input_stream(input_stream),
	.sending_scan_left(sending_scan_left),
	.sending_scan_right(sending_scan_right),
	.resign(posKEY[2]),
	.draw(posKEY[3]),
	.pieces(pieces),
	.new_game(posKEY[1]),
	.black_setting(SW[5:3]),
	.white_setting(SW[2:0]),
	.dataStream(output_stream),
	.data_start(TxD_start)
	);

decoding d(
	.clk(CLOCK_50),
	.data_incoming(RxD_data_ready),
	.dataStream(input_stream),
	.new_game(new_game),
	.user_turn_done(user_turn_done),
	.direction(direction),
	.want_scan(want_scan),
	.magnet_on(magnet_on),
	.magnet_off(magnet_off),
	.reset(reset),
	.black_to_play(black_to_play),
	.white_to_play(white_to_play),
	.draw_offer(draw_offer),
	.black_wins(black_wins),
	.white_wins(white_wins),
	.draw(draw_game),
	.normal_wait(normal_wait),
	.player_must_jump(player_must_jump),
	.more_jumps_available(more_jumps_available),
	.unrecoverable_error(unrecoverable_error),
	.did_not_move(did_not_move),
	.horizontal_offset(horizontal_offset),
	);

async_receiver r(
	.clk(CLOCK_50),
	.RxD(UART_RXD),
	.RxD_data_ready(RxD_data_ready),
	.RxD_data(input_stream),
	.RxD_endofpacket(RxD_endofpacket),
	.RxD_idle(RxD_idle)
	);

async_transmitter t(
	.clk(CLOCK_50),
	.TxD_start(TxD_start),
	.TxD_data(output_stream),
	.TxD(UART_TXD),
	.TxD_busy(TxD_busy)
	);

LCD_Interfacer l(
	.CLOCK_50(CLOCK_50),
	.SW(display),
	.LCD_DATA(LCD_DATA),
	.LCD_ON(LCD_ON),
	.LCD_BLON(LCD_BLON),
	.LCD_RW(LCD_RW),
	.LCD_EN(LCD_EN),
	.LCD_RS(LCD_RS),
	);

endmodule
