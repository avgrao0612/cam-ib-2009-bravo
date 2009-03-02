
/*
This module is a shift register implementation which ANDs 10 results. It is used to
filter out noise from the GPIO port.
*/

module shift(
	input clk,
	input WireIn,
	output ANDShift
	);
	
	reg Out = 0;
	reg [9:0] ShiftReg;
	
	always@ (posedge clk) begin
		ShiftReg = ShiftReg << 1; // Bit shifts the register
		ShiftReg[0] = WireIn; // puts the new input into the shift register
		if(ShiftReg == 10'b11_1111_1111) Out <= 1; // AND for output
		else Out <= 0;
	end
	
	assign ANDShift = Out;
	
endmodule