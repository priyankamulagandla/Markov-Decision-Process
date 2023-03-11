## Artificial Intelligence
#Priyanka Mulagandla (pm3392)

### Markov Process Solver using value and policy iterations:

#### Help with executions

usage: MdpSolver [-v] [-df DF] [-min MIN] [-tol TOL] [-iter ITER] [-input input_file]

Markov process solver

positional arguments:
  input_file  Path to the input file

optional arguments:
  -v          Enable verbosity for program runs
  -df DF      discount factor in the range [0, 1]. Defaults to 1.0
  -min MIN    minimize values as costs, defaults to False which maximizes values as rewards
  -tol TOL    tolerance. Defaults to 0.01
  -iter ITER  Maximum number of value iteration. Defaults to 100
  -input input_file Takes the path given as input


#### Executing the programs

1) Unzip the mdpFinal.zip folder.

2) Compile the mdpFinal folder using the command line(in the folder and unzipped)
   javac -classpath "*:." *.java

3) The program can be executed with different options as follows:

# Run the solver with default options
java -classpath "*:." MdpSolver -input ../lab3Tests/lab3_input6.txt

# Run the solver with default options in verbose mode
java -classpath "*:." MdpSolver -v -input ../lab3Tests/lab3_input6.txt

# Run the solver with a tolerance of 0.001
java -classpath "*:." MdpSolver -tol 0.001 -input ../lab3Tests/lab3_input6.txt

# Run the solver with cost minimizer option
java -classpath "*:." MdpSolver -min true -input ../lab3Tests/lab3_input6.txt

# Run the solver with a discount factor of 0.9
java -classpath "*:." MdpSolver -df 0.9 -input ../lab3Tests/lab3_input6.txt


**NOTE: The `input_file` value given in the examples above are just for reference and have to be modified w.r.t proper test file paths**
