package nodemcu;

import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MCUWindow {
	
	private ArrayList<MCUMonitor> mcuMonitors = 
			new ArrayList<>();
	private HBox windowRoot = null;
	
	public MCUWindow() {
		
	}
	
	public void closeNode(MCUMonitor mcuNode) {
		this.windowRoot.getChildren().remove(mcuNode.getMcuNode());
		this.mcuMonitors.remove(mcuNode);
		if (this.windowRoot.getChildren().size() != 0) {
			this.windowRoot.getScene().getWindow().sizeToScene();
		}
		if (this.mcuMonitors.isEmpty()) {
			this.stopMonitoring();
		}
	}

	public void startMonitor(String id) {
		id = id.replace(' ', '_');
		for (MCUMonitor node : this.mcuMonitors) {
			if (node.getId().equals(id)) {
				MCUHelper.ringAlert(AlertType.ERROR, id + " is already open!");
				return;
			}
		}
		if (this.windowRoot == null) {
			MCUMonitor mcuNode = new MCUMonitor(id, this);
			this.windowRoot = (HBox)mcuNode.getMcuWholeNode();
			Scene scene = new Scene(this.windowRoot);
			Stage stage = new Stage();
			
			scene.getStylesheets().add(getClass().
					getResource("application.css").toExternalForm());
			
			stage.setTitle("NodeMCU Monitor");
			stage.setScene(scene);
			stage.show();
			
			stage.setOnCloseRequest(e -> {
				this.stopMonitoring();
			});
			
			this.mcuMonitors.add(mcuNode);
		} else {
			MCUMonitor mcuNode = new MCUMonitor(id, this);
			this.windowRoot.getChildren().add(mcuNode.getMcuNode());
			this.mcuMonitors.add(mcuNode);
			this.windowRoot.sceneProperty().get().
				getWindow().sizeToScene();
		}
	}
	
	public void stopMonitoring() {
		for (MCUMonitor node : this.mcuMonitors) {
			node.stop();
		}
		this.mcuMonitors.clear();
		if (this.windowRoot != null)
			((Stage)this.windowRoot.sceneProperty().get().
					getWindow()).close();
		this.windowRoot = null;
	}
}
