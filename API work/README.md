# Teachworks API

## Overview
Our company uses a website called Teachworks to handle scheduling.  We needed to calculate teacher downtime between lessons, which is not possible with the Teachworks web app.  I was asked to create a script which we could run to calculate the teacher downtime and export it into a usable format for our office manager.

## Business rules
- "Downtime" is all time between the start of a teacher's first lesson and the end of their last lesson of the day, less all their paid time.
- A teacher's paid time is any time when they are actively teaching a lesson or are being paid at their teaching rate, such as for last-minute cancellations (the client still pays for a cancellation), no-show lessons, client illness/family emergency, and any other violations of our company cancellation policy.
- All teachers are paid a flat rate for downtime, regardless of their pay rate.

## Requirements
- A valid authorization token for the Teachworks API.
- Write permission for the containing folder.
- Python 3
- Packages: requests, sys, time, datetime

## Input/output
- User enters start and end dates (inclusive) for the pay period.
- Outputs a single CSV file in the following format:
| Teacher Name 	| Date                  	| Downtime (in minutes) 	|
|--------------	|-----------------------	|-----------------------	|
| First Last   	| Monday 26 Aug 2019    	| 70                    	|
| First Last   	| Tuesday 27 Aug 2019   	| 25                    	|
| First Last   	| Wednesday 28 Aug 2019 	| 10                    	|
| First Last   	| TOTAL TIME (minutes)	 	| 105                    	|
| First Last   	| TOTAL DOWNTIME PAY	 	| $22.75	                |
| 			   	| 						 	| 			                |
| First2 Last2 	| Monday 26 Aug 2019    	| 10		                |
etc.


### Known issues/limitations
- Some teachers have a non-teaching gap in their schedule (e.g. a 10am lesson, and then a 3-9pm teaching day); this program does not yet exclude these "one-off" lessons.
- Only processes up to the first 50 employees.  (Not a current issue.)
- The API sometimes rejects valid requests.

### Future developments
- Generate log file, rather than terminal verification.
- Print a single success/failure message to console.
- Create testing/verbose mode argument.
- Run program for a single teacher, specified by first name and last initial.
- Save to separate directory.