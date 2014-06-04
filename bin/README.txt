
1) Compile the source files on cs1 or cs2 machines eg: on Linux execute the command inside quotes when inside the directory containing all the source files"javac *.java"
2) Open Config.txt, identify the number of hosts
3) Open multiple terminals or putty sessions each logged in with your userid onto the netxx.utdallas.edu machines in the Config.txt file
4) Open as many as number of nodes in Config.txt, that is one terminal for each of the machine. Alternative you can come up with a script to run them in parallel
5) Now on each machine, change to the src directory and execute "java Application y", where y is the id of the machine in Config.txt
6) Once execution is completed on any of the machines execute the command "java MutualExclusionTest"
7) This tests the program execution and returns the status of the test.