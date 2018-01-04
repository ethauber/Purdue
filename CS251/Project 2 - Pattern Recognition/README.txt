Brute.java - algorithm that finds collinear with a brute force approach
Fast.java - algorithm that finds collinear with integer arithmetic and timsort to sort by slopes
Point.java - file that implements logic for points and what is or is not collinear

all other txt files are provided test cases

Use:
Windows: % javac -classpath ".;stdlib.jar;" Point.java Brute.java
Unix: % javac -classpath .:stdlib.jar Brute.java

Windows: % java -classpath ".;stdlib.jar;" Brute < input8.txt
Unix: % java -classpath .:stdlib.jar Brute < input8.txt

4: (0, 10000) -> (3000, 7000) -> (7000, 3000) -> (10000, 0)
4: (3000, 4000) -> (6000, 7000) -> (14000, 15000) -> (20000, 21000) 

Windows: % javac -classpath ".;stdlib.jar;" Fast.java
Unix: % javac -classpath .:stdlib.jar Fast.java

Windows: % java -classpath ".;stdlib.jar;" Fast < input6.txt
Unix: % javac -classpath .:stdlib.jar Fast < input6.txt

5: (14000, 10000) -> (18000, 10000) -> (19000, 10000) -> (21000, 10000) -> (32000, 10000)