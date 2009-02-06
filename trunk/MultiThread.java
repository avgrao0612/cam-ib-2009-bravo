/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Tests;

/**
 *
 * @author Zheng Guo
 */
import java.util.Vector;
class GoToTop extends Thread
{    int[][] board;
     int x;
     int y;
     long path;
     Vector paths;
     public GoToTop(int[][] board,int x,int y,long path,Vector paths)
     {    this.board=board;
          this.x=x;
          this.y=y;
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
         if(x<0||x>=board.length||y<0||y>=board[x].length) return;
          else if(isPathComplete!=0)
        { path*=10;
            path+=isPathComplete;
             paths.addElement(path);
        }
        else
        {    GoToTop g1=null;
             GoToTop g2=null;
             GoToTop g3=null;

             if(x-1>=0&&board[x-1][y]==0)
             {
                  g1=new GoToTop(board,x-1,y,path*10+2,paths);
                  g1.start();
             }
             if(x-1>=0&&y-1>=0&&board[x-1][y-1]==0)
             {   
                  g2=new GoToTop(board,x-1,y-1,path*10+1,paths);
                  g2.start();
             }

             if(x-1>=0&&y+1<board[x-1].length&&board[x-1][y+1]==0)
             {   
                 
                  g3=new GoToTop(board,x-1,y+1,path*10+3,paths);
                  g3.start();
             }

             try
             {if(g1!=null) g1.join();
              if(g2!=null) g2.join();
              if(g3!=null) g3.join();
              }
             catch(InterruptedException e)
               {
               }
        }
     }
}
class GoToRight extends Thread
{    int[][] board;
     int x;
     int y;
     long path;
     Vector paths;
     public GoToRight(int[][] board,int x,int y,long path,Vector paths)
     {    this.board=board;
          this.x=x;
          this.y=y;
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
         if(x<0||x>=board.length||y<0||y>=board[x].length) return;
          else if(isPathComplete!=0)
        { path*=10;
            path+=isPathComplete;
             paths.addElement(path);
        }
        else
        {    GoToRight g1=null;
             GoToRight g2=null;
             GoToRight g3=null;

             if(y+1<board[x].length&&board[x][y+1]==0)
             {   
                  g1=new GoToRight(board,x,y+1,path*10+4,paths);
                  g1.start();
             }
             if(x-1>=0&&y+1<board[x-1].length&&board[x-1][y+1]==0)
             {   
                  g2=new GoToRight(board,x-1,y+1,path*10+3,paths);
                  g2.start();
             }
              if(x+1<board.length&&y+1<board[x+1].length&&board[x+1][y+1]==0)
             {   
                  g3=new GoToRight(board,x+1,y+1,path*10+5,paths);
                  g3.start();
             }

             try
             {if(g1!=null) g1.join();
              if(g2!=null) g2.join();
              if(g3!=null) g3.join();
              }
             catch(InterruptedException e)
               {
               }
        }
     }
}

class GoToBottom extends Thread
{    int[][] board;
     int x;
     int y;
     long path;
     Vector paths;
     public GoToBottom(int[][] board,int x,int y,long path,Vector paths)
     {    this.board=board;
          this.x=x;
          this.y=y;
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
         if(x<0||x>=board.length||y<0||y>=board[x].length) return;
          else if(isPathComplete!=0)
        { path*=10;
            path+=isPathComplete;
             paths.addElement(path);
        }
        else
        {    GoToBottom g1=null;
             GoToBottom g2=null;
             GoToBottom g3=null;

             if(x+1<board.length&&board[x+1][y]==0)
             {   
                  g1=new GoToBottom(board,x+1,y,path*10+6,paths);
                  g1.start();
             }
             if(x+1<board.length&&y+1<board[x+1].length&&board[x+1][y+1]==0)
             {   
                  g2=new GoToBottom(board,x+1,y+1,path*10+5,paths);
                  g2.start();
             }
             if(x+1<board.length&&y-1>=0&&board[x+1][y-1]==0)
             {   
                  g3=new GoToBottom(board,x+1,y-1,path*10+7,paths);
                  g3.start();
             }

             try
             {if(g1!=null) g1.join();
              if(g2!=null) g2.join();
              if(g3!=null) g3.join();
              }
             catch(InterruptedException e)
               {
               }
        }
     }

}

class GoToLeft extends Thread
{    int[][] board;
     int x;
     int y;
     long path;
     Vector paths;
     public GoToLeft(int[][] board,int x,int y,long path,Vector paths)
     {    this.board=board;
          this.x=x;
          this.y=y;
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
         if(x<0||x>=board.length||y<0||y>=board[x].length) return;
          else if(isPathComplete!=0)
        { path*=10;
            path+=isPathComplete;
             paths.addElement(path);
        }
        else
        {    GoToLeft g1=null;
             GoToLeft g2=null;
             GoToLeft g3=null;

             if(y-1>=0&&board[x][y-1]==0)
             {   
                  g1=new GoToLeft(board,x,y-1,path*10+8,paths);
                  g1.start();
             }
             if(x+1<board.length&&y-1>=0&&board[x+1][y-1]==0)
             {  
                  g2=new GoToLeft(board,x+1,y-1,path*10+7,paths);
                  g2.start();
             }
             if(x-1>=0&&y-1>=0&&board[x-1][y-1]==0)
             {   
                  g3=new GoToLeft(board,x-1,y-1,path*10+1,paths);
                  g3.start();
             }


             try
             {if(g1!=null) g1.join();
              if(g2!=null) g2.join();
              if(g3!=null) g3.join();
              }
             catch(InterruptedException e)
               {
               }
        }
     }


}

