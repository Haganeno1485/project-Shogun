package nodemcu;
	
import fxmlcontroller.MCUSelector;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;


public class Central extends Application {
	
	@FXML Label lblTime;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Image appIcon = new Image(getClass().
					getResourceAsStream("/images/Icon-16.png"));
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
					getResource("/css/application.css").toExternalForm());
			primaryStage.getIcons().add(appIcon);
			primaryStage.getIcons().add(new Image(getClass().
					getResourceAsStream("/images/Icon-32.png")));
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("NodeMCU Monitor");
			
			primaryStage.show();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}//*/
	}
	
	public static void main(String[] args) {
		try {
			Application.launch(args);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
