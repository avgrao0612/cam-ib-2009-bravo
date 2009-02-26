//The Java side of the software-hardware interface. This class is responsible for various
//signal transmission and receival through the serial cable between the host computer and the
//DE2 board.
//However, this class is very machine-dependent, since it requires a special java extension
//package called Java Communication API, which is not included in the standard JDK. So only the
//machines with this package can run this code.

package bravo.io;

import bravo.game.Draughts.*;
import bravo.game.Board.*;
//import javax.comm.*;
import gnu.io.*; // an alternative to javax.comm
import java.io.*;
import java.util.Arrays;

public class HWInterface
{
     private final byte[] DIRECTION={0x38,0x00,0x08,0x10,0x18,0x20,0x28,0x30};
     private final byte RESET=0x3f;
     private final byte MAGNET_ON=0x60,MAGNET_OFF=0x40;
     private final byte SCAN_REQUEST=(byte)0xbf,BLACK_TURN=(byte)0xa1,WHITE_TURN=(byte)0xa2;
     private final byte ERROR=(byte)0xff;
//All the signals sent from Java to Verilog

     private final byte CONTINUE=0x00,WISH_TO_DRAW=0x10,RESIGN=0x20;
     private final byte ACKNOWLEDGEMENT=(byte)0x40;
     private final byte START_OPTION_CHECKER=(byte)0xc0;
     private final byte ROW_ALINEMENT_CHECKER=(byte)0x80;
     private final byte POSITION_CHECKER=0x20;
//Some of the signals received from Verilog to Java

     private InputStream is;
     private OutputStream os;

