First Compile:
javac -classpath .:stdlib.jar Bag.java
javac -classpath .:stdlib.jar Stack.java
javac -classpath .:stdlib.jar Queue.java
javac -classpath .:stdlib.jar BredthFirstDirectedPaths.java
javac -Xlint:-unchecked -classpath .:stdlib.jar Digraph.java
javac -Xlint:-unchecked -classpath .:stdlib.jar Graph.java
javac  -classpath .:stdlib.jar DepthFirstPaths.java

To compile SAP:
javac -classpath .:stdlib.jar SAP.java
To execute SAP:
java -classpath .:stdlib.jar SAP <NAMEFORTEXTFILE>.txt <NAMEFORINPUTFILE>.input

To compile WordNet:
javac -classpath .:stdlib.jar WordNet.java
To execute WordNet:
java -classpath .:stdlib.jar WordNet <NAMEFORSYNSETSFILE>.txt <HYPERNYMSFILE>.txt <WORDPAIRSFILE>.input

Detailed information about the project is in "CS25100_Project4.pdf"

My findings when doing this project is in "Elijah Hauber -project 4 report.pdf"
