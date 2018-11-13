package fxmlcontroller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import nodemcu.MCUHelper;
import nodemcu.MCUWindow;

public class MCUSelector {
	
	private @FXML Button btnGo;
	
	private @FXML ComboBox<String> cbNodeSelect;
	
	private @FXML Label lblDate;
	private @FXML Label lblTime;
	
	public MCUSelector() {
		
	}
	
	public void init(Stage stage) {
		MCUWindow mcuWindow = new MCUWindow();
		lblDate.setTextAlignment(TextAlignment.CENTER);
		
		cbNodeSelect.getItems().addAll(FXCollections.observableArrayList(
				"NodeMCU 1", 
				"NodeMCU 2",
				"NodeMCU 3"));
		cbNodeSelect.getSelectionModel().selectFirst();
		MCUHelper.initDate(lblDate, lblTime);
		
		btnGo.setOnMouseClicked(e -> {
			mcuWindow.startMonitor(cbNodeSelect.getValue());
		});
		
		stage.setOnCloseRequest(e -> {
			mcuWindow.stopMonitoring();
		});
	}
	
}
