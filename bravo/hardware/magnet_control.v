
module magnet_control(
	input clk,
	input magnet_on,
	input magnet_off,
	output magnet_state
	);
	
	reg magnet = 1;
	assign magnet_state = magnet;
	
	always@(posedge clk) begin
		if (magnet_on) magnet <= 0;
		else if (magnet_off) magnet <= 1;
	end
	
endmodule