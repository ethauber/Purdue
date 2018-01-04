import java.util.*;

public class Fast{
 private static Point[] PointsArray;
 private static Out outF;

 public static void printCol(Point[] colP, int floor, int ceiling) {
     StdOut.print((ceiling-floor)+1 +":");
     outF.print((ceiling-floor)+1+":");
     StdOut.print(colP[0].getPAS());
     outF.print(colP[0].getPAS());
     for(int mop = floor; mop < ceiling; mop++) {
         StdOut.print(" -> ");
         StdOut.print(colP[mop].getPAS());
         outF.print(" -> ");
         outF.print(colP[mop].getPAS());
     }
     StdOut.println();
     outF.println();
 }

 public static void main(String[] args) {
	outF = new Out("visualPoints.txt");
  	//read size for array and initialize
  	int n = StdIn.readInt();
  	PointsArray = new Point[n];
  	Point[] copyP = new Point[n];
  	//iterate through rest of file creating points
  	int i = 0;
  	while(!StdIn.isEmpty()) {
   		int x = StdIn.readInt();
   		int y = StdIn.readInt();
   		Point newP = new Point(x,y);
   		copyP[i] = newP;
   		PointsArray[i++] = newP;
  	}
  	//Check for Collinearity (Fast Alg)
  	for(int a = 0; a < n; a++) {
        Arrays.sort(PointsArray, copyP[a].BY_SLOPE_ORDER);
        //debug printing
        //System.out.format("a=%d_copyP[%d]=(%d,%d)%n", a, a, copyP[a].getX(), copyP[a].getY());
        //for (int ni = 0; ni < n; ni++) {
        //    System.out.format("(%d,%d)--", PointsArray[ni].getX(), PointsArray[ni].getY());
        //}
        //System.out.format("%n%n");
        //---
        int floor = 1;
        do
        {
            int count = 1;
            double floorSlope = copyP[a].getSlope(PointsArray[floor]);
            int ceiling = floor+1;
            while (ceiling < n && copyP[a].getSlope(PointsArray[ceiling]) == floorSlope)
            {
                count++;
                ceiling++;
            }

            if (count >= 3)
            {
                Arrays.sort(PointsArray, floor, ceiling);
                if (copyP[a].compareTo(PointsArray[floor]) < 0)
                    printCol(PointsArray, floor, ceiling);
            }
            floor = ceiling;


        }
        while (floor < n);
    }
     //outF.println();
  	 StdOut.println();
  	 outF.close();
  }
}
