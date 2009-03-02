
/*
Magnet control requires its own module because unlike most of
the other signals to the board, we want this one to be held
high rather than pulsed. So, the magnet_on signal will only
be pulsed high but we then want to keep the magnet on so we
need a seperate register to hold the magnet state. Note that
the magnet is active low so magnet = 1 corresponds to magnet off.
*/

module magnet_control(
	input clk,
	input magnet_on,
	input magnet_off,
	output magnet_state
	);
	
	// turn magnet off at start
	reg magnet = 1;
	assign magnet_state = magnet;
	
	always@(posedge clk) begin
	//Update the magnet_state if a signal is received, otherwise
	//keep the current state.
		if (magnet_on) magnet <= 0;
		else if (magnet_off) magnet <= 1;
	end
	
endmodule