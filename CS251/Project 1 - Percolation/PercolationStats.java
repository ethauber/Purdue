public class PercolationStats {
 
 private static int N;
 private static int T;
 private static String type;
 
 private static int grandTotalOpened = 0;
 private static double[] threshResults;
 private static double[] timeResults;
 
 private static double threshValue;
 private static double threshStdDev;
 private static double totalTimeSecs;
 private static double meanTimeSecs;
 private static double stdDevTimeSecs;
 
 public static void fastMonte(){
  
  threshResults = new double[T];
  timeResults = new double[T];
  
  for(int i = T; i > 0; i--) {
   	Percolation p = new Percolation(N);
  	Stopwatch meanTime = new Stopwatch();
    int totalOpenNeeded = 0;
  	while(!p.percolates()){
  		
   		int j = StdRandom.uniform(N);
   		int k = StdRandom.uniform(N);
 		  
   		if(!p.isOpen(j,k)){
    		totalOpenNeeded++;
    		p.open(j,k);
   		}
  	}
   	timeResults[i-1] = meanTime.elapsedTime();
   	threshResults[i-1] = (double)totalOpenNeeded/(double)(N*N);
   //StdOut.println("(i-1)="+(i-1)+" time="+timeResults[i-1]+" thresh="+threshResults[i-1]);
   	grandTotalOpened += totalOpenNeeded;
  }
  
  StdStats stats = new StdStats();
  totalTimeSecs= stats.sum(timeResults);
  meanTimeSecs = stats.mean(timeResults);
  stdDevTimeSecs = stats.stddev(timeResults);
  threshValue = stats.mean(threshResults);
  threshStdDev = stats.stddev(threshResults);
 }
 
 public static void slowMonte(){
  
  threshResults = new double[T];
  timeResults = new double[T];
  
  for(int i = T; i > 0; i--) {
   	PercolationQuick p = new PercolationQuick(N);
  	Stopwatch meanTime = new Stopwatch();
    int totalOpenNeeded = 0;
  	while(!p.percolates()){
  		
   		int j = StdRandom.uniform(N);
   		int k = StdRandom.uniform(N);
 		  
   		if(!p.isOpen(j,k)){
    		totalOpenNeeded++;
    		p.open(j,k);
   		}
  	}
   	timeResults[i-1] = meanTime.elapsedTime();
   	threshResults[i-1] = (double)totalOpenNeeded/(double)(N*N);
   	grandTotalOpened += totalOpenNeeded;
  }
  
  StdStats stats = new StdStats();
  totalTimeSecs= stats.sum(timeResults);
  meanTimeSecs = stats.mean(timeResults);
  stdDevTimeSecs = stats.stddev(timeResults);
  threshValue = stats.mean(threshResults);
  threshStdDev = stats.stddev(threshResults);
 }
 
 public static void main(String[] args){
  
  if(args.length < 3){
   throw new java.lang.IllegalArgumentException();
  }
  N = Integer.parseInt(args[0]);
  T = Integer.parseInt(args[1]);
  if(T <= 0 || N <= 0){
   throw new java.lang.IllegalArgumentException();
  }
  
  type = args[2].toLowerCase();
  if(!(type.equals("fast") || type.equals("slow"))) {
   throw new java.lang.IllegalArgumentException();
  }
  
  //StdOut.println(N);
  //StdOut.println(T);
  //StdOut.println(type);
  
  if(type.equals("fast")){
   fastMonte();
  }
  else{
   slowMonte();
  }
  
  StdOut.println("**OUTPUT BELOW**");
  StdOut.println("mean threshold=" + threshValue);
  StdOut.println("std dev=" + threshStdDev);
  StdOut.println("time=" + totalTimeSecs);
  StdOut.println("mean time=" + meanTimeSecs);
  StdOut.println("stddev time=" + stdDevTimeSecs);
  
 }
 
}