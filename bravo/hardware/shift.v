module shift(
			input clk,
			input WireIn,
			output ANDShift
			);
			
			reg Out = 0;
			reg [9:0] ShiftReg;
			
			always@ (posedge clk) begin
			ShiftReg = ShiftReg << 1;
			ShiftReg[0] = WireIn;
			if(ShiftReg == 10'b11_1111_1111) Out <= 1;
			else Out <= 0;
			end
			
			assign ANDShift = Out;
			
endmodule