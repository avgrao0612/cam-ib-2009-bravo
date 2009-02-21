//The Java side of the software-hardware interface. This class is responsible for various
//signal transmission and receival through the serial cable between the host computer and the
//DE2 board.
//However, this class is very machine-dependent, since it requires a special java extension
//package called Java Communication API, which is not included in the standard JDK. So only the
//machines with this package can run this code.

package bravo.io;

import javax.comm.*;
import java.io.*;

public class HWInterface
{
     private final byte[] DIRECTION={0x38,0x00,0x08,0x10,0x18,0x20,0x28,0x30};
     private final byte RESET=0x3f;
     private final byte MAGNET_ON=0x60,MAGNET_OFF=0x40;
     private final byte SCAN_REQUEST=(byte)0xbf,BLACK_TURN=(byte)0xb9,WHITE_TURN=(byte)0xba;
     private final byte BLACK_WIN=(byte)0x81,WHITE_WIN=(byte)0x82,DRAW=(byte)0x83;
     private final byte NORMAL=(byte)0xc0,JUMP=(byte)0xc1,MORE_JUMPS=(byte)0xc2,ERROR=(byte)0xff;
//All the signals sent from Java to Verilog

     private final byte CONTINUE=0x00,WISH_TO_DRAW=0x10,RESIGN=0x20;
     private final byte ACKNOWLEDGEMENT=(byte)0x40;
     private final byte START_OPTION_CHECKER=(byte)0xc0;
     private final byte ROW_ALINEMENT_CHECKER=(byte)0x80;
     private final byte POSITION_CHECKER=0x20;
//Some of the signals received from Verilog to Java

     private InputStream is;
     private OutputStream os;

     public HWInterface(String portName)
     {
         try
         {
             CommPortIdentifier cpi=CommPortIdentifier.getPortIdentifier(portName);
             SerialPort port=(SerialPort)cpi.open(this.getClass().getName(),1000);
             port.setSerialPortParams(200000,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
             is=port.getInputStream();
             os=port.getOutputStream();
         }
         catch(Exception e)
         {
             System.out.print("Initialisation error has occured: ");
             System.err.println(e);
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

     public int nextRound(boolean player, int option)
     {
         byte side=player?BLACK_TURN:WHITE_TURN;
         transmit("nextRound",side);
         int playerResponse=-1;
         switch(option)
         {
             case 0:playerResponse=receive("nextRound",(byte)0);break;
             case 1:transmit("nextRound",BLACK_WIN); return 3;
             case 2:transmit("nextRound",WHITE_WIN); return 3;
             case 3:transmit("nextRound",DRAW); byte[] acceptance={CONTINUE,WISH_TO_DRAW};
                    playerResponse=receive("nextRound",acceptance); break;
         }
//0=continue as usual; 1=player has been offered to draw; 2=player's opponent has resigned; 3=game over
         switch(playerResponse)
         {
             case CONTINUE: return 0;
             case WISH_TO_DRAW: return 1;
             case RESIGN: return 2;
             default:  return playerResponse;
         }
//-1=something wrong has happened
     }

     public boolean[] scan()
     {
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
         int[][] board=new int[10][10];
         for(int i=0;i<rowState.length;i++)
         {
             int rowNumber=i/2;
             byte rowAlinement=rowState[i];
             if((rowAlinement&POSITION_CHECKER)==POSITION_CHECKER)
             {
                 for(int j=4;j>=0;j--)
                 {
                     board[rowNumber][j]=rowAlinement%2;
                     rowAlinement/=2;
                 }
//Left half of the row alinement.
             }
             else
             {
                 for(int j=9;j>=5;j--)
                 {
                     board[rowNumber][j]=rowAlinement%2;
                     rowAlinement/=2;
                 }
             }
         }
//Right half of the row alinement.
         boolean[] boardState=new boolean[256];
         for(int i=0;i<board.length;i++)
             for(int j=0;j<board[i].length;j++)
             {
                 int squareNumber=squareNumber(i,j);
                 boardState[squareNumber]=(board[i][j]==1)?true:false;
             }
         return boardState;
     }
//Calling scan returns the current board state.

     public int proceed(int situation)
     {
         int playerResponse=-1;
         switch(situation)
         {
             case 1: transmit("proceed",JUMP);playerResponse=receive("proceed",(byte)0);break;
             case 2: transmit("proceed",MORE_JUMPS);playerResponse=receive("proceed",(byte)0);break;
             case 3: transmit("proceed",ERROR);playerResponse=receive("proceed",(byte)0);break;
             default: transmit("proceed",NORMAL);return 3;
         }
//3 is returned if the game can proceed normally, otherwise player must fix the board first.
         switch(playerResponse)
         {
             case CONTINUE: return 0;
             case WISH_TO_DRAW: return 1;
             case RESIGN: return 2;
             default:  return playerResponse;
         }
     }
//This method is called to indicate the situation of the game. It requires the board to be fixed
//before carrying on to play, and it gives the player a second chance to think about their action.

     public void moveHead(int direction)
     {
         transmit("moveHead",DIRECTION[direction-1]);
//Transmit th emoving order.
         receive("moveHead",ACKNOWLEDGEMENT);
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
     }
//Reset the magnetic head back to the starting position. Should not be called directly.

     private byte squareNumber(int x, int y)
     {
         byte s;
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
                 for(int i=0;i<validSignal.length;i++)
                    if(data[0]==validSignal[i]) return data[0];
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
                 if((data[0]&checker^signalType)==0) return data[0];
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
}

