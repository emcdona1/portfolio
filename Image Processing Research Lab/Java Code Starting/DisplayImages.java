import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

public class DisplayImages extends Application
{
    public void start(Stage stage)
    {
        //1. Setup
        Group root = new Group();
        Scene scene = new Scene(root, 600, 400);
        String file = "animals.jpg";
        
        //2. Display image in top left, without any changes
        ManipulatedImage plain = new ManipulatedImage(file);
        ImageView plainView = new ImageView(plain.getWritableImg());
        root.getChildren().add(plainView);
        
        //3. Display image in lower left, mirrored across the Y-axis
        ManipulatedImage mirror = new ManipulatedImage(file);
        mirror.mirrorY();
        ImageView mirrorView = new ImageView(mirror.getWritableImg());
        mirrorView.setLayoutX(0);
        mirrorView.setLayoutY(200);
        root.getChildren().add(mirrorView);
        
        //4. Display image on right side, rotated and desaturated
        ManipulatedImage gray = new ManipulatedImage(file);
        gray.rotate90();
        gray.grayscale();
        ImageView grayView = new ImageView(gray.getWritableImg());
        grayView.setLayoutX(400);
        grayView.setLayoutY(47);
        root.getChildren().add(grayView);
        
        //5. Display all images
        stage.setTitle("Animals!");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }
    
}