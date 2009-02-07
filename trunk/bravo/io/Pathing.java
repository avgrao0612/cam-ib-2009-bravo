//Pathing is responsible for taking a series of Moves and calculating the best way to
//move the head from one square to another in order to drag a piece to its destination
//quickly and undisturbed. Concurrency is added to maximise calculating speed.
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
  LEFT   8 | 0 | 4   RIGHT
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
              GoToTop g4=null;
              GoToTop g5=null;

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
              if(y-1>=0&&board[x][y-1]==0)
              {
                   g4=new GoToTop(board,x,y-1,topBound,path*10+8,paths);
                   g4.start();  //Recursion towards left
              }
              if(y+1<board[x].length&&board[x][y+1]==0)
              {
                   g5=new GoToTop(board,x,y+1,topBound,path*10+4,paths);
                   g5.start();  //Recursion towards right
              }
              try
              {
                  if(g1!=null) g1.join();
                  if(g2!=null) g2.join();
                  if(g3!=null) g3.join();
                  if(g4!=null) g4.join();
                  if(g5!=null) g5.join();
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
              GoToRight g4=null;
              GoToRight g5=null;

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
              if(x-1>=0&&board[x-1][y]==0)
              {
                   g4=new GoToRight(board,x-1,y,rightBound,path*10+2,paths);
                   g4.start();  //Recursion towards top
              }
              if(x+1<board.length&&board[x+1][y]==0)
              {
                   g5=new GoToRight(board,x+1,y,rightBound,path*10+6,paths);
                   g5.start();  //Recursion towards bottom
              }
              try
              {
                  if(g1!=null) g1.join();
                  if(g2!=null) g2.join();
                  if(g3!=null) g3.join();
                  if(g4!=null) g4.join();
                  if(g5!=null) g5.join();
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
               GoToBottom g4=null;
               GoToBottom g5=null;

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
               if(y+1<board[x].length&&board[x][y+1]==0)
               {
                   g4=new GoToBottom(board,x,y+1,bottomBound,path*10+4,paths);
                   g4.start();  //Recursion towards the right
               }
               if(y-1>=0&&board[x][y-1]==0)
               {
                   g5=new GoToBottom(board,x,y-1,bottomBound,path*10+8,paths);
                   g5.start();  //Recursion towards the left
               }

               try
               {
                   if(g1!=null) g1.join();
                   if(g2!=null) g2.join();
                   if(g3!=null) g3.join();
                   if(g4!=null) g4.join();
                   if(g5!=null) g5.join();
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
              GoToLeft g4=null;
              GoToLeft g5=null;
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
              if(x+1<board.length&&board[x+1][y]==0)
              {
                  g4=new GoToLeft(board,x+1,y,leftBound,path*10+6,paths);
                  g4.start();  //Recursion towards the bottom
              }
              if(x-1>=0&&board[x-1][y]==0)
              {
                  g5=new GoToLeft(board,x-1,y,leftBound,path*10+2,paths);
                  g5.start();  //Recursion towards the top
              }

              try
              {
                  if(g1!=null) g1.join();
                  if(g2!=null) g2.join();
                  if(g3!=null) g3.join();
                  if(g4!=null) g4.join();
                  if(g5!=null) g5.join();
              }
              catch(InterruptedException e){}
        }
     }
}

public class Pathing
{
/*Coordinate system of boardStatus:
                   White Player
       0   1   2   3   4   5   6   7   8   9  y
      _______________________________________
  0  |___|___|___|___|___|___|___|___|___|___|
  1  |___|_W_|_B_|_W_|_B_|_W_|_B_|_W_|_B_|___|
  2  |___|_B_|_W_|_B_|_W_|_B_|_W_|_B_|_W_|___|
  3  |___|_W_|_B_|_W_|_B_|_W_|_B_|_W_|_B_|___|
  4  |___|_B_|_W_|_B_|_W_|_B_|_W_|_B_|_W_|___|    W=White Square
  5  |___|_W_|_B_|_W_|_B_|_W_|_B_|_W_|_B_|___|    B=Black Square
  6  |___|_B_|_W_|_B_|_W_|_B_|_W_|_B_|_W_|___|
  7  |___|_W_|_B_|_W_|_B_|_W_|_B_|_W_|_B_|___|
  8  |___|_B_|_W_|_B_|_W_|_B_|_W_|_B_|_W_|___|
  9  |___|___|___|___|___|___|___|___|___|___|
  x                Black Player
For each element, 0 represents an empty square, 2 represents an occupied square and 3
represents the distination square of a move.
*/

