# Hidden Markov Model

## Discussion


## Contents
* HiddenMarkov.java - Program file
* rooms.txt - Sample input file for a robot navigating between 3 rooms
* traffic.txt - Sample input file for a self-driving robot detecting a traffic light
* weather.txt - Sample input files for a weather sensor (with infallible rain detection!)

## Input File Format Specifications
Line 1: (integer) # of states, S
Line 2: (strings) Name of each states, tab-separated
Line 3: (not used) Transition Table header. Rows = yesterday, Cols = today
Next S lines: (doubles) each row of the transition table, tab-separated
Next line: (not used) Emissions Table header. Rows = actual, Cols = sensor reading
Next S lines: (doubles) each row of the emission table, tab-separated

## Sample Output
(Bold text is user input)

Enter file name of transition & emission tables: **traffic.txt**
Enter # of time increments to calculate: **5**
Possible states are: Green Yellow Red
Enter the initial probabilities of each state (for time unit 0). If initial state is known, that state = 1.0: **0 0 1**
0 is Green
1 is Yellow
2 is Red

Enter 5 observations (i.e. sensor readings): **2 0 0 0 0**
All computed probabilities: 
Day 1: 	0.0182 	0.0000 	0.9818 	
Day 2: 	0.5081 	0.0020 	0.4899
Day 3: 	0.8812 	0.0226 	0.0961
Day 4: 	0.9555 	0.0303 	0.0142 	
Day 5: 	0.9647 	0.0318 	0.0035 	

Most likely states: 
Time 1: 98.2% chance it's Red (Sensed: Red)
Time 2: 50.8% chance it's Green (Sensed: Green)
Time 3: 88.1% chance it's Green (Sensed: Green)
Time 4: 95.6% chance it's Green (Sensed: Green)
Time 5: 96.5% chance it's Green (Sensed: Green)