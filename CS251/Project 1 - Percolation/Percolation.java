public class Percolation {
 
 //ta said this uses the weighted quick union
 
 protected int grid[][];
 private WeightedQuickUnionUF wqu;
 private int BLOCKED = 0; //black
 private int OPEN = 1; //white
 private int FULL = 2; //blue
 private int bottom;
 private int top;
 private int size;
 
 public Percolation(int n) {//Create a new n by n grid where all cells are intially blocked (black or 0)
 	//Percolation p = new Percolation;
  	size = n;
  	bottom = size * size;
  	top = size * size + 1;
  	wqu = new WeightedQuickUnionUF(size * size + 2);
  	grid = new int[size][size];
 }
 
 /*
 Open the site at coordinate (x,y), here x represents the row number and y the column number. For consistency purposes, (0,0) will be the bottom-left cell of the 
 grid and (n-1,n-1) will be on the top-right. The graphical capabilities discussed later assume a similar convention.
 */
 public void open(int x, int y) {
  if(x >= size || x < 0) {
   throw new IndexOutOfBoundsException("Index " + x + " is out of bounds!");
  }
  if(y >= size || y < 0) {
   throw new IndexOutOfBoundsException("Index " + y + " is out of bounds!");
  }
  int finderI = x*size + y;
  boolean isTop = false;
  boolean isBottom = false;
  if(grid[x][y] == BLOCKED) {
   grid[x][y] = OPEN;
   if(x == 0) {//check if it is top
   	isTop = true;
    wqu.union(finderI, top);
   }
   if(x == size-1) {//check if it is bottom
    isBottom = true;
    wqu.union(finderI, bottom);
   }
   if(!isTop && grid[x-1][y]==OPEN) {//check above
    wqu.union(finderI, ((x-1)*size + y));
   }
   if(!isBottom && grid[x+1][y]==OPEN) {//check below
    wqu.union(finderI, ((x+1)*size + y));
   }
   if(y!=0 && grid[x][y-1]==OPEN) {//check left
    wqu.union(finderI, (x*size + y - 1));
   }
   if(y!=size-1 && grid[x][y+1]==OPEN) {//check right
    wqu.union(finderI, (x*size + y + 1));
   }
  }
 }
 
 //Returns true if cell (x,y) is open due to a previous call to open(int x, int y)
 public boolean isOpen(int x, int y) {
  if(grid[x][y] == 0) {
   return false;
  }
  else {
   return true;
  }
 }
 
 //Returns true if there is a path from cell (x,y) to the surface (i.e. there is percolation up to this cell)
 public boolean isFull(int x, int y) {
  if(x >= size || x < 0) {
   throw new IndexOutOfBoundsException("Index " + x + " is out of bounds!");
  }
  if(y >= size || y < 0) {
   throw new IndexOutOfBoundsException("Index " + y + " is out of bounds!");
  }
  int finderI = x*size + y;
  return wqu.connected(bottom, finderI);
 }
 
 //Analyzes the entire grid and returns true if the whole system percolates
 public boolean percolates() {
  return wqu.connected(top,bottom);
 }
 
 /*Create a main method that reads a description of a grid from standard input and validates if the system described percolates or 
 not, printing to standard output a simple "Yes" or "No" answer.
 */
 public static void main(String[] args) {
  // constructors are noramlly found in main method
  // takes in values then makes the object
  int n = StdIn.readInt();
  Percolation perc = new Percolation(n);
  while (!StdIn.isEmpty()) {
            int i = StdIn.readInt();
            int j = StdIn.readInt();
            //if (uf.connected(p, q)) continue;
            //uf.union(p, q);
            //StdOut.println(i + " " + j);
   			perc.open(i, j);
  }
  if(perc.percolates()){
   StdOut.println("Yes");
  }
  else{
   StdOut.println("No");
  }
 }
}