public class MultiThread
{    private int[][] squareNumber={{0x98,0x90,0x91,0x92,0x93,0x94,0x95,0x96,0x97,0x99},
                                   {0x78,0x70,0x71,0x72,0x73,0x74,0x75,0x76,0x77,0x79},
                                   {0x68,0x60,0x61,0x62,0x63,0x64,0x65,0x66,0x67,0x69},
                                   {0x58,0x50,0x51,0x52,0x53,0x54,0x55,0x56,0x57,0x59},
                                   {0x48,0x40,0x41,0x42,0x43,0x44,0x45,0x46,0x47,0x49},
                                   {0x38,0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x39},
                                   {0x28,0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x29},
                                   {0x18,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x19},
                                   {0x08,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x09},
                                   {0x88,0x80,0x81,0x82,0x83,0x84,0x85,0x86,0x87,0x89}};


     private int xcoordinate(int a)
     {    for(int i=0;i<squareNumber.length;i++)
              for(int j=0;j<squareNumber[i].length;j++)
                   if(squareNumber[i][j]==a) return i;

          return -1;
     }

     private int ycoordinate(int a)
     {    for(int i=0;i<squareNumber.length;i++)
              for(int j=0;j<squareNumber[i].length;j++)
                   if(squareNumber[i][j]==a) return j;

          return -1;

     }

     private int square(int a,int b)
     {    return squareNumber[a][b];
     }

     private static int moveCounter(Long l)
     {    long n=l.longValue();
          int i=0;
          while(n>0)
          {    i++;
               n/=10;

          }
          return i;
     }
     private static int[] bestRoute(Vector v)
     {    Long l=(Long)v.elementAt(0);
          int n=moveCounter(l);
          for(int i=0;i<v.size();i++)
     {    Long p=(Long)v.elementAt(i);
          int m=moveCounter(p);
          if(m<n) {l=p; n=m;}
     }
          long a=l.longValue();
          int[] b=new int[n];
          for(int i=n-1;i>=0;i--)
          {    b[i]=(int)a%10;
               a/=10;
          }
     return b;

     }
    static int direction(int startX, int startY, int endX, int endY)
 {    if(endY==startY)
      {    if(endX<startX) return 1;
           else return 3;
      }
      else if((endX-startX)/(endY-startY)<1&&(endX-startX)/(endY-startY)>=-1)
      {    if(endY<startY) return 4;
           else return 2;
      }
      else
      {    if(endY<startY) return 1;
           else return 3;
      }
 }
static int getStartX(int[][]board)
{    for(int i=0;i<board.length;i++)
     {    for(int j=0;j<board[i].length;j++)
          {    if(board[i][j]==1) return i;

          }
     }
     return -1;
}
static int getStartY(int[][]board)
{    for(int i=0;i<board.length;i++)
     {    for(int j=0;j<board[i].length;j++)
          {    if(board[i][j]==1) return j;

          }
     }
     return -1;
}
static int getEndX(int[][]board)
{    for(int i=0;i<board.length;i++)
     {    for(int j=0;j<board[i].length;j++)
          {    if(board[i][j]==3) return i;

          }
     }
     return -1;
}
static int getEndY(int[][]board)
{    for(int i=0;i<board.length;i++)
     {    for(int j=0;j<board[i].length;j++)
          {    if(board[i][j]==3) return j;

          }
     }
     return -1;
}
public static void main(String[] args)
{    int[][] board={{0,0,0,0,0,0,0,0},
                       {0,1,0,2,0,0,0,0},
                       {0,0,2,0,0,0,0,0},
                       {0,0,0,2,0,0,0,0},
                       {0,0,0,0,0,0,0,0},
                       {0,0,0,0,0,0,0,0},
                       {0,0,0,0,0,0,0,0},
                       {0,0,0,0,0,3,0,0},
                                        };
     long path=0L;
     Vector paths=new Vector();
     int d=direction(getStartX(board),getStartY(board),getEndX(board),getEndY(board));
     GoToTop g1=null;
     GoToRight g2=null;
     GoToBottom g3=null;
     GoToLeft g4=null;
     switch(d)
     {    case 1: g1=new GoToTop(board,getStartX(board),getStartY(board),path,paths);g1.start();break;
         case 2: g2= new GoToRight(board,getStartX(board),getStartY(board),path,paths);g2.start();break;
         case 3: g3=new GoToBottom(board,getStartX(board),getStartY(board),path,paths);g3.start();break;
         case 4: g4=new GoToLeft(board,getStartX(board),getStartY(board),path,paths);g4.start();break;
     }
         
     try
     {if (g1!=null) g1.join();
      if (g2!=null) g2.join();
      if (g3!=null) g3.join();
      if (g4!=null) g4.join();

     }
     catch(InterruptedException e)
     {
     }
     int[] a=bestRoute(paths);
     for(int i=0;i<a.length;i++)
         System.out.print(a[i]+" ");
     System.out.println();
}
}
