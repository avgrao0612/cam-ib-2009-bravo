//Pathing is responsible for taking a series of Moves and calculating the best way to
//move the head from one square to another in order to drag a piece to its destination
//quickly and undisturbed.
package bravo.io;

import java.util.Vector;
import bravo.game.Move;
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
For each element, 0 represents an empty square, 1 represents the seuare in the path,
2 represents an occupied square and 3represents the distination square of a move.
*/
    private HWInterface hwi;
    private int previousX=0;
    private int previousY=0;

    public Pathing(HWInterface hwi)
    {
        this.hwi=hwi;
    }

    final static int B = 0x00FF;
    final static byte NE_C=-0x67, NE_S=0x79, NE_W=-0x69;
    final static byte SW_C=-0x78, SW_N=0x08, SW_E=-0x80;

    public void path(Move move, boolean[] skel)
    {
        // fix corner cases
        if (move.dst == NE_C && !skel[NE_C&B]) {
            if (skel[NE_S&B] && skel[NE_W&B] && move.src != NE_S && move.src != NE_W) {
                path(new Move(NE_S, NE_C), skel);
                skel[NE_C&B] = true;
                skel[NE_S&B] = false;
                move = new Move(move.src, NE_S);
                System.out.println("Remove a piece to the top right corner");
            }
	} else if (move.dst == SW_C && !skel[SW_C&B]) {
            if (skel[SW_N&B] && skel[SW_E&B] && move.src != SW_N && move.src != SW_E) {
                path(new Move(SW_N, SW_C), skel);
                skel[SW_C&B] = true;
                skel[SW_N&B] = false;
                move = new Move(move.src, SW_N);
                System.out.println("Remove a piece to the bottom left corner");
            }
	}
        int[] p1=pathWithMagnetOff(move);
        System.out.println("Moving route complete");
        System.out.println("Magnet off");
        for(int i=0;i<p1.length;i++)
            hwi.moveHead(p1[i]);
        System.out.println("Move commence");
//Move the magnetic head to the piece to be moved.
        hwi.magnetSwitch(true);
        System.out.println("Magnet on");
        int[] p2=pathWithMagnetOn(move, skel);
        System.out.println("Dragging route complete");
        for(int i=0;i<p2.length;i++)
            hwi.moveHead(p2[i]);
        System.out.println("Dragging complete");
//Drag the piece to its destination.
        hwi.magnetSwitch(false);
    }
//The method to be called to invoke a complete move.

    public void reset()
    {
        hwi.reset();
	    hwi.offset_h();
        previousX=0;
        previousY=0;
    }
 //Set the magnetic head back to the starting position.
    
    private int[] pathWithMagnetOff(Move move)
    {
        int nextX=xcoordinate(move.src);
        int nextY=ycoordinate(move.src);
        Vector path=new Vector();
        while(nextX!=previousX)
        {
            if(nextX>previousX) {previousX++;path.addElement(6);}
            else {previousX--;path.addElement(2);}
        }
        while(nextY!=previousY)
        {
            if(nextY>previousY) {previousY++;path.addElement(4);}
            else {previousY--;path.addElement(8);}
        }
        int[] p=new int[path.size()];
        for(int i=0;i<path.size();i++)
        {
            Integer a=(Integer)path.elementAt(i);
            p[i]=a.intValue();
        }
        return p;
    }

    private int[] pathWithMagnetOn(Move move, boolean[] skel)
    {
            int[][]board=setBoard(move, skel);
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
            previousX=xcoordinate(move.dst);
            previousY=ycoordinate(move.dst);
            int[] p=bestRoute(paths);
            return p;
    }
//This method takes a Move and return an int array containing a series of
//individual movement to achieve it. It returns null if there is no valid
//path that can be generated, though theoretically that should never happen
//as long as the game is played in a resonable manner.

    private int xcoordinate(byte a)
    {
        int x = (a >> 4) & 0x0F;
        return (x < 8)? 8-x: (x == 8)? 9: (x == 9)? 0: -1;
    }
//Find the x-coordinate of a square given its square number. Return -1
//if no such square exists.

    private int ycoordinate(byte a)
    {
        int y = a & 0x0F;
        return (y < 8)? y+1: (y == 8)? 0: (y == 9)? 9: -1;
    }
//Find the y-coordinate of a square given its square number. Return -1
//if no such square exists.

    private int[][] setBoard(Move move, boolean[] skel)
    {
        int[][] board=new int[10][10];
        for(int i=0;i<256;++i) {
           int x = xcoordinate((byte)i), y = ycoordinate((byte)i);
           if(x>=0 && y>=0 && skel[i]) board[x][y]=2;
        }
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
 /*   public static void main(String[] args)
    {
        Move m=new Move((byte)0x36,(byte)0x58);
        Pathing p=new Pathing();
        int[] a=p.path(m);
        for(int i=0;i<a.length;i++)
            System.out.print(a[i]+" ");
        System.out.println();
    }*/
//Some tests. Have this removed when implementing it.
}