     public HWInterface(String portName,int baudRate)
     {
         try
         {
             CommPortIdentifier cpi=CommPortIdentifier.getPortIdentifier(portName);
             SerialPort port=(SerialPort)cpi.open(this.getClass().getName(),1000);
             port.setSerialPortParams(baudRate,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
             is=port.getInputStream();
             os=port.getOutputStream();
             //throw new Exception("force testing through stdin");
         }
         catch(Exception e)
         {
             System.out.print("Initialisation error has occured: ");
             System.err.println(e);
             System.out.println("Using stdin instead. ");
             try {
                 is=System.in;
                 os=new FileOutputStream("/dev/null");
             } catch (Exception ee) {
                 System.exit(1);
             }
         }
     }
//The name of the serial port needs to be identified before run this code.

     public int gameStart()
     {
         int gameOption=receive("gameStart",START_OPTION_CHECKER);
//It waits a input signal from DE2 board to indicate the game option
         gameOption&=0x3f;
         return gameOption;
     }
/*The ourput of gameStart with different game options:
 *                 Human     Easy comouter     Normal computer     Hard computer        White player
 * Human             0             1                 2                  4
 * Easy computer     8             9                 10                 12
 * Normal computer   16            17                18                 20
 * Hard computer     32            33                34                 36
 *
 *  Black player
*/

    // tells the hardware whose turn it is now, and what state the game is in
    // this is called at the START of every turn
     public void nextRound(boolean player, GameState gstate)
     {
         byte side = player?WHITE_TURN:BLACK_TURN;
         byte state = (byte)(gstate.ordinal() << 3);
         transmit("nextRound", (byte)(side|state));
     }

     public void gameOver(EndGame gend)
     {
         transmit("gameOver", (byte)(gend.ordinal()|0x80));
     }

     public boolean[] scan()
     {
     // TODO: Janus says that at the end of every row data, he is going to send "movement complete"
         reset();
         transmit("scan",SCAN_REQUEST);
         byte[] rowState=new byte[20];
         int byteNumber=0;
         while(byteNumber<20)
         {
             int rowAlinement=receive("scan",ROW_ALINEMENT_CHECKER);
             if(rowAlinement==-1) continue;
             rowState[byteNumber]=(byte)rowAlinement;
             byteNumber++;
         }
         boolean[] boardState=new boolean[256];
         for(int c=0;c<rowState.length;c++)
         {
             int i=c>>1;
             byte rowAlinement=rowState[c];
             if((rowAlinement&POSITION_CHECKER)==POSITION_CHECKER)
             {
                 for(int j=4;j>=0;j--)
                 {
                     boardState[squareNumber(i,j)] = (rowAlinement&1)==1? true: false;
                     rowAlinement>>=1;
                 }
//Left half of the row alinement.
             }
             else
             {
                 for(int j=9;j>=5;j--)
                 {
                     boardState[squareNumber(i,j)] = (rowAlinement&1)==1? true: false;
                     rowAlinement>>=1;
                 }
             }
         }
//Right half of the row alinement.

/*
		int[] c = {8, 0, 1, 2, 3, 4, 5, 6, 7, 9};
		String cellsep = "+---+---+---+---+---+---+---+---+---+---+\n";
		String rowsep = "+   +---+---+---+---+---+---+---+---+   +\n";

		StringBuffer out = new StringBuffer();
		out.append(cellsep);

		int x, y;
		char ch;
		for (int i=c.length-1; i>=0; --i) {
			y = c[i];
			out.append("|");

			for (int j=0; j<c.length; ++j) {
				x = c[j];
				ch = boardState[y<<4|x]?'.':' ';
				out.append(" ").append(ch).append(y<8||x==9?" |":"  ");

			}
			out.append(" ").append(y).append("\n").append(y==8?cellsep:rowsep);

		}
		out.append("  8   0   1   2   3   4   5   6   7   9 x\\y\n");

		System.out.println(out.toString());
*/

         return boardState;
     }
//Calling scan returns the current board state.

     public EndTurn proceed(BoardState situation)
     {
         transmit("proceed", (byte)(situation.ordinal()>4?ERROR:situation.ordinal()|0xC0));
         int playerResponse=receive("proceed",(byte)0);
//player must play a move, or fix the board first.
         switch(playerResponse)
         {
             default: case CONTINUE: return EndTurn.NORMAL;
             case WISH_TO_DRAW: return EndTurn.DRAW;
             case RESIGN: return EndTurn.RESIGN;
         }
     }
//This method is called to indicate the situation of the game. It requires the board to be fixed
//before carrying on to play, and it gives the player a second chance to think about their action.

     public void moveHead(int direction)
     {
         transmit("moveHead",DIRECTION[direction-1]);
//Transmit th emoving order.
         byte[] validData={ACKNOWLEDGEMENT};
         receive("moveHead",validData);
//The method will be blocked until an acknowledge is received.
     }
//Move the electromagnet head to a neighbouring square in the direction specified.

     public void magnetSwitch(boolean power)
     {
         byte magnetState=power?MAGNET_ON:MAGNET_OFF;
         transmit("magnetSwitch",magnetState);
     }
//Switch on/off the electromagnet head.

     public void reset()
     {
         transmit("reset",RESET);
         byte[] validData={ACKNOWLEDGEMENT};
         receive("reset",validData);
     }
//Reset the magnetic head back to the starting position. Should not be called directly.

     private int squareNumber(int x, int y)
     {
         int s;
         if(x<9&&x>0) s=(byte)(8-x);
         else s=(x==0)?(byte)9:8;
         s<<=4;
         if(y<9&&y>0) s+=(byte)(y-1);
         else s+=(y==0)?(byte)8:9;
         return s;
     }
//Map the coordinate system used in Pathing to the system used in Board.

     private void transmit(String method, byte signal)
     {
         byte[] data=new byte[1];
         data[0]=signal;
         try
         {
             System.out.println("TX " + String.format("%1$#8s", Integer.toString(data[0]&0xff, 2)).replace(' ','0'));
             os.write(data);
         }
         catch(Exception e)
         {
             System.out.print("Error for transmitting at "+method+" : ");
             System.err.println(e);
         }
     }
//This method is called to transmit one byte at a time.

     private int receive(String method, byte[] validSignal)
     {
         try
         {
             byte[] data=new byte[1];
             while(true)
             {
                 int byteNumber=is.read(data);
                 if (byteNumber<1) continue;
                        //System.out.println("RX " + String.format("%1$#8s", Integer.toString(data[0]&0xff, 2)).replace(' ','0'));
                 for(int i=0;i<validSignal.length;i++)
                    if(data[0]==validSignal[i])
                    {
                        System.out.println("             RX " + String.format("%1$#8s", Integer.toString(data[0]&0xff, 2)).replace(' ','0'));
                        return data[0];
                    }
             }
         }
         catch(Exception e)
         {
             System.out.print("Error for receiving at "+method+" : ");
             System.err.println(e);
             return -1;
         }
     }
//This method is called to receive a signal. Only the signals that are member of
//validSignal will be accepted so the method will not return until one of them is
//is received. Return -1 if something wrong has happened.

     private int receive(String method, byte signalType)
     {
         try
         {
             byte[] data=new byte[1];
             byte checker=(byte)0xc0;
             while(true)
             {
                 int byteNumber=is.read(data);
                 if (byteNumber<1) continue;
                        //System.out.println("RX " + String.format("%1$#8s", Integer.toString(data[0]&0xff, 2)).replace(' ','0'));
                 if((data[0]&checker^signalType)==0) {
                        System.out.println("             RX " + String.format("%1$#8s", Integer.toString(data[0]&0xff, 2)).replace(' ','0'));
                        return data[0];
                 }
             }
         }
         catch(Exception e)
         {
             System.out.print("Error for receiving at "+method+" : ");
             System.err.println(e);
             return -1;
         }
     }
//This method is called to receive a signal. Only the signals that have the required
//signal format will be accepted so the method will not return until one of them is
//is received. Return -1 if something wrong has happened.
     public static void main(String[] args)
     {
         HWInterface hwi=new HWInterface("COM4",115200);
         //hwi.reset();
         //hwi.magnetSwitch(true);
         //hwi.moveHead(1);
         //hwi.nextRound(true, 0);
         //int i=hwi.proceed(3);
         //System.out.println(i);
     }
}

