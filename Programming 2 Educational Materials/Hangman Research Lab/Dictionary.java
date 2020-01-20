import java.util.*;
import java.io.*;

public class Dictionary
{
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private ArrayList<String> wordList;
    
    public Dictionary()
    {
        loadWords();
    }
    
    public String getRandomWord()
    {
        int idx = (int)(Math.random() * this.wordList.size());
        return wordList.get(idx);
    }
    
    private void loadWords()
    {   
        this.wordList = new ArrayList<String>();
        File f = new File("words.txt");
        try
        {
            Scanner in = new Scanner(f);
            while(in.hasNext())
            {
                String word = in.next();
                wordList.add(word);
            }
            in.close();
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("File not found.");
        }
    }
}