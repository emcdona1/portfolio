import javafx.application.Application;
import javafx.geometry.*; 
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;  
import javafx.stage.Stage;
import javafx.event.*; 
import javafx.scene.text.*;


public class SweepstakesJavaFX extends Application 
{
	private TextField first = new TextField();
	private TextField last = new TextField();
	private TextField phone = new TextField();
	private TextField email = new TextField();
	private TextField luckyNum = new TextField();
	private TextField dob = new TextField();
	public Label error = new Label();
	public Label title = new Label("Sweepstakes Entry Form\nPlease complete the fields below");
	
	public static void main(String[] args)
   	{
      	Application.launch(args);
   	}
   
	@Override
	public void start(Stage primaryStage) 
	{
		VBox labels = new VBox(); 
    	GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
    	labels.setAlignment(Pos.CENTER);
    	pane.setPadding(new Insets(10, 5, 5, 5)); 
    	labels.setPadding(new Insets(5, 5, 5, 5)); 
    	pane.setHgap(45.5);
    	pane.setVgap(20.5); 
      
      	title.setStyle("-fx-font-weight: bold; -fx-font-size:20;"); 
      	error.setStyle("-fx-text-fill: red;"); 
      
      	labels.getChildren().add(title);
      	labels.getChildren().add(error);  
      
      	pane.add(new Label("First Name: "), 0, 0);
      	pane.add(first, 1, 0); 
      	pane.add(new Label("Last Name: "), 0, 1); 
      	pane.add(last, 1, 1); 
      	pane.add(new Label("Phone Number: "), 0, 2); 
      	pane.add(phone, 1, 2);  
      	pane.add(new Label("Email Address: "), 0, 3); 
      	pane.add(email, 1, 3); 
      	pane.add(new Label("Lucky Number: "), 0, 4); 
      	pane.add(luckyNum, 1, 4); 
      	pane.add(new Label("Date of Birth: "), 0, 5); 
      	pane.add(dob, 1, 5); 
      
      	Button btnSubmit = new Button("Submit"); 
      	pane.add(btnSubmit,1,6); 
      	GridPane.setHalignment(btnSubmit, HPos.RIGHT); 
            
      	Scene scene = new Scene(new VBox(labels,pane), 450, 525);  
      	primaryStage.setTitle("Sign up form"); // Set the stage title
      	primaryStage.setScene(scene); // Place the scene in the stage
      	primaryStage.show(); // Display the stage
      
      	btnSubmit.setOnAction(new EventHandler<ActionEvent>()
      	{
        	@Override
         	public void handle(ActionEvent e)
         	{
            	String fName = first.getText(); 
            	String lName = last.getText(); 
            	String inPhone = phone.getText(); 
            	String inEmail = email.getText(); 
            	String inLuckyNum = luckyNum.getText(); 
            	String inDob = dob.getText(); 
            
            	error.setText("");
            	
            	// Call the validation methods here.             	
            
            	if (error.getText() == "")
            	{
               		primaryStage.hide();
               		resultsPage();
            	}
         	}    
		}); 
	}
	
	public void checkFirst(String first)
   	{
      	if (first.length() == 0)
         	error.setText(error.getText() + "\nFirst name required "); 
      	else if (!first.matches("[A-Za-z]{2,}"))
      		error.setText(error.getText() + "\nFirst name invalid");
	}
	
	public void checkLast(String last)
   	{
      	if (last.length() == 0)
         	error.setText(error.getText() + "\nLast name required "); 
      	else if (!last.matches("[A-Za-z]{2,}"))
      		error.setText(error.getText() + "\nLast name invalid");
	}
	
	public void checkPhone(String phone)
	{
		if (phone.length() == 0)
         	error.setText(error.getText() + "\nPhone required "); 
      	else if (!phone.matches("[0-9]{3}-[0-9]{3}-[0-9]{4}"))
      		error.setText(error.getText() + "\nPhone number format: xxx-xxx-xxxx");
	}
	
	public void checkEmail(String email)
   	{
      	if (email.length() == 0)
         	error.setText(error.getText() + "\nEmail required "); 
      	else if (!email.matches("[A-Za-z0-9.-_]+@[A-Za-z0-9.-_]+.[A-Za-z0-9]+"))
      		error.setText(error.getText() + "\nEmail address invalid. Only letters, numbers, and . - _ are allowed.");
	}
	
	public void checkLuckyNum(String luckyNum)
   	{
      	if (luckyNum.length() == 0)
         	error.setText(error.getText() + "\nLucky number required "); 
      	else if (!luckyNum.matches("[01]{0,1}[0-9]{1,2}"))
      		error.setText(error.getText() + "\nLucky number must be between 1 and 100.");
	}
	
	public void checkDob(String dob)
   	{
      	if (dob.length() == 0)
         	error.setText(error.getText() + "\nDate of Birth required "); 
      	else if (!dob.matches("[0-9]{1,2}/[0-9]{1,2}/(19|20)[0-9]{2}"))
      		error.setText(error.getText() + "\nDate of Birth format: xx/xx/xxxx or x/x/xxxx");
	}
   
  
	public void resultsPage() 
   	{
      	Stage resultsStage = new Stage(); 
      	VBox results = new VBox(); 
      	results.setAlignment(Pos.CENTER);
      	results.setPadding(new Insets(10, 5, 5, 5));
      
      	Label congrats = new Label("Congrats!");
      	Label display = new Label("The fields have been validated!");
      	congrats.setStyle("-fx-font-weight: bold; -fx-font-size:20; -fx-text-fill:blue;");
      	display.setStyle("-fx-font-weight: bold; -fx-font-size:20; -fx-text-fill:blue;");
      	results.getChildren().add(congrats); 
      	results.getChildren().add(display);
      
      	Scene scene = new Scene(results, 400, 400);  
      	resultsStage.setTitle("Results Page"); 
      	resultsStage.setScene(scene);
      	resultsStage.show(); 
   	}
}