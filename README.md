# UPMSP

## Solver for the unrelated parallel machine scheduling problem with sequence dependent setup times

Written by Túlio Toffolo and Haroldo Santos.

(C) Copyright 2015 by GOAL, UFOP and CODeS Research Group, KU Leuven. All rights reserved.  
More information: http://goal.ufop.br/upmsp

Please address all contributions, suggestions, and inquiries to the current project administrator.

## Getting Started

This repository contains stochastic local search (SLS) methods to address the unrelated parallel machine scheduling problem (UPMSP) with sequence dependent setup times.

### Compiling the code

This project now uses [gradle](http://gradle.org "Gradle").
It simplifies compiling the code with its dependencies. Just run:

- gradle build

The jar file (``upmsp.jar``) will be generated.

### Usage examples:

- ``java -jar upmsp.jar instance.txt solution.txt``  
- ``java -jar upmsp.jar instance.txt solution.txt -algorithm sa -alpha 0.98 -saMax 1000 -t0 100000``  
- ``java -jar upmsp.jar instance.txt solution.txt -algorithm ils -rnaMax 10000000 -itersP 700 -p0 10 -pMax 5``  
- ``java -jar upmsp.jar instance.txt solution.txt -algorithm lahc -listSize 100``  
- ``java -jar upmsp.jar instance.txt solution.txt -algorithm schc -stepSize 100``  

### Requirements

Java 1.8 and [Apache Commons Math](https://commons.apache.org/proper/commons-math/ "Apache Commons Math") library are required.

## Questions?

If you have any questions, please feel free to contact us.
For additional information, we would like to direct you to http://goal.ufop.br/upmsp

Thanks!
