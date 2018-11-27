import java.util.Scanner;
import java.text.DecimalFormat;
import java.io.*;

/*INPUT FILE FORMAT
Line 1: (integer) # of states, S
Line 2: (strings) Name of each states, tab-separated
Line 3: (not used) Transition Table header. Rows = yesterday, Cols = today
Next S lines: (doubles) each row of the transition table, tab-separated
Next line: (not used) Emissions Table header. Rows = actual, Cols = sensor reading
Next S lines: (doubles) each row of the emission table, tab-separated
*/

public class HiddenMarkov
{
    private static int states;
    private static String[] labels;
    private static double[][] transition; // rows = yesterday, cols = today
    private static double[][] emission; // rows = actual, cols = sensor reading
    
    private static double[] initial;
    private static int[] observations;
    
    public static void main(String[] args) {
        Scanner kbd = new Scanner(System.in);
        
        boolean fileReadIn = false;
        while (!fileReadIn) {
            System.out.print("Enter file name of transition & emission tables: ");
            String filename = kbd.nextLine();
            try {
                fileInput(filename);
                fileReadIn = true;
            } catch(FileNotFoundException ex) {
                System.out.println(filename + " is not a valid file.");
            } catch(NumberFormatException ex) {
                System.out.println(filename + " is not formatted correctly.");
            }
        }
        
        System.out.print("Enter # of time increments to calculate: ");
        int increments = kbd.nextInt();
        observations = new int[increments];
        
        input(kbd, increments);
                
        double[][] probabilities = calculateHMMs(increments, states); //probabilities is indexed [0, days]! 0 contains initial state
        printHMMs(probabilities);
    }
    
    public static double[][] calculateHMMs(int days, int states) {
        double[][] probs = new double[days + 1][states];
        for(int n = 0; n < states; n++) {
            probs[0][n] = initial[n];
        }
        
        for(int i = 1; i <= days; i++) {
            double eta = 0;
            int currObs = observations[i - 1]; // current sensor reading
            for(int j = 0; j < states; j++) {
                // calculate for each state, not normalized
                probs[i][j] = emission[j][currObs];
                double sum = 0.0;
                for(int k = 0; k < states; k++) {
                    sum += transition[k][j] * probs[i - 1][k];
                }
                probs[i][j] *= sum;
                eta += probs[i][j];
            }
            //normalize
            eta = 1 / eta;
            for(int n = 0; n < states; n++) {
                probs[i][n] *= eta;
            }
        }
        
        return probs;
    }
    public static void printHMMs(double[][] probs) {
        DecimalFormat dec = new DecimalFormat("#0.0000");
        DecimalFormat per = new DecimalFormat("#00.0");
        System.out.println("All computed probabilities: ");
        for(int r = 1; r < probs.length; r++) {
            System.out.print("Day " + r + ": \t");
            for(int c = 0; c < probs[r].length; c++) {
                System.out.print(dec.format(probs[r][c]) + " \t");
            }
            System.out.println();
        }
        System.out.println("\nMost likely states: ");
        for(int r = 1; r < probs.length; r++) {
            int argmax = 0;
            double max = 0.0;
            for(int c = 0; c < probs[r].length; c++) {
                if(probs[r][c] > max) {
                    argmax = c;
                    max = probs[r][c];
                }
            }
            System.out.println("Time " + r + ": " + per.format(probs[r][argmax] * 100) + "% chance it's " + labels[argmax] + " (Sensed: " + labels[observations[r - 1]] + ")");
        }
    }



    public static void fileInput(String filename) throws FileNotFoundException, NumberFormatException {
        File f = new File(filename);
        Scanner in = new Scanner(f);
        
        states = Integer.parseInt(in.nextLine());
        labels = in.nextLine().split("\t+");
        in.nextLine(); //throw away header
        transition = new double[states][states];
        for(int i = 0; i < states; i++) {
            for(int j = 0; j < states; j++) {
                transition[i][j] = in.nextDouble();
            }
        }
        in.nextLine();
        
        in.nextLine(); //throw away header
        emission = new double[states][states];
        for(int i = 0; i < states; i++) {
            for(int j = 0; j < states; j++) {
                emission[i][j] = in.nextDouble();
            }
        }
        in.close();
    }
    public static void input(Scanner kbd, int increments) {
        System.out.print("Possible states are: ");
        for(int i = 0; i < states; i++) {
            System.out.print(labels[i] + " ");
        }
        System.out.print("\nEnter the initial probabilities of each state (for time unit 0). If initial state is known, that state = 1.0: ");
        initial = new double[states];
        for(int i = 0; i < states; i++) {
            double input = -1;
            while(input < 0 || input > 1) {
                input = kbd.nextDouble();
            }
            initial[i] = input;
        }
        
        for(int i = 0; i < states; i++) {
            System.out.print(i + " is " + labels[i] + "\n");
        }
        
        System.out.println();
        
        System.out.print("Enter " + increments + " observations (i.e. sensor readings): ");
        for(int i = 0; i < increments; i++) {
            int reading = -1;
            while(reading > states || reading < 0) {
                reading = kbd.nextInt();
            }
            observations[i] = reading;
        }

    }
}