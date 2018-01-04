import java.util.Arrays;

public class Brute {
 private static Point[] PointsArray;
 private static Out outF;

 public static void main(String[] args) {
  outF = new Out("visualPoints.txt");
  
  //read size for array and initialize
  int n = StdIn.readInt();
  PointsArray = new Point[n];
  
  //iterate through rest of file creating points
  int i = 0;
  while(!StdIn.isEmpty()) {
   int x = StdIn.readInt();
   int y = StdIn.readInt();
   Point newP = new Point(x,y);
   PointsArray[i++] = newP;
  }
  
  //Arrays.sort(PointsArray, new Point());
  Arrays.sort(PointsArray);
  
  //List<Point> 
  
  //Check for Collinearity (Brute Alg)
  for(int a = 0; a < n-3; a++) {
   //PointsArray[k].Print();//Testing purposes
   for(int b = a+1; b < n-2; b++) {
    for(int c = b+1; c < n-1; c++) {
        //check to see if first three are collinear before checking all four
        if((Point.areCollinear(PointsArray[a],PointsArray[b],PointsArray[c]))) {
            for(int d = c+1; d < n; d++) {
                if(Point.areCollinear(PointsArray[b],PointsArray[c],PointsArray[d])){
                    StdOut.print("4:");
                    outF.print("4:");
                    StdOut.print(PointsArray[a].getPAS());
                    outF.print(PointsArray[a].getPAS());
                    StdOut.print(" -> ");
                    outF.print(" -> ");
                    StdOut.print(PointsArray[b].getPAS());
                    outF.print(PointsArray[b].getPAS());
                    StdOut.print(" -> ");
                    outF.print(" -> ");
                    StdOut.print(PointsArray[c].getPAS());
                    outF.print(PointsArray[c].getPAS());
                    StdOut.print(" -> ");
                    outF.print(" -> ");
                    StdOut.print(PointsArray[d].getPAS());
                    outF.print(PointsArray[d].getPAS());
                    StdOut.println();
                    outF.println();
                }
            }
        }
    }
   }
  }
  StdOut.println();
  outF.close();
  
 }
 
}