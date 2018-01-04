Elijah Hauber

compilation:
	javac -classpath .:stdlib.jar BurrowsWheeler.java
	javac -classpath .:stdlib.jar MoveToFront.java
	javac -classpath .:stdlib.jar:algs4.jar Huffman.java

execution:
	BurrowWheeler:
		decompress(decode)
		 java -classpath .:stdlib.jar BurrowsWheeler + < <file to decode>
		compress(encode)
		 java -classpath .:stdlib.jar BurrowsWheeler - < <file to encode>

	MoveToFront:
		decompress(decode)
		 java -classpath .:stdlib.jar BurrowsWheeler + < <file to decode>
		compress(encode)
		 java -classpath .:stdlib.jar BurrowsWheeler - < <file to encode>
	
	Huffman:
		decompress(decode)
		 java -classpath .:stdlib.jar BurrowsWheeler + < <file to decode>
		compress(encode)
		 java -classpath .:stdlib.jar BurrowsWheeler - < <file to encode>