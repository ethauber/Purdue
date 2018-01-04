import java.util.Arrays;
import java.util.Comparator;

import java.util.HashMap;
import java.util.Map;

public class BurrowsWheeler {
 //used for encoding
 public static class CircularSuffixArray {
   private String input;
   private Integer[] index;
   
   //creates circular suffix array with input String s
   public CircularSuffixArray(String s) {
    //error check
    if(s == null || s.equals("")) { 
    	throw new java.lang.IllegalArgumentException("Can't get suffix array for empty string!");
    } 
    input = s;
    index = new Integer[length()];
     
    for (int i = 0; i < index.length; i++) index[i] = i;
      
    //compare strings using number of shifts
    Arrays.sort(index, new Comparator<Integer>() {
      @Override
      public int compare(Integer first, Integer second) {
      //get start indexes of chars for comparison
        int firstIndex = first;
        int secondIndex = second;
        //for all characters
        for(int i = 0; i < input.length(); i++) {
          //if out of the last char then start from beginning
          if(firstIndex > input.length() - 1) firstIndex = 0;
          if(secondIndex > input.length() - 1) secondIndex = 0;
          //if first string > second
          if(input.charAt(firstIndex) < input.charAt(secondIndex)) return -1;
          else if(input.charAt(firstIndex) > input.charAt(secondIndex)) return 1;
          //watch next chars
          firstIndex++;
          secondIndex++;
        }
        //equal strings
        return 0;
      }
    });
   }
   //Length of circular array string
   public int length() { return input.length(); }
   
   //Returns row (index) in the original suffix of ith sorted suffix
   //i is the number of sorted index
   public int index(int i) { return index[i]; }
 
 } //end of circular suffix array
 
 private static final int R = 256; //radix of byte
 
    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode() {
		
      //read string from binary std in
      String input = BinaryStdIn.readString();
      //create circ suff arr for in string
      CircularSuffixArray circularSuffixArray = new CircularSuffixArray(input);
      //look for first row in original suffix with 0 offset(index[i] = 0)
      for(int i = 0; i < circularSuffixArray.length(); i++) {
         if(circularSuffixArray.index(i) == 0) {
            //output number of first row in sorted suffixes
            BinaryStdOut.write(i);
            break;
         }
      }
      //make output as last chars of sorted suffixes
      for (int i = 0; i < circularSuffixArray.length(); i++) {
         int index = circularSuffixArray.index(i);
         if (index == 0) {
            BinaryStdOut.write(input.charAt(input.length() - 1));
            continue;
         }
         BinaryStdOut.write(input.charAt(index - 1));
      }
		BinaryStdOut.close();
    }//end of encoding
 
    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode() {
        int first = BinaryStdIn.readInt();
     	//StdOut.printf("first=%d\n",first);
     	//StdOut.printf("first=%c\n",first);
     	
     //read the string from binary std in
        String s = BinaryStdIn.readString();
     	//store string as a char array
        char[] input = s.toCharArray();
     	
     //make a sorted array from the input
        char[] sorted = new char[input.length];
        for (int i = 0; i < input.length; i++) sorted[i] = input[i];
        Arrays.sort(sorted);
        
     //create an array for the ascii values and the array to hold the decoded characters
     	int []baseIndex = new int[R];
        int []next = new int[input.length];

        //make next array
        for(int i = 0; i < input.length; i++) {
            next[i] = getNextForChar(sorted[i], input, baseIndex);
        }          

        //show the string.
        int i, point;
        for(i = 0, point = first; i < next.length; i++, point = next[point]) {
            BinaryStdOut.write(sorted[point]); 
        }
            
		BinaryStdOut.close();
    }//end of decoding
 
    private static int getNextForChar(char c, char[] input, int []baseIndex) {
        for(int i = baseIndex[c]; i < input.length; i++) {
            if(input[i] == c) {
                baseIndex[c] = i+1;
                return i;
            }
        }
        //error check
        assert false;
        return 1;
	}
 
    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
    	if(args[0].equals("-")) encode();
 		else if(args[0].equals("+")) decode();
    }
}

//neat but useless
     /*sort original suffixes
     java.util.Arrays.sort(suffixMatrix, new java.util.Comparator<char[]>() {
    	public int compare(char[] a, char[] b) {
         	String aS = new String(a);
         	String bS = new String(b);
        	return aS.compareTo(bS);
    	}
	});*/