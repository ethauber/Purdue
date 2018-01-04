CS348 -- Project 3
Author: Elijah Hauber Login: ehauber
Email: ehauber@purdue.edu

When specifying the files at runtime make sure to include the appropriate paths.
Much of the code has comments explaining logic and commented out debug statements.

There is a directory with the name ignoreTheseFiles. I put files that were used to help me complete this project
into this directory so it would make it easy for the grader to grade by cleaning up some of the clutter.
I made some files in the sample folder to do some of my own testing and a script in the scripts folder to show the tables in my database to ensure that I had performed drop, create, and then init before submitting.

First compile with: javac -cp .:ojdbc8.jar Project3.javac
Then usage: java -cp .:ojdbc8.jar Project3 input.txt output.txt