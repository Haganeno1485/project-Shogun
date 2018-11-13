package nodemcu;
	
import fxmlcontroller.MCUSelector;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;


public class Central extends Application {
	
	@FXML Label lblTime;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			Parent root = null;
			MCUSelector controller = null;
			try {
				root = loader.load(getClass().
						getResource("/fxml/mcuselector.fxml").openStream());
				controller = (MCUSelector)loader.getController();
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			controller.init(primaryStage);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().
					getResource("application.css").toExternalForm());
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("NodeMCU Monitor");
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}//*/
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
