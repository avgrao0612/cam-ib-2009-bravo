# Software to Hardware interface #

## Overview ##

The job of the SW/HW interface is to call commands and pass the relevant arguments from the Java side to Verilog which drives the electronics on the game board. To do that it needs to encode the command with the arguments into a stream of bits, then using a protocol send the steam of data through a serial port to an Altera board. The Verilog need to sample the data and then decode it, identifying which method is called and obtains the relative data; and performing the method. It must also send relative data though the same channel back to the java via the serial port.

## Sampling ##

To sample and send a stream of data a protocol is required. The main features of this protocol are a size of a stream, baud rate/sampling rate, idle state, stop, and start bits. The size of the data stream will be a byte for ease with Java types and convention. The baud rate/sampling rate will be variable which can be changed if necessary but no matter what will be the same for both sides. The idle signal will be a 1 and the start bit will be a 0 to signal that the next 8 bits will be the data stream, then the stop bit a 1 will set it back to the idle state. The reason for this is to detect stuck high or stuck low errors on the signal.

To sample the data, a side will sample the incoming stream on a wire at 8 or 16 times the baud rate. This is guarantee it will hit the start bit when it is sent, because it has several chances to sample the one bit. It will then precede to sample at the baud rate to get each bit, then when it get the stop it stops recording the data till it sees the next start bit. The data is passed to decode to get the method and arguments of it. To send the data, the program is given as a byte, and it sets then splits it up 1 bit at a time. Then sends it with the start and stop bits before and after at the agreed baud rate.

## Encoding ##

### Java to Verilog ###

  * 00 XXX XXX (M)otor control
    * 00 XXX 000 move in (D)irection
      * This requires an argument of the compass direction (1-8). This can be encoded with 3 bits, 000-111, a good convention would be for 000 to be N and then count up clockwise to 111 being NW. The 3 bits can be the 3rd-5th bits, leaving 3 left if required.
    * 00 111 001 W-E (O)ffset (for motor)
    * 00 111 111 (R)eset to (0,0)
  * 01 X00 000 ma(G)net control
  * 10 XXX XXX misc game (A)ctions
    * 10 111 111 (S)can
    * 10 1ZZ 0XX tur(N) start
      * 10 1ZZ 001 blac(K) to play
      * 10 1ZZ 010 whit(E) to play
      * ZZ: 00: normal turn
      * ZZ: 01: draw offered
    * 10 000 0XX game (O)ver
      * 10 000 001 (B)lack wins
      * 10 000 010 (W)hite wins
      * 10 000 011 (D)raw
  * 11 XXX XXX re(Q)uest human response
    * 11 000 000 : normal wait
    * 11 000 001 : player must jump; wait for player to fix
    * 11 000 010 : more jumps available in sequence; wait for player to fix
    * 11 000 011 : player hasn't made a move yet, board is in the same state as previously; wait for player to fix
    * 11 111 111 : unrecoverable error; wait for player to fix

### Verilog to Java ###

  * 00 XXX 000 human (R)esponse
    * 00 000 000 : (T)urn done (implies decline draw)
    * 00 010 000 : offer (D)raw, or accept draw
    * 00 100 000 : r(E)sign
  * 01 XXX XXX (M)ovement complete
    * XXX XXX : as in Motor Control above
  * 10 XYY YYY (S)can data (X left/right side bit, Y scanned data)
    * Scanning a row of data returns 10 bits, 1 for each square on a row of the board (8 live and 2 dead). Setting a bit to 1 for a piece and 0 for empty. The problem is a signal stream only sends a byte (8 bits), therefore multiple streams are needed. A way to do this would be to have a bit to tell if the stream is for the 1st half or the 2nd half then 5 bits to encode that half of the row. So having the 3rd bit can be set to 1 for the left side and 0 for the right side, then the 4th-8th bit being the 5 scanned bits
  * 11 XXX YYY (N)ew game (X black, Y white)
    * 000 human
    * 001 AI:easy
    * 010 AI:standard
    * 100 AI:hard

## Send/Recv flowchart ##

overview of send/recv algorithm

  * "send" means Java to Verilog.
  * "recv" means Verilog to Java. all recvs are BLOCKING.

### Human turn ###

(some of these actions will actually be carried out by the Board class)

  * send Q=11000000 (request response)
  * loop
    * recv R "turn type" (normal, draw, resign)
    * if game has ended in draw or resign, send O and end game
    * send S (scan)
    * recv S (scan data)
    * if board is valid, break
    * else, send Q indicating error condition
  * for any additional moves to be done
    * send G=on
    * send M (move)
    * recv M (move successful)
    * repeat above two as necessary
    * send G=off

### AI turn ###

(some of these actions will actually be carried out by the Board class)

  * for all moves to be done
    * send G=on
    * send M (move)
    * recv M (move successful)
    * repeat above two as necessary
    * send G=off