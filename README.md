# UPMSP

## Traveling Umpire Problem Solver

Written by Túlio Toffolo and Haroldo Santos.

(C) Copyright 2015, by GOAL, UFOP and CODeS Research Group, KU Leuven. All rights reserved.
More information: http://goal.ufop.br/upmsp

Please address all contributions, suggestions, and inquiries to the current project administrator.

## Getting Started

The package includes the solver source code for the unrelated parallel machine scheduling problem with sequence dependent setup times.

The class with the main procedure is at upmsp.Main.
Alternatively, you can download the latest binary (jar) file in the *bin* folder.

Usage examples:

- java -jar upmsp.jar instance.txt solution.txt
- java -jar upmsp.jar instance.txt solution.txt -algorithm sa -alpha 0.98 -samax 1000 -t0 100000
- java -jar upmsp.jar instance.txt solution.txt -algorithm ils -rnamax 10000000 -itersp 700 -p0 10 -pmax 5
- java -jar upmsp.jar instance.txt solution.txt -algorithm lahc -listsize 100
- java -jar upmsp.jar instance.txt solution.txt -algorithm schc -stepsize 100

## Requirements

Java 1.8 is required.

## Questions?

If you have any questions, please feel free to contact us.
For additional information, we would like to direct you to http://goal.ufop.br/upmsp

Thanks!
