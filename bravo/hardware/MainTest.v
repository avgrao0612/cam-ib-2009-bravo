
// top level module

`include "defines.v"

module MainTest(
         input CLOCK_50,
         input [3:0] KEY,
         input [17:0] SW,
         inout [35:0] GPIO_0,
         output [17:0] LEDR,
         output [7:0] LEDG,
         input UART_RXD,
         output UART_TXD
         );
		
		 wire [9:0] sensorData;
		 wire boundary_S;
         wire boundary_N;
         wire boundary_E;
         wire boundary_W;
		 
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





		 
		 
		 
         wire [7:0] input_stream;
         wire [7:0] output_stream;
         wire [7:0] direction;
         wire [4:0] pieces;
         
        // wire boundary_S = (!GPIO_0[`reset_y_2]);
        //wire boundary_N = (!GPIO_0[`reset_y_1]);
        // wire boundary_E = (!GPIO_0[`reset_x_2]);
        // wire boundary_W = (!GPIO_0[`reset_x_1]);

motorMain m(
         .clk(CLOCK_50),
         .direction(direction),
         .scan_offset_move(scan_offset_move),
         //.scan_offset_move(SW[8]),
         .scan_move(scan_move),
         .horizontal_offset(horizontal_offset),
         //.horizontal_offset(SW[8]),
         
         .boundary_main_1(boundary_S),
         .boundary_main_2(boundary_N),
         .boundary_magnet_1(boundary_E),
         .boundary_magnet_2(boundary_W),
         
         //.boundary_main_1(SW[8]),
         //.boundary_main_2(SW[7]),
         //.boundary_magnet_1(SW[6]),
         //.boundary_magnet_2(SW[5]),
         
         .reset(reset),
         .GPIO_1(GPIO_0),
         .done(movement_done),
         .reset_done(reset_done),
         .offset_done(offset_done)
         
         //.LEDR(LEDR)
         //.LEDG(LEDG)
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
         
         //.LEDG(LEDG)
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
         .new_game(!KEY[1]),
         .user_turn_done(!KEY[0]),
         
         .LEDR(LEDR[10:0])
         );

encoding e(
         .clk(CLOCK_50),
         .user_turn_done(!KEY[0]),
         .movement_done(movement_done),
         .reset_done(reset_done),
         .offset_done(offset_done),
         .input_stream(input_stream),
         .sending_scan_left(sending_scan_left),
         .sending_scan_right(sending_scan_right),
         .resign(!KEY[2]),
         .draw(!KEY[3]),
         .pieces(pieces),
         .new_game(!KEY[1]),
         
         .black_setting(SW[5:3]),
         .white_setting(SW[2:0]),
         
         .dataStream(output_stream),
         .data_start(TxD_start)
         //.LEDG(LEDG),
         //.LEDR(LEDR[9])
         );

decoding d(
         .clk(CLOCK_50),
         
         .data_incoming(RxD_data_ready),
         .dataStream(input_stream),
         //.data_incoming(SW[9]),
         //.dataStream(SW[17:10]),
         
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
         
         //.LEDG(LEDG)
         );

async_receiver r(
         .clk(CLOCK_50),
         .RxD(UART_RXD),
         .RxD_data_ready(RxD_data_ready),
         .RxD_data(input_stream),
         //.RxD_data(LEDR[17:10]),
         .RxD_endofpacket(RxD_endofpacket),
         .RxD_idle(RxD_idle)
         );

async_transmitter t(
         .clk(CLOCK_50),
         .TxD_start(TxD_start),
         .TxD_data(output_stream),

         .TxD(UART_TXD),
         //.TxD(LEDG),
         //.LEDG(LEDG),

         .TxD_busy(TxD_busy)
         );

endmodule
