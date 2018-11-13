package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			NodeWindow nodeWindow = NodeWindow.getInstance();
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/window-main.fxml"));
			@SuppressWarnings("unchecked")
			ComboBox<String> cbNodeSelect = (ComboBox<String>) root.lookup("#cb_node_select");
			Button btnMainGo = (Button) root.lookup("#main_button_go");
			Label lblTime = (Label)root.lookup("#lbl_time");
			Label lblDate = (Label)root.lookup("#lbl_date");
			lblDate.setTextAlignment(TextAlignment.CENTER);
			btnMainGo.setOnMouseClicked(e -> {
				nodeWindow.show(cbNodeSelect.getValue(), primaryStage);//*/
			});
			cbNodeSelect.getItems().addAll(FXCollections.observableArrayList(
					"NodeMCU 1", 
					"NodeMCU 2",
					"NodeMCU 3"));
			cbNodeSelect.getSelectionModel().selectFirst();
			WindowUtil.initDate(lblDate, lblTime);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("NodeMCU Monitor");
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(e -> {
				nodeWindow.destroy();
			});
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}//*/
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
