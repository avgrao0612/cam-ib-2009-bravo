//INCOMPLETE!!!
//Pathing is responsible for taking a series of Moves and calculating the best way to
//move the head from one square to another in order to drag a piece to its destination
//quickly and undisturbed.
package bravo.game;
import java.util.Vector;

class GoToTop extends Thread  //The thread responsible for calculating the path towards the top
{    
     private int[][] board;
     private int x;
     private int y;
     private int topBound;
     private long path;
     private Vector paths;

     public GoToTop(int[][] board,int x,int y,int topBound,long path,Vector paths)
     {    
          this.board=board;
          this.x=x;
          this.y=y;
          this.topBound=topBound;
          this.path=path;
          this.paths=paths;
     }

     private int isEndReached()
     {    
          if(x>0&&y>0&&board[x-1][y-1]==3) return 1;
          else if(x>0&&board[x-1][y]==3) return 2;
          else if(x>0&&y<board.length-1&&board[x-1][y+1]==3) return 3;
          else if(y<board[0].length-1&&board[x][y+1]==3) return 4;
          else if(x<board.length-1&&y<board[0].length-1&&board[x+1][y+1]==3) return 5;
          else if(x<board.length-1&&board[x+1][y]==3) return 6;
          else if(x<board.length-1&&y>0&&board[x+1][y-1]==3) return 7;
          else if(y>0&&board[x][y-1]==3) return 8;
          else return 0;
     }
//Check whether the distination square can be reached at the next move
/*Direction representation
            TOP
         1 | 2 | 3
        ___|___|___
  LEFT   8 |   | 4   RIGHT
        ___|___|___
         7 | 6 | 5
           |   |
           BOTTOM
*/     
     
     public void run()
     {    
         int isPathComplete=isEndReached();
         if(isPathComplete!=0)
         {  
             path*=10;
             path+=isPathComplete;
             paths.addElement(path);
         }
/*If the distination can be reached at the next move, store this move and add this 
  path to the set of all valid paths.
  The path is coded as a long, each digit represent a move and the whole sequence is
  in the reverse order.
*/        
         else if(x-1<=topBound) return;
//If the destination cannot be reached at the next move, yet it is about to pass the
//destination square, terminate the thread.
         else
         {    GoToTop g1=null;
              GoToTop g2=null;
              GoToTop g3=null;

              if(x-1>=0&&board[x-1][y]==0)
              {
		   g1=new GoToTop(board,x-1,y,topBound,path*10+2,paths);
                   g1.start();  //Recursion towards top
              } 
              if(x-1>=0&&y-1>=0&&board[x-1][y-1]==0)
              {   
		   g2=new GoToTop(board,x-1,y-1,topBound,path*10+1,paths);
                   g2.start();  //Recursion towards topleft
              }
 
              if(x-1>=0&&y+1<board[x-1].length&&board[x-1][y+1]==0)
              {   
		   g3=new GoToTop(board,x-1,y+1,topBound,path*10+3,paths);
                   g3.start();  //Recursion towards topright
              }

              try
              {   
                  if(g1!=null) g1.join();
                  if(g2!=null) g2.join();
                  if(g3!=null) g3.join();
              }
              catch(InterruptedException e){}
         }
     }
}

class GoToRight extends Thread  //The thread responsible for calculating the path towards the right
{    
     private int[][] board;
     private int x;
     private int y;
     private int rightBound;
     private long path;
     private Vector paths;

     public GoToRight(int[][] board,int x,int y,int rightBound,long path,Vector paths)
     {    
          this.board=board;
          this.x=x;
          this.y=y;
          this.rightBound=rightBound;
          this.path=path;
          this.paths=paths;
     }
 
     private int isEndReached()
     {    
          if(x>0&&y>0&&board[x-1][y-1]==3) return 1;
          else if(x>0&&board[x-1][y]==3) return 2;
          else if(x>0&&y<board.length-1&&board[x-1][y+1]==3) return 3;
          else if(y<board[0].length-1&&board[x][y+1]==3) return 4;
          else if(x<board.length-1&&y<board[0].length-1&&board[x+1][y+1]==3) return 5;
          else if(x<board.length-1&&board[x+1][y]==3) return 6;
          else if(x<board.length-1&&y>0&&board[x+1][y-1]==3) return 7;
          else if(y>0&&board[x][y-1]==3) return 8;
          else return 0;
     }

