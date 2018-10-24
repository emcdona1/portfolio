# Hangman Research Lab

## Learning Objectives
- Increase comfort with referencing the Java documentation
- Handle arrays which contain various objects, including arrays which contain objects of different subclasses
- Practice using String class methods
- Practice with methods and inheritance/polymorphism
- Give initial exposure to concepts in future classes, including: Event-driven programming, JavaFX, and Lambda expressions
- Provide guided practice for how to break down a larger software problem into multiple steps
- Provide guided practice for how to test software and identify edge cases

## Student Activities
- Examine the Dictionary class -- including its variables/constants, constructors, and methods -- to understand how it operates.
- Examine the provided code for the Hangman class. Identify variables/constants, methods, and how an application is laid out.
- Outline the methods of the Hangman class.
- Use their understanding of Hangman to construct a set of instructions (i.e. game logic / algorithm), which can be translated to psuedocode and then to Java.
- Use String methods to validate a player's guess (did they enter a letter? Did they enter a letter they already tried?) and determine if the guess is correct.
- Test the software and try to identify edge cases.

## Contents
Java Code Final/
Solution code, which was provided to student peer leaders for reference (in case of unclear/ambiguous written instructions, to show one potential "correct" solution).

- Dictionary.java - Provided class (written by a previous student) to read in the words.txt file, and used to randomly select a word for the game. Students learn about file I/O later in the semester, so this action was encapsulated.
- Hangman.java - JavaFX application which executes a Hangman game, and allows for multiple rounds of the game per execution. Students are given a partially completed version of this class, and are asked to implement various steps of the game logic.
- words.txt - Provided list of "dictionary words" for use in the game. Read in by the Dictionary class