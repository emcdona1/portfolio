# Teachworks API

## Overview
Our company uses a website called Teachworks to handle scheduling.  We needed to calculate teacher downtime between lessons.  Since this functionality is not included in Teachworks, I created a script which would interface with Teachworks API, calculate the teacher downtime for each teacher, and export it into a CSV format.

## Definitions / Business Rules
- "Downtime" is any time between the start of a teacher's first lesson and the end of their last lesson of the day, LESS that teacher's paid time.
- "Paid time" is any time when they are actively teaching a lesson or are being paid at their teaching rate, such as for last-minute cancellations (the client still pays for a cancellation), no-show lessons, client illness/family emergency, and any other violations of our company cancellation policy.
- All teachers are paid a flat rate for downtime.

## Requirements
- A valid authorization token for the Teachworks API.
- Write permission for the containing folder.
- Python 3 with the following packages: requests, time, datetime

## Input/output
- User is prompted to enter the start and end dates (inclusive) for the pay period.  Dates must be entered in the format "yyyy-mm-dd".
- The program outputs a single CSV file in the following format:

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
- Some teachers have a non-teaching gap in their schedule (e.g. a 4pm private lesson during a 3-9pm teaching day); this program does not yet exclude these "one-off" lessons.
- Only processes up to the first 50 employees.
- The API sometimes rejects valid requests.  If any API query fails for any reason (i.e. an HTTP status other than 200), the user sees a message that they should rerun the program.

### Future developments
- Exclude private lessons/outside obligations.
- Option to specify file name or directory.
- Input validation.
- Automatically restart process if a query fails.
- Additional output details, to aid the Office Manager with troubleshooting / processing payroll more quickly.