    public int[] path (Move move)
    {
        if(move.src==move.dst)
        {
            int[] a={0};
            return a;
//If the source and destination of a move is the same, return 0 to show that it does not move
        }
        else
        {
            int[][]board=setBoard(move);
            int startX=xcoordinate(move.src);
            int startY=ycoordinate(move.src);
            int endX=xcoordinate(move.dst);
            int endY=ycoordinate(move.dst);
            Vector paths=new Vector();
            GoToTop g1=null;
            GoToRight g2=null;
            GoToBottom g3=null;
            GoToLeft g4=null;
            switch(direction(move))
            {
                case 1:g1=new GoToTop(board,startX,startY,endX,0L,paths);g1.start();break;
//Recursion towards the top
                case 2:g2=new GoToRight(board,startX,startY,endY,0L,paths);g2.start();break;
//Recursion towards the right
                case 3:g3=new GoToBottom(board,startX,startY,endX,0L,paths);g3.start();break;
//Recursion towards the bottom
                case 4:g4=new GoToLeft(board,startX,startY,endY,0L,paths);g4.start();break;
//Recursion towards the right
            }
            try
            {
                if (g1!=null) g1.join();
                if (g2!=null) g2.join();
                if (g3!=null) g3.join();
                if (g4!=null) g4.join();
            }
            catch(InterruptedException e){}
            int[] path=bestRoute(paths);
            return path;
        }
    }
//This method takes a Move and return an int array containing a series of
//individual movement to achieve it. It returns null if there is no valid
//path that can be generated, though theoretically that should never happen
//as long as the game is played in a resonable manner.

    private int xcoordinate(byte a)
    {
        int x = a & 0x0F;
        return (x < 8)? x+1: (x == 8)? 0: (x == 9)? 9: -1;
    }
//Find the x-coordinate of a square given its square number. Return -1
//if no such square exists.

    private int ycoordinate(byte a)
    {
        int y = a >>> 4;
        return (y < 8)? y+1: (y == 8)? 0: (y == 9)? 9: -1;
    }
//Find the y-coordinate of a square given its square number. Return -1
//if no such square exists.

    private int[][] setBoard(Move move)
    {
        int[][] board=new int[10][];
        for(int i=0;i<board.length;i++)
           board[i]=new int[10];
        for(int i=0;i<board.length;i++)
          for(int j=0;j<board[i].length;j++)
             if((i+j)%2==1&&i!=0&&i!=board.length-1&&j!=0&&j!=board[i].length-1) board[i][j]=2;
        board[xcoordinate(move.dst)][ycoordinate(move.dst)]=3;
        return board;
    }
//Map a Board to boardStatus to represent the positions of all pieces.
//All black square are assumed to be occupied.

    private int direction(Move move)
    {
        int startX=xcoordinate(move.src);
        int startY=ycoordinate(move.src);
        int endX=xcoordinate(move.dst);
        int endY=ycoordinate(move.dst);
        if(endY==startY)
        {
            if(endX<startX) return 1;
            else return 3;
        }
        else if((endX-startX)/(endY-startY)<1&&(endX-startX)/(endY-startY)>=-1)
        {
            if(endY<startY) return 4;
            else return 2;
        }
        else
        {
            if(endX<startX) return 1;
            else return 3;
        }
    }
/*The area around a piece is split into 4 areas:
             \ TOP /
              \ 1 /
               \_/
     LEFT  4   |0|   2  RIGHT
               /-\
              / 3 \
             /     \
             BOTTOM
  Recursion only takes place in one of the 4 areas depending on the
  alignment of the start and destination.
*/

    private int moveCounter(Long l)
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

    private int[] bestRoute(Vector v)
    {
         if(v.size()==0) return null;
         else
         {
             Long l=(Long)v.elementAt(0);
             int n=moveCounter(l);
             for(int i=0;i<v.size();i++)
             {
                 Long a=(Long)v.elementAt(i);
                 int m=moveCounter(a);
                 if(m<n){l=a;n=m;}
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
//Find the shortest path among all paths. Return null if no valid paths available
    public static void main(String[] args)
    {
        Move m=new Move((byte)0x40,(byte)0x22);
        Pathing p=new Pathing();
        int[] a=p.path(m);
        for(int i=0;i<a.length;i++)
            System.out.print(a[i]+" ");
        System.out.println();
    }
}
