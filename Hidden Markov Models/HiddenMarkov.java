import java.util.Scanner;
import java.text.DecimalFormat; 
public class HiddenMarkov
{
    /*private static double[][] transition = {{0.8, 0.15, 0.05},
                                            {0.4, 0.4, 0.2},
                                            {0.2, 0.6, 0.2}}; // rows = yesterday, cols = today
   private static double[][] emission = {  {0.6, 0.4, 0},
                                            {0.3, 0.7, 0},
                                            {0.0, 0.0, 1}}; // rows = actual, cols = sensor reading*/
    private static double[][] transition = {{0.7, 0.2, 0.1},
                                            {0.3, 0.4, 0.3},
                                            {0.2, 0.3, 0.5}};
    private static double[][] emission = {  {0.7, 0.2, 0.1},
                                            {0.2, 0.6, 0.2},
                                            {0.1, 0.1, 0.8}};
    private static double[] initial;
    private static int[] observations;
    //private static String[] labels = {"Sunny", "Cloudy", "Rainy"};
    private static String[] labels = {"Room 1", "Room 2", "Room 3"};
    
    public static void main(String[] args) {
        Scanner kbd = new Scanner(System.in);
        int states = 3;
        System.out.print("Enter D days to calculate [1, D]: ");
        int days = kbd.nextInt();
        observations = new int[days];
        
        input(kbd, states, days);
                
        double[][] probabilities = calculateHMMs(days, states); //probabilities is indexed [0, days]! 0 contains initial state
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
            System.out.println("Day " + r + ": " + labels[argmax] + " (Sensed: " + labels[observations[r - 1]] + ")");
        }
    }
    
    public static void input(Scanner kbd, int states, int days) {
        System.out.print("Enter the initial probabilities of each state (if initial state is known, that state = 1.0): ");
        initial = new double[states];
        for(int i = 0; i < states; i++) {
            double input = -1;
            while(input < 0 || input > 1) {
                input = kbd.nextDouble();
            }
            initial[i] = input;
        }
        
        for(int i = 0; i < states; i++) {
            System.out.print(i + " is " + labels[i] + "\t");
        }
        System.out.println();
        System.out.print("Enter observations (sensor readings) for days 1 through " + days + ": ");
        for(int i = 0; i < days; i++) {
            int reading = -1;
            while(reading > states || reading < 0) {
                reading = kbd.nextInt();
            }
            observations[i] = reading;
        }

    }
}