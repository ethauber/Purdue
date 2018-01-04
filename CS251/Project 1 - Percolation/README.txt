Elijah Hauber

To compile:
javac -classpath .:stdlib.jar WeightedQuickUnionUF.java
javac -classpath .:stdlib.jar QuickUnionUF.java
javac -classpath .:stdlib.jar Percolation.java
javac -classpath .:stdlib.jar PercolationQuick.java
javac -classpath .:stdlib.jar PercolationVisualizer.java
javac -classpath .:stdlib.jar PercolationStats.java
javac -classpath .:stdlib.jar VisualizeFrames.java

To run:
java -classpath .:stdlib.jar Percolation < testCase.txt
java -classpath .:stdlib.jar PercolationVisualizer < testCase.txt
java -classpath .:stdlib.jar PercolationStats 20 50 fast
java -classpath .:stdlib.jar VisualizeFrames

