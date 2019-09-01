# Teachworks API

## Overview
Our company uses a website called Teachworks to handle scheduling.  We needed to calculate teacher downtime between lessons, which is not possible with the Teachworks web app.  I was asked to create a script which we could run to calculate the teacher downtime and export it into a usable format for our office manager.

## Business rules
- "Downtime" is any time between the start of the first lesson of the day, and the end of the last lesson of the day, less all of a teacher's paid time (teaching time, last-minute cancellations, and no-shows).
- 

## Known issues
- Some teachers have a non-teaching gap in their schedule (e.g. a 10am lesson, and then a 3-9pm teaching day); this program does not yet exclude these "one-off" lessons.