     public void run()
     {    
          int isPathComplete=isEndReached();
          if(isPathComplete!=0)
          {
              path*=10;
              path+=isPathComplete;
              paths.addElement(path);
          }
          else if(y+1>=rightBound) return;
          else
          {   GoToRight g1=null;
              GoToRight g2=null;
              GoToRight g3=null;

              if(y+1<board[x].length&&board[x][y+1]==0)
              {   
		   g1=new GoToRight(board,x,y+1,rightBound,path*10+4,paths);
                   g1.start();  //Recursion towards right
              }
              if(x-1>=0&&y+1<board[x-1].length&&board[x-1][y+1]==0)
              {   
		   g2=new GoToRight(board,x-1,y+1,rightBound,path*10+3,paths);
                   g2.start();  //Recursion towards topright
              }
              if(x+1<board.length&&y+1<board[x+1].length&&board[x+1][y+1]==0)
              {   
		   g3=new GoToRight(board,x+1,y+1,rightBound,path*10+5,paths);
                   g3.start();  //Recursion towards bottomright
              }

              try
              {
                  if(g1!=null) g1.join();
                  if(g2!=null) g2.join();
                  if(g3!=null) g3.join();
              }
              catch(InterruptedException e){}
          }
     }
}

class GoToBottom extends Thread  //Thread responsible for calculating the path towards the bottom
{   
      private int[][] board;
     private int x;
     private int y;
     private int bottomBound;
     private long path;
     private Vector paths;

     public GoToBottom(int[][] board,int x,int y,int bottomBound,long path,Vector paths)
     {    
          this.board=board;
          this.x=x;
          this.y=y;
          this.bottomBound=bottomBound;
          this.path=path;
          this.paths=paths;
     }

     private int isEndReached()
     {    if(x>0&&y>0&&board[x-1][y-1]==3) return 1;
          else if(x>0&&board[x-1][y]==3) return 2;
          else if(x>0&&y<board.length-1&&board[x-1][y+1]==3) return 3;
          else if(y<board[0].length-1&&board[x][y+1]==3) return 4;
          else if(x<board.length-1&&y<board[0].length-1&&board[x+1][y+1]==3) return 5;
          else if(x<board.length-1&&board[x+1][y]==3) return 6;
          else if(x<board.length-1&&y>0&&board[x+1][y-1]==3) return 7;
          else if(y>0&&board[x][y-1]==3) return 8;
          else return 0;
     }

     public void run()
     {    int isPathComplete=isEndReached();
          if(isPathComplete!=0)
          {   path*=10;
              path+=isPathComplete;
              paths.addElement(path);
          }
          else if(x+1>=bottomBound) return;
          else
          {    GoToBottom g1=null;
               GoToBottom g2=null;
               GoToBottom g3=null;

               if(x+1<board.length&&board[x+1][y]==0)
               {   
                   g1=new GoToBottom(board,x+1,y,bottomBound,path*10+6,paths);
                   g1.start();  //Recursion towards the bottom
               }
               if(x+1<board.length&&y+1<board[x+1].length&&board[x+1][y+1]==0)
               {   
                   g2=new GoToBottom(board,x+1,y+1,bottomBound,path*10+5,paths);
                   g2.start();  //Recursion towards the bottomright
               }
               if(x+1<board.length&&y-1>=0&&board[x+1][y-1]==0)
               {   
                   g3=new GoToBottom(board,x+1,y-1,bottomBound,path*10+7,paths);
                   g3.start();  //Recursion towards the bottomleft
               }

               try
               {
                   if(g1!=null) g1.join();
                   if(g2!=null) g2.join();
                   if(g3!=null) g3.join();
               }
               catch(InterruptedException e){}
        }
     }

}

class GoToLeft extends Thread  //Thread responsible for calculating the path towards the left
{    
     private int[][] board;
     private int x;
     private int y;
     private int leftBound;
     private long path;
     private Vector paths;

     public GoToLeft(int[][] board,int x,int y,int leftBound,long path,Vector paths)
     {    this.board=board;
          this.x=x;
          this.y=y;
          this.leftBound=leftBound;
          this.path=path;
          this.paths=paths;
     }
     private int isEndReached()
     {    
          if(x>0&&y>0&&board[x-1][y-1]==3) return 1;
          else if(x>0&&board[x-1][y]==3) return 2;
          else if(x>0&&y<board.length-1&&board[x-1][y+1]==3) return 3;
          else if(y<board[0].length-1&&board[x][y+1]==3) return 4;
          else if(x<board.length-1&&y<board[0].length-1&&board[x+1][y+1]==3) return 5;
          else if(x<board.length-1&&board[x+1][y]==3) return 6;
          else if(x<board.length-1&&y>0&&board[x+1][y-1]==3) return 7;
          else if(y>0&&board[x][y-1]==3) return 8;
          else return 0;
     }

