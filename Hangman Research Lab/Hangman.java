import javafx.application.*;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

public class Hangman extends Application
{
    private Dictionary dict = new Dictionary();
    private String secretWord;
    
    private Text[] letterGuesses;
    private Text[] lettersRevealedSoFar;
    private Shape[] hangman;
    
    private int currentHangmanPart;
    private boolean activeGame;
   
    @Override
    public void start(Stage primaryStage)
    {        
        primaryStage.setTitle("Hangman");
        Font gameFont = Font.font("Consolas", 22.0);
        
        Text statusBox = new Text(200, 100, "");
        statusBox.setFont(gameFont);
        Pane main = this.setUpLayout(statusBox);
        Scene scene = new Scene(main, 400, 500);
        
        Button resetButton = new Button("New Game");
        resetButton.setDefaultButton(true);
        resetButton.relocate(25,350);
        main.getChildren().add(resetButton);        
        
        resetButton.setOnAction( e -> {
            Pane newPane = setUpLayout(statusBox);
            newPane.getChildren().add(resetButton);
            scene.setRoot(newPane);
        });
        
        scene.setOnKeyPressed( e -> {
            if(activeGame)
            {
                String guess = e.getText().toLowerCase();
                checkGuess(guess, statusBox);
            }
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Pane setUpLayout(Text statusBox)
    {        
        Pane main = new Pane();
        Font gameFont = Font.font("Consolas", 22.0);
        
        //set new word to guess
        this.secretWord = dict.getRandomWord();
		
        GridPane word = new GridPane();
        word.setHgap(5);
        
        this.currentHangmanPart = 0;
        
        GridPane lettersPane = new GridPane();
        lettersPane.relocate(25, 430);
        lettersPane.setHgap(5);
        lettersPane.setVgap(5);
        
        //create letterGuesses
        this.letterGuesses = new Text[26];
        for(int i = 0; i < 26; i++)
        {
            String temp = Dictionary.ALPHABET.charAt(i) + "";
            this.letterGuesses[i] = new Text(temp);
            letterGuesses[i].setFont(gameFont);
            lettersPane.add(letterGuesses[i], i % 13,  i / 13);
        }
        
        //set up lettersRevealedSoFar
        this.lettersRevealedSoFar = new Text[this.secretWord.length()];
        for(int i = 0; i < lettersRevealedSoFar.length; i++)
        {
            this.lettersRevealedSoFar[i] = new Text("_");
            this.lettersRevealedSoFar[i].setFont(gameFont);
            word.add(this.lettersRevealedSoFar[i], i, 0);
        }
        
        //set up the gallows
        Pane figure = new Pane();
        Shape[] gallows = { new Line(120, 40, 120, 10),
                           new Line(120, 10, 40, 10),
                           new Line(40, 10, 40, 250),
                           new Rectangle(150, 7) };
        gallows[3].relocate(20, 250);
        for(int i = 0; i < gallows.length; i++)
        {
            gallows[i].setStroke(Color.BLACK);
            gallows[i].setStrokeWidth(2.0);
            figure.getChildren().add(gallows[i]);
        }
        
        //set up the hangman outline
        this.hangman = new Shape[6];
        this.hangman[0] = new Circle(120, 60, 20, Color.WHITE); // head
        this.hangman[1] = new Line(120, 80, 120, 150); // body
        this.hangman[2] = new Line(120, 110, 80, 90); // left arm
        this.hangman[3] = new Line(120, 110, 160, 90); // right arm
        this.hangman[4] = new Line(120, 150, 100, 200); // left leg
        this.hangman[5] = new Line(120, 150, 140, 200); // right leg
        
        for(int i = 0; i < this.hangman.length; i++)
        {
            this.hangman[i].setStroke(Color.WHITE);
            this.hangman[i].setStrokeWidth(3.0);
            figure.getChildren().add(this.hangman[i]);
        }
        
        //set up the alphabet
        Text title = new Text(25, 420, "Guesses so far:");
        
        //ready to play
        this.activeGame = true;
        statusBox.setText("Ready to play");
        statusBox.setFill(Color.BLUE);
        
        //place everything in window
        word.relocate(20, 300);
        main.getChildren().add(word);
        main.getChildren().add(figure);
        main.getChildren().add(title);
        main.getChildren().add(lettersPane);
        main.getChildren().add(statusBox);
        
        return main;
    }
   
    private void checkGuess(String s, Text statusBox)
    {
        int index = this.findLetter(s);
        
        if(!alreadyGuessed(index))
        {
            boolean found = foundInSecretWord(s);
            
            if(found) // correct guess
            {
                this.letterGuesses[index].setFill(Color.GREEN);
                checkIfWon(s, statusBox);
            }
            else // incorrect guess
            {
                this.letterGuesses[index].setFill(Color.RED);
                checkIfLost(s, statusBox);
            }
        }
    }
    private int findLetter(String s)
    {
        int result = -1;
        for(int i = 0; i < 26; i++)
        {
            if(s.equals(this.letterGuesses[i].getText()))
            {
                result = i;
            }
        }
        return result;
    }
    private boolean alreadyGuessed(int index)
    {
        if(index >= 0)
        {
            Color currColor = (Color)this.letterGuesses[index].getFill();
            if(currColor.equals(Color.BLACK))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        return true;
    }
    private boolean foundInSecretWord(String s)
    {
        boolean found = false;
        for(int i = 0; i < this.secretWord.length(); i++)
        {
            if((this.secretWord.charAt(i)+"").equals(s))
            {
                found = true;
                this.lettersRevealedSoFar[i].setText(s);
            }
        }
        return found;
    }
    private void checkIfWon(String s, Text statusBox)
    {
        boolean won = true;
        for(int i = 0; i < this.lettersRevealedSoFar.length; i++)
        {
            if(lettersRevealedSoFar[i].getText().equals("_"))
            {
                won = false;
            }
        }
        if(won)
        {
            statusBox.setText("YOU WIN!!!");
            statusBox.setFill(Color.GREEN);
            this.activeGame = false;
        }
        else
        {
            statusBox.setText("Letter " + s + " is \ncorrect!");
            statusBox.setFill(Color.FORESTGREEN);
        }
    }
    private void checkIfLost(String s, Text statusBox)
    {
        if(this.currentHangmanPart < 6)
        {
            this.hangman[this.currentHangmanPart].setStroke(Color.BLACK);
            this.currentHangmanPart++;
            
            statusBox.setText("Letter " + s + " is \nnot correct.");
            statusBox.setFill(Color.FIREBRICK);
        }
        else
        {
            for(int i = 0; i < this.lettersRevealedSoFar.length; i++)
            {
                this.lettersRevealedSoFar[i].setText(this.secretWord.charAt(i)+"");
                this.lettersRevealedSoFar[i].setFill(Color.FIREBRICK);
            }
            statusBox.setText("You lost. :(");
            statusBox.setFill(Color.RED);
            this.activeGame = false;
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}