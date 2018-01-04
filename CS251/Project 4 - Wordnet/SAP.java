import java.io.*;
import java.io.File;
import java.io.IOException;

public class SAP {
 private Digraph diGraph;
 
 // constructor
 public SAP(Digraph G) {
 	//todo
  	this.diGraph = new Digraph(G);
 }
 
 // return length of shortest ancestral path of v and w; -1 if no such path
 public int length(int v, int w) {
  //todo
  //create two new breadth first directed paths for both v and w
  BreadthFirstDirectedPaths bfV = new BreadthFirstDirectedPaths(diGraph, v);
  BreadthFirstDirectedPaths bfW = new BreadthFirstDirectedPaths(diGraph, w);

  //set the shortest length to negative one when there is no common ancestor
  int sL = -1;
  //go through all possible vertexes
  for (int i = 0; i < diGraph.V(); ++i) {
   //check for common ancestor
   if (bfV.hasPathTo(i) && bfW.hasPathTo(i)) {
    //calculate the current length to the ancestor
    int cL = bfV.distTo(i) + bfW.distTo(i);
    //set shortest length if it has not been set yet or if the current length is shorter than the shortest length
    if (sL == -1 || cL < sL) {
      sL = cL;
    }
   }
  }

  return sL;
 }
 
  // return length of shortest ancestral path of v and w; -1 if no such path
 public int length(Iterable<Integer> v, Iterable<Integer> w) {
  //todo
  //create two new breadth first directed paths for both v and w
  BreadthFirstDirectedPaths bfV = new BreadthFirstDirectedPaths(diGraph, v);
  BreadthFirstDirectedPaths bfW = new BreadthFirstDirectedPaths(diGraph, w);

  //set the shortest length to negative one when there is no common ancestor
  int sL = -1;
  //go through all possible vertexes
  for (int i = 0; i < diGraph.V(); ++i) {
   //check for common ancestor
   if (bfV.hasPathTo(i) && bfW.hasPathTo(i)) {
    //calculate the current length to the ancestor
    int cL = bfV.distTo(i) + bfW.distTo(i);
    //set shortest length if it has not been set yet or if the current length is shorter than the shortest length
    if (sL == -1 || cL < sL) {
      sL = cL;
    }
   }
  }

  return sL;
 }
 
 // return a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
 public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
 	//todo
  	BreadthFirstDirectedPaths bfV = new BreadthFirstDirectedPaths(diGraph, v);
    BreadthFirstDirectedPaths bfW = new BreadthFirstDirectedPaths(diGraph, w);

    int sL = -1;
    int ancestor = -1;
    for (int i = 0; i < diGraph.V(); ++i) {
     if (bfV.hasPathTo(i) && bfW.hasPathTo(i)) {
      int cL = bfV.distTo(i) + bfW.distTo(i);
      if (sL == -1 || cL < sL) {
       sL = cL;
       ancestor = i;
      }
     }
    }

    return ancestor;
 }
 
  // return a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
 public int ancestor(int v, int w) {
 	//todo
  	BreadthFirstDirectedPaths bfV = new BreadthFirstDirectedPaths(diGraph, v);
    BreadthFirstDirectedPaths bfW = new BreadthFirstDirectedPaths(diGraph, w);

    int sL = -1;
    int ancestor = -1;
    for (int i = 0; i < diGraph.V(); ++i) {
     if (bfV.hasPathTo(i) && bfW.hasPathTo(i)) {
      int cL = bfV.distTo(i) + bfW.distTo(i);
      if (sL == -1 || cL < sL) {
       sL = cL;
       ancestor = i;
      }
     }
    }

    return ancestor;
 }
 
 public static void main(String[] args) {
  In inText = new In(args[0]); //use In.java to create streams
  Digraph diG = new Digraph(inText); 
  //StdOut.print(diG); //prints graph
  SAP s = new SAP(diG);
  
  In inInput = new In(args[1]);
  while(!inInput.isEmpty()) {
   int v = inInput.readInt(); //read first int from .input
   //error check but continue parsing file for valid inputs
   if(v >= diG.V() || v < 0) {
    StdOut.printf("vertex %d is not between 0 and %d", v, diG.V());
    continue;
   }
   int w = inInput.readInt(); //read second int from .input
   //error check but continue parsing file for valid inputs
   if(w >= diG.V() || w < 0) {
    StdOut.printf("vertex %d is not between 0 and %d", w, diG.V());
    continue;
   }
   //StdOut.printf("v: %d w: %d\n", v, w); //prints points v and w for each line in the .input file
   
   int len = s.length(v, w);
   int anc = s.ancestor(v, w);
   StdOut.printf("sap = %d, ancestor = %d\n", len, anc);
   
  }
 }
 
} 