package nodemcu;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MCUWindow {
	
	private ArrayList<String> mcuIDs = new ArrayList<>();
	
	public MCUWindow() {
		
	}

	public Stage startMonitor(String id) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		String mcuID = id.replace(' ', '_');
		
		if (mcuIDs.contains(mcuID)) {
			MCUHelper.ringAlert(AlertType.WARNING, id+" is already open!");
			return null;
		}
		
		MCUMonitor mcuMonitor = new MCUMonitor(mcuID);
		HBox root = (HBox)mcuMonitor.getMonitor();
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		
		stage.setScene(scene);
		stage.setTitle(id);
		stage.setOnCloseRequest(e -> {
			mcuMonitor.stop();
		});
		stage.show();
		
		double width = stage.getWidth();
		double height = stage.getHeight();
		
		stage.setMaxWidth(screenSize.getWidth() / 2);
		stage.setMinWidth(width);
		stage.setMinHeight(height);
		
		mcuIDs.add(mcuID);
		
		return stage;
	}
}
