The main files are Runner.java, FrogSimple.java, Node.java, and Message.java. 
Executing 'javac Runner.java' will compile all of these files as well.

Runner takes two arguments: the graph filename, and the algorithm index (1 for FrogSimple and 2 for dLDF). 
For example:

'java Runner Graphs/zeroin.i.1.col 1'

will colour the graph 'zeroin.i.1.col' using FrogSimple and output to the folder 'Colourings'

Converter.java converts the graphs in the folder 'DimacsGraphSet' (obtained from the Second DIMACS challenge at http://archive.dimacs.rutgers.edu/pub/challenge/) to a useable format and outputs the solutions to the folder 'Graphs'.

The graphs that you can run are found in Graphs. The solutions of these will be found in Colourings under the same filename.