public class PercolationVisualizer {
 private static Out outF;
 public static void main(String[] args) {
  	outF = new Out("visualMatrix.txt");
  	int n = StdIn.readInt();
  	StdOut.println(n + "\n");
  	outF.println(n + "\n");
  	Percolation perc = new Percolation(n);
  	while (!StdIn.isEmpty()) {
  		int x = StdIn.readInt();
    	int y = StdIn.readInt();
    	perc.open(x, y);
   		//StdOut.println(i+ "" +j);
    	for(int i = n - 1; i >= 0; i--){
   			for(int j = 0; j < n; j++){
             if(perc.isFull(i,j)){
              StdOut.print(perc.grid[i][j] + 1 + " ");
              outF.print(perc.grid[i][j] + 1 + " ");
             }
             else{
              StdOut.print(perc.grid[i][j] + " ");
              outF.print(perc.grid[i][j] + " ");
             }
   			}
         	StdOut.println();
   			outF.println();
  		}
     	StdOut.println();
  		outF.println();
 	}
 	outF.close();
 }
}