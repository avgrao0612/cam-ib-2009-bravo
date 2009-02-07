//Pathing is responsible for taking a series of Moves and calculating the best way to
//move the head from one square to another in order to drag a piece to its destination
//quickly and undisturbed. 
package bravo.game;

import java.util.Vector;
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
            Vector path=new Vector();
            Vector paths=new Vector();
            
            switch(direction(move))
            {
                case 1:goToTop(board,startX,startY,endX,path,paths);break;
                //Recursion towards the top
                case 2:goToRight(board,startX,startY,endY,path,paths);break;
                //Recursion towards the right
                case 3:goToBottom(board,startX,startY,endX,path,paths);break;
                //Recursion towards the bottom
                case 4:goToLeft(board,startX,startY,endY,path,paths);break;
                //Recursion towards the right
            }
            int[] p=bestRoute(paths);
            return p;
        }
    }
//This method takes a Move and return an int array containing a series of
//individual movement to achieve it. It returns null if there is no valid
//path that can be generated, though theoretically that should never happen
//as long as the game is played in a resonable manner.
    
    private int xcoordinate(int a)
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

    private int[][] setBoard(Move move)
    {  
        int[][] board=new int[10][];
        for(int i=0;i<board.length;i++)
           board[i]=new int[10];
        for(int i=0;i<board.length;i++)
          for(int j=0;j<board[i].length;j++)
             if((i+j)%2==1&&i!=0&&i!=board.length-1&&j!=0&&j!=board[i].length-1) board[i][j]=2;
        board[xcoordinate(move.src)][ycoordinate(move.src)]=1;
        board[xcoordinate(move.dst)][ycoordinate(move.dst)]=3;
        return board;
    }
//Map a Board to boardStatus to represent the positions of all pieces.
//All black square are assumed to be occupied.

    private int[][] copyBoard(int[][]board)
    {
        int[][]copy=new int[board.length][board[0].length];
        for(int i=0;i<board.length;i++)
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        return copy;
    }
//Produce a copy of the board status

    private int isEndReached(int[][]board,int x,int y)
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

    private void goToTop(int[][]board,int x,int y,int topBound,Vector path,Vector paths)
    {
        int isPathComplete=isEndReached(board,x,y);
        if(path.size()>99) return;
//The maximum step a path can take. Any one beyond this is discarded.
        else if(isPathComplete!=0)
         {
            path.add(isPathComplete);
            paths.addElement(path);
         }
/*If the distination can be reached at the next move, store this move and add this
  path to the set of all valid paths.
*/
        else if(x<topBound) return;
        else
        {
            if(x-1>=0&&board[x-1][y]==0)
              {
		           int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(2);
                   goToTop(copy,x-1,y,topBound,p,paths);
                   //Recursion towards top
              }
              if(x-1>=0&&y-1>=0&&board[x-1][y-1]==0)
              {
		           int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(1);
                   goToTop(copy,x-1,y-1,topBound,p,paths);
                   //Recursion towards topleft
              }
              if(x-1>=0&&y+1<board[x-1].length&&board[x-1][y+1]==0)
              {
		           int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(3);
                   goToTop(copy,x-1,y+1,topBound,p,paths);
                   //Recursion towards topright
              }
              if(y-1>=0&&board[x][y-1]==0)
              {
                  int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(8);
                   goToTop(copy,x,y-1,topBound,p,paths);
                   //Recursion towards left
              }
              if(y+1<board[x].length&&board[x][y+1]==0)
              {
                   int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(4);
                   goToTop(copy,x,y+1,topBound,p,paths);
                   //Recursion towards right
              }
        }
    }

    private void goToRight(int[][]board,int x,int y,int rightBound,Vector path,Vector paths)
    {
        int isPathComplete=isEndReached(board,x,y);
        if(path.size()>99) return;
        else if(isPathComplete!=0)
         {
            path.add(isPathComplete);
            paths.addElement(path);
         }
        else if(y>rightBound) return;
        else
        {
            if(y+1<board[x].length&&board[x][y+1]==0)
              {
		           int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(4);
                   goToRight(copy,x,y+1,rightBound,p,paths);
                   //Recursion towards right
              }
              if(x-1>=0&&y+1<board[x-1].length&&board[x-1][y+1]==0)
              {
		           int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(3);
                   goToRight(copy,x-1,y+1,rightBound,p,paths);
                   //Recursion towards topright
              }
              if(x+1<board.length&&y+1<board[x+1].length&&board[x+1][y+1]==0)
              {
		           int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(5);
                   goToRight(copy,x+1,y+1,rightBound,p,paths);
                   //Recursion towards bottomright
              }
              if(x-1>=0&&board[x-1][y]==0)
              {
                   int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(2);
                   goToRight(copy,x-1,y,rightBound,p,paths);
                   //Recursion towards top
              }
              if(x+1<board.length&&board[x+1][y]==0)
              {
                   int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(6);
                   goToRight(copy,x+1,y,rightBound,p,paths);
                   //Recursion towards bottom
              }
        }
    }
    private void goToBottom(int[][]board,int x,int y,int bottomBound,Vector path,Vector paths)
    {
        int isPathComplete=isEndReached(board,x,y);
        if(path.size()>99) return;
        else if(isPathComplete!=0)
         {
            path.add(isPathComplete);
            paths.addElement(path);
         }
        else if(x>bottomBound) return;
        else
        {
            if(x+1<board.length&&board[x+1][y]==0)
               {
                   int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(6);
                   goToBottom(copy,x+1,y,bottomBound,p,paths);
                   //Recursion towards the bottom
               }
               if(x+1<board.length&&y+1<board[x+1].length&&board[x+1][y+1]==0)
               {
                   int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(5);
                   goToBottom(copy,x+1,y+1,bottomBound,p,paths);
                   //Recursion towards the bottomright
               }
               if(x+1<board.length&&y-1>=0&&board[x+1][y-1]==0)
               {
                   int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(7);
                   goToBottom(copy,x+1,y-1,bottomBound,p,paths);
                   //Recursion towards the bottomleft
               }
               if(y+1<board[x].length&&board[x][y+1]==0)
               {
                   int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(4);
                   goToBottom(copy,x,y+1,bottomBound,p,paths);
                   //Recursion towards the right
               }
               if(y-1>=0&&board[x][y-1]==0)
               {
                   int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(8);
                   goToBottom(copy,x,y-1,bottomBound,p,paths);
                   //Recursion towards the left
               }
        }
    }

    private void goToLeft(int[][]board,int x,int y,int leftBound,Vector path,Vector paths)
    {
        int isPathComplete=isEndReached(board,x,y);
        if(path.size()>99) return;
        else if(isPathComplete!=0)
         {
            path.add(isPathComplete);
            paths.addElement(path);
         }
        else if(y<leftBound) return;
        else
        {
            if(y-1>=0&&board[x][y-1]==0)
              {
                  int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(8);
                   goToLeft(copy,x,y-1,leftBound,p,paths);
                  //Recursion towards the right
              }
              if(x+1<board.length&&y-1>=0&&board[x+1][y-1]==0)
              {
                  int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(7);
                   goToLeft(copy,x+1,y-1,leftBound,p,paths);
                   //Recursion towards the bottomright
              }
              if(x-1>=0&&y-1>=0&&board[x-1][y-1]==0)
              {
                  int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(1);
                   goToLeft(copy,x-1,y-1,leftBound,p,paths);
                   //Recursion towards the topright
              }
              if(x+1<board.length&&board[x+1][y]==0)
              {
                  int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(6);
                   goToLeft(copy,x+1,y,leftBound,p,paths);
                  //Recursion towards the bottom
              }
              if(x-1>=0&&board[x-1][y]==0)
              {
                  int[][]copy=copyBoard(board);
                   copy[x][y]=1;
                   Vector p=(Vector)path.clone();
                   p.add(2);
                   goToLeft(copy,x-1,y,leftBound,p,paths);
                  //Recursion towards the top
              }
        }
    }
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

    private int[] bestRoute(Vector v)
    {   
         if(v.size()==0) return null;
         else
         {
             Vector path=(Vector)v.elementAt(0);
             int n=path.size();
             for(int i=0;i<v.size();i++)
             {
                 Vector p=(Vector)v.elementAt(i);
                 int m=p.size();
                 if(m<n){path=p;n=m;}
             }
             int[] c=new int[path.size()];
             for(int i=0;i<path.size();i++)
             {
                 Integer a=(Integer)path.elementAt(i);
                 c[i]=a.intValue();
             }
             return c;
         }
    }    
//Find the shortest path among all paths. Return null if no valid paths available
    public static void main(String[] args)
    {
        Move m=new Move(0x08,0x79);
        Pathing p=new Pathing();
        int[] a=p.path(m);
        for(int i=0;i<a.length;i++)
            System.out.print(a[i]+" ");
        System.out.println();
    }
}
