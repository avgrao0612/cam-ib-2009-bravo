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
     private final byte SCANREQUEST=(byte)0x80,MAGNETON=0x60,MAGNETOFF=0x40;
     private final byte ACKNOWLEDGEMENT=0x40,ENDOFPLAYER=0x20,ENDOFCOMPUTER,RESIGN=0x00,ERRORSIGNAL=(byte)0xff;

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

     public void moveHead(int direction)
     {
         byte[] data=new byte[1];
         try
         {
             data[0]=DIRECTION[direction-1];
             os.write(data);
         }
         catch(IOException e)
         {
             System.out.print("Moving error has occured: ");
             System.err.println(e);
         }
         try
         {
             byte acknowledgement;
             while(true)
             {
                 acknowledgement=(byte)is.read(data);
                 if(acknowledgement==ACKNOWLEDGEMENT) break;
//Any other signals received will be discarded apart from ACKNOWLEDGEMENT.
             }

         }
         catch(Exception e)
         {
             System.out.print("Acknowledgement receiving error has occured: ");
             System.err.println(e);
         }
     }
//

     public void magnetOn()
     {
         try
         {
             byte[] data={MAGNETON};
             os.write(data);
         }
         catch(IOException e)
         {
             System.out.print("Magnet powering up error has occured: ");
             System.err.println(e);
         }
     }
// Turn the electromagnet on

     public void magnetOff()
     {
         try
         {
             byte[] data={MAGNETOFF};
             os.write(data);
         }
         catch(IOException e)
         {
             System.out.print("Magnet powering down error has occured: ");
             System.err.println(e);
         }
     }
// Turn the electromagnet off

     public boolean[] scan()
     {
         try
         {
             byte[] data={SCANREQUEST};
             os.write(data);
         }
         catch(IOException e)
         {
             System.out.print("Scanning error has occured: ");
             System.err.println(e);
         }
         byte[] rowState=new byte[20];
         int successful=-1;
         try
         {
             successful=is.read(rowState);
             if(successful<0) return null;
         }
         catch(Exception e)
         {
             System.out.print("Board state receiving error has occured: ");
             System.err.println(e);
         }
         int[][] board=new int[10][10];
         for(int i=0;i<rowState.length;i++)
         {
             int rowNumber=i/2;
             byte rowAlinement=rowState[i];
             if(i%2==0)
             {
                 for(int j=4;j>=0;j--)
                 {
                     board[rowNumber][j]=rowAlinement%2;
                     rowAlinement/=2;
                 }

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

         boolean[] boardState=new boolean[256];
         for(int i=0;i<board.length;i++)
             for(int j=0;j<board[i].length;j++)
             {
                 int squareNumber=squareNumber(i,j);
                 boardState[squareNumber]=(board[i][j]==1)?true:false;
             }
         return boardState;
     }

     public byte playerTurn()
     {
         try
         {
             byte[] data={ENDOFCOMPUTER};
             os.write(data);
         }
         catch(IOException e)
         {
             System.out.print("Error signal sent by computer after its turn: ");
             System.err.println(e);
         }
//Notify the DE2 board that the computer has finished its turn so the player can start.
         byte playerOperation=ERRORSIGNAL;
         try
         {
             byte[] data=new byte[1];
             while(true)
             {
                 playerOperation=(byte)is.read(data);
                 if(playerOperation==RESIGN||playerOperation==ENDOFPLAYER) break;
//Any other signals received will be discarded apart from RESIGN and ENDOFPLAYER.
             }
         }
         catch(Exception e)
         {
             System.out.print("Acknowledgement receiving error has occured: ");
             System.err.println(e);
         }
         return playerOperation;
     }

     public static byte squareNumber(int x, int y)
     {
         byte s;
         if(x<9&&x>0) s=(byte)(8-x);
         else s=(x==0)?(byte)9:8;
         s<<=4;
         if(y<9&&y>0) s+=(byte)(y-1);
         else s+=(y==0)?(byte)8:9;
         return s;
     }
}