     public void run()
     {    
          int isPathComplete=isEndReached();
          if(isPathComplete!=0)
          { 
              path*=10;
              path+=isPathComplete;
              paths.addElement(path);
          }
          else if(y-1<=leftBound) return;
          else
          {  
              GoToLeft g1=null;
              GoToLeft g2=null;
              GoToLeft g3=null;

              if(y-1>=0&&board[x][y-1]==0)
              {   
                  g1=new GoToLeft(board,x,y-1,leftBound,path*10+8,paths);
                  g1.start();  //Recursion towards the right
              }
              if(x+1<board.length&&y-1>=0&&board[x+1][y-1]==0)
              {  
                  g2=new GoToLeft(board,x+1,y-1,leftBound,path*10+7,paths);
                  g2.start();  //Recursion towards the bottomright
              }
              if(x-1>=0&&y-1>=0&&board[x-1][y-1]==0)
              {   
                  g3=new GoToLeft(board,x-1,y-1,leftBound,path*10+1,paths);
                  g3.start();  //Recursion towards the topright
              }

              try
              {
                  if(g1!=null) g1.join();
                  if(g2!=null) g2.join();
                  if(g3!=null) g3.join();
              }
              catch(InterruptedException e){}
        }
     }
}

public class Pathing 
{
    private int[][] boardStatus;
//A matrix to represent the current status of the board, indicating the positions of  
//all pieces and displaying the destination of a Move.
/*Coordinate system of boardStatus:

       0   1   2   3   4   5   6   7   8   9  y    
      _______________________________________
  0  |___|___|___|___|___|___|___|___|___|___|    
  1  |___|___|___|___|___|___|___|___|___|___|
  2  |___|___|___|___|___|___|___|___|___|___|
  3  |___|___|___|___|___|___|___|___|___|___|
  4  |___|___|___|___|___|___|___|___|___|___|
  5  |___|___|___|___|___|___|___|___|___|___|
  6  |___|___|___|___|___|___|___|___|___|___|
  7  |___|___|___|___|___|___|___|___|___|___|
  8  |___|___|___|___|___|___|___|___|___|___|
  9  |___|___|___|___|___|___|___|___|___|___|
  x
For each element, 0 represents an empty square, 2 represents an occupied square and 3
represents the distination square of a move.
*/    
    private int[][] squareNumber={{0x98,0x90,0x91,0x92,0x93,0x94,0x95,0x96,0x97,0x99},
                                   {0x78,0x70,0x71,0x72,0x73,0x74,0x75,0x76,0x77,0x79},
                                   {0x68,0x60,0x61,0x62,0x63,0x64,0x65,0x66,0x67,0x69},
                                   {0x58,0x50,0x51,0x52,0x53,0x54,0x55,0x56,0x57,0x59},
                                   {0x48,0x40,0x41,0x42,0x43,0x44,0x45,0x46,0x47,0x49},
                                   {0x38,0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x39},
                                   {0x28,0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x29},
                                   {0x18,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x19},
                                   {0x08,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x09},
                                   {0x88,0x80,0x81,0x82,0x83,0x84,0x85,0x86,0x87,0x89}};
//Reference matrix used to convert the identifier of each square into coordinates of
//boardstatus and vise varsa.

    public Pathing()
    {
	    boardStatus=new int[10][];
        for(int i=0;i<boardStatus.length;i++)
	    boardStatus[i]=new int[10];
    }

    //public abstract int[] processMove (Move move, Board board);

    
    private int xcoordinate(byte a)
    {    
        for(int i=0;i<squareNumber.length;i++)
            for(int j=0;j<squareNumber[i].length;j++)
                if(squareNumber[i][j]==a) return i;
          return -1;
    }
//Find the x-coordinate of a square given its square number. Return -1
//if no such square exists.

    private int ycoordinate(int a)
    {    for(int i=0;i<squareNumber.length;i++)
             for(int j=0;j<squareNumber[i].length;j++)
                  if(squareNumber[i][j]==a) return j;
         return -1;
    }
//Find the y-coordinate of a square given its square number. Return -1
//if no such square exists.

    private int square(int a,int b)
    {   
         return squareNumber[a][b];
    }
//Return the square number given its x and y coordinates.

    /*private void setBoardStatus(Board b)
    {  
        for(int a=0;a<b.cell.length;a++)
        {  
	    byte position=b.cell[i].pos;
            int xcoor=xcoordinate(position);
            int ycoor=ycoordinate(position);
            boardStatus[x][y]=2;
        }
    }*/
//Map a Board to boardStatus to represent the positions of all pieces.

    private static int moveCounter(Long l)
    {    long n=l.longValue();
         int i=0;
         while(n>0)
         {    
              i++;
              n/=10;
         }
         return i;
    }
//Count the number of movements needed for a calculated path.

    private static int[] bestRoute(Vector v)
    {   
         Long l=(Long)v.elementAt(0);
         int n=moveCounter(l);
         for(int i=0;i<v.size();i++)
         {
             Long a=(Long)v.elementAt(i);
             int m=moveCounter(a);
             if(m<n)
             {
                 l=a;n=m;
             }
         }
         long b=l.longValue();
         int[] c=new int[n];
         while(b>0)
         {
             c[n-1]=(int)b%10;
             b/=10;
             n--;
         }
         return c;
    }    

}
