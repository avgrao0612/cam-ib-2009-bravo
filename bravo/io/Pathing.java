//Pathing is responsible for taking a Move and calculating the best way to move the
//head from one square to another in order to drag a piece to its destination
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
    final static int B = 0x00FF;
    final static byte NE_C=-0x67, NE_S=0x79, NE_W=-0x69;
    final static byte SW_C=-0x78, SW_N=0x08, SW_E=-0x80;
//Some parametres for the corner cases.
    private HWInterface hwi;
    private int previousX=0;
    private int previousY=0;
//Current position of the electromagnet head after the previous move.

    public Pathing(HWInterface hwi)
    {
        this.hwi=hwi;
    }

    public void path(Move move, boolean[] skel)
    {
        if (move.dst == NE_C && !skel[NE_C&B]) 
//To move a captured piece to the northeast corner.
        {
            if (skel[NE_S&B] && skel[NE_W&B] && move.src != NE_S && move.src != NE_W)
            {
                path(new Move(NE_S, NE_C), skel);
                move = new Move(move.src, NE_S);
//If the square next to the northeast corner is occupied, move that piece to the corner first
//then move the captured piece to this corner.
            }
	    }
        else if (move.dst == SW_C && !skel[SW_C&B])
//To move a captured piece to the southwest corner.
        {
            if (skel[SW_N&B] && skel[SW_E&B] && move.src != SW_N && move.src != SW_E)
            {
                path(new Move(SW_N, SW_C), skel);
                move = new Move(move.src, SW_N);
            }
//Same principle as the case above.
	    }
        int[] p1=pathWithMagnetOff(move);
        for(int i=0;i<p1.length;i++)
           hwi.moveHead(p1[i]);
//Move the electromagnetic head under the piece to be moved.
        hwi.magnetSwitch(true);
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        int[] p2=pathWithMagnetOn(move, skel);
        for(int i=0;i<p2.length;i++)
            hwi.moveHead(p2[i]);
//Drag the piece to its destination.
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        hwi.magnetSwitch(false);
        skel[move.dst&B] = true;
        skel[move.src&B] = false;
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
        while(nextX!=previousX&&nextY!=previousY)
        {
            if(nextX>previousX&&nextY>previousY) {previousX++;previousY++;path.addElement(5);}
            else if(nextX>previousX&&nextY<previousY) {previousX++;previousY--;path.addElement(7);}
            else if(nextX<previousX&&nextY>previousY) {previousX--;previousY++;path.addElement(3);}
            else {previousX--;previousY--;path.addElement(1);}
        }
//Move diagonally first towards the next piecel.
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
//This method generates a route to move the electromagnetic head to the piece to be moved. As
//the magnet is off, there is no need to avoid other pieces on the board.

    private int[] pathWithMagnetOn(Move move, boolean[] skel)
    {
            int[][]board=setBoard(move, skel);
            int startX=xcoordinate(move.src);
            int startY=ycoordinate(move.src);
            int endX=xcoordinate(move.dst);
            int endY=ycoordinate(move.dst);
            Vector path=new Vector();
            Vector paths=new Vector();
            bestPath(startX,startY,endX,endY,board,path,paths);
            int[] p=bestRoute(paths);
            previousX=xcoordinate(move.dst);
            previousY=ycoordinate(move.dst);
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
     
     private int distance(int currentX, int currentY, int endX, int endY)
     {
         return Math.abs(endX-currentX)+Math.abs(endY-currentY);
     }
//The number of squares between the current square and the end square.

     private int[][] copyBoard(int[][]board)
     {
        int[][]copy=new int[board.length][board[0].length];
        for(int i=0;i<board.length;i++)
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        return copy;
     }
//Produce a copy of the board status

     private int[] bestDirection(int currentX, int currentY, int endX, int endY, int[][] board)
     {
         int[] distances=new int[8];
         distances[0]=currentX-1>=0&&currentY-1>=0&&board[currentX-1][currentY-1]==0?distance(currentX-1, currentY-1, endX, endY):20;
         distances[1]=currentX-1>=0&&board[currentX-1][currentY]==0?distance(currentX-1,currentY,endX,endY):20;
         distances[2]=currentX-1>=0&&currentY+1<=9&&board[currentX-1][currentY+1]==0?distance(currentX-1,currentY+1,endX,endY):20;
         distances[3]=currentY+1<=9&&board[currentX][currentY+1]==0?distance(currentX,currentY+1,endX,endY):20;
         distances[4]=currentX+1<=9&&currentY+1<=9&&board[currentX+1][currentY+1]==0?distance(currentX+1,currentY+1,endX,endY):20;
         distances[5]=currentX+1<=9&&board[currentX+1][currentY]==0?distance(currentX+1,currentY,endX,endY):20;
         distances[6]=currentX+1<=9&&currentY-1>=0&&board[currentX+1][currentY-1]==0?distance(currentX+1,currentY-1,endX,endY):20;
         distances[7]=currentY-1>=0&&board[currentX][currentY-1]==0?distance(currentX,currentY-1,endX,endY):20;
//The distances from the squares adjacent to the current square to the end square.
         int direction=0;
         for(int i=0;i<distances.length;i++)
         if(distances[i]<distances[direction]) direction=i;
//the shortest distance from the next square to the end square.
         direction=distances[direction]<20?direction:-1;
//If all the distances are 20, the path is blocked and no valid route can be found.
         if(direction==-1) return null;
//Return null if no valid path is possible.
         int[] nextSquares=new int[8];
         int minimumDistance=distances[direction];
         for(int i=0;i<distances.length;i++)
             if(distances[i]==minimumDistance) nextSquares[i]=1;
//All the adjacent squares that provides a shorter route in the next step are marked as 1.
         return nextSquares;
     }
//This method tests all 8 squares anound the current square and decides which of them provides the shortest
//route to the end square in the next step.

     private void bestPath(int currentX, int currentY, int endX, int endY, int[][] board, Vector path, Vector paths)
     {
         int isPathComplete=isEndReached(board,currentX,currentY);
         if(isPathComplete>0) {path.add(isPathComplete);paths.add(path);}
//If the end square can be reached in the next step, store the direction and store the path.
         else
         {
             int[] direction=bestDirection(currentX, currentY, endX, endY, board);
             if(direction[0]==1)
             {
                 int[][] b=copyBoard(board);
                 Vector p=(Vector)path.clone();
                 p.add(1);
                 b[currentX-1][currentY-1]=1;
                 bestPath(currentX-1,currentY-1,endX,endY,b,p,paths);
             }
             if(direction[1]==1)
             {
                 int[][] b=copyBoard(board);
                 Vector p=(Vector)path.clone();
                 p.add(2);
                 b[currentX-1][currentY]=1;
                 bestPath(currentX-1,currentY,endX,endY,b,p,paths);
             }
             if(direction[2]==1)
             {
                 int[][] b=copyBoard(board);
                 Vector p=(Vector)path.clone();
                 p.add(3);
                 b[currentX-1][currentY+1]=1;
                 bestPath(currentX-1,currentY+1,endX,endY,b,p,paths);
             }
             if(direction[3]==1)
             {
                 int[][] b=copyBoard(board);
                 Vector p=(Vector)path.clone();
                 p.add(4);
                 b[currentX][currentY+1]=1;
                 bestPath(currentX,currentY+1,endX,endY,b,p,paths);
             }
             if(direction[4]==1)
             {
                 int[][] b=copyBoard(board);
                 Vector p=(Vector)path.clone();
                 p.add(5);
                 b[currentX+1][currentY+1]=1;
                 bestPath(currentX+1,currentY+1,endX,endY,b,p,paths);
             }
             if(direction[5]==1)
             {
                 int[][] b=copyBoard(board);
                 Vector p=(Vector)path.clone();
                 p.add(6);
                 b[currentX+1][currentY]=1;
                 bestPath(currentX+1,currentY,endX,endY,b,p,paths);
             }
             if(direction[6]==1)
             {
                 int[][] b=copyBoard(board);
                 Vector p=(Vector)path.clone();
                 p.add(7);
                 b[currentX+1][currentY-1]=1;
                 bestPath(currentX+1,currentY-1,endX,endY,b,p,paths);
             }
             if(direction[7]==1)
             {
                 int[][] b=copyBoard(board);
                 Vector p=(Vector)path.clone();
                 p.add(8);
                 b[currentX][currentY-1]=1;
                 bestPath(currentX,currentY-1,endX,endY,b,p,paths);
             }
//The squares that provides shortest route in the next step are listed and recursion are
//carried on from these squares.
         }
     }

    private int[] bestRoute(Vector v)
    {
         if(v.size()==0) return null;
//Return null if no route available.
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
//Pick up the route with the minimum number of directions
             int[] c=new int[path.size()];
             for(int i=0;i<path.size();i++)
             {
                 Integer a=(Integer)path.elementAt(i);
                 c[i]=a.intValue();
             }
//Return the route as an integer array.
             return c;
         }
    }
//This method takes a series of routes and find the shortest amoung all. If multiple shortest
//routes are available it just pick the first one.
}
