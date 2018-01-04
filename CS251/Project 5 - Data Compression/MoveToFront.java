import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

public class MoveToFront {
    private static final int ALPHABET_SIZE = 256;
	// apply move-to-front encoding, reading from standard input and writing to standard output
	public static void encode() {
      	List<Character> mTF = createASCIIList(); //move to front
      	while (!BinaryStdIn.isEmpty()) {
         	char curChar = BinaryStdIn.readChar();
         	int alphabetPos = 0;
         	Iterator<Character> mTFIterator = mTF.iterator();
         	while (mTFIterator.hasNext()) {
            	if (mTFIterator.next().equals(Character.valueOf(curChar))) {
               		BinaryStdOut.write(alphabetPos, 8);
               		char toFront = mTF.get(alphabetPos);
               		mTF.remove(alphabetPos);
               		mTF.add(0, toFront);
               		break;
            	}
            	alphabetPos++;
         	}
      	}
		BinaryStdOut.close();
	}//end of encode
 
	// apply move-to-front decoding, reading from standard input and writing to standard output
	public static void decode() {
      	List<Character> mTF = createASCIIList();
      	while (!BinaryStdIn.isEmpty()) {
         	int curCharPos = BinaryStdIn.readChar();
         	BinaryStdOut.write(mTF.get(curCharPos));
         	char toFront = mTF.get(curCharPos);
         	mTF.remove(curCharPos);
         	mTF.add(0, toFront);
      	}
		BinaryStdOut.close();
	}
 
   private static List<Character> createASCIIList() {
      List<Character> asciiList = new LinkedList<Character>();
      for (int alphabetPosition = 0; alphabetPosition < ALPHABET_SIZE ; alphabetPosition++) { 
         asciiList.add((char) alphabetPosition);
   	  }
      return asciiList;
   }
 
	// if args[0] is '-', apply move-to-front encoding
	// if args[0] is '+', apply move-to-front decoding
	public static void main(String[] args) {
		if (args[0].equals("-")) encode();
      	else if (args[0].equals("+")) decode();
	}
}