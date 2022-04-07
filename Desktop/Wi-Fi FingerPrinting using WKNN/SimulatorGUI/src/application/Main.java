package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("map.fxml"));
		
		Scene scene = new Scene(root, 720, 480);
		
		primaryStage.initStyle(StageStyle.UNDECORATED);
		//primaryStage.setTitle("Wi-Fi Fingerprinting using WKNN");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.getIcons().add(new Image("/wifi.png"));
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
