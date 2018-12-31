package nodemcu;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MCUWindow {
	
	private ArrayList<String> mcuIDs = new ArrayList<>();
	
	public MCUWindow() {
		//*/
	}

	public Stage startMonitor(String title, String id) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphDev = graphEnv.getDefaultScreenDevice();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(graphDev.getDefaultConfiguration());
		
		if (mcuIDs.contains(id)) {
			MCUHelper.ringAlert(AlertType.WARNING, title+" is already open!");
			return null;
		}
		
		MCUMonitor mcuMonitor = new MCUMonitor(id);
		HBox root = (HBox)mcuMonitor.getMonitor();
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		
		scene.getStylesheets().add(getClass().
			getResource("/css/application.css").toExternalForm());
		
		stage.setScene(scene);
		stage.setTitle(title);
		stage.setOnCloseRequest(e -> {
			mcuMonitor.stop();
			mcuIDs.remove(id);
		});
		
		stage.onCloseRequestProperty().set(e -> {
			mcuMonitor.stop();
			mcuIDs.remove(id);
		});
		
		stage.getIcons().add(new Image(getClass().
				getResourceAsStream("/images/Icon-16.png")));
		stage.getIcons().add(new Image(getClass().
				getResourceAsStream("/images/Icon-32.png")));
		
		stage.show();
		
		double width = stage.getWidth();
		double height = stage.getHeight();
		
		stage.setMaxWidth(screenSize.getWidth() / 3);
		stage.setMaxHeight(screenSize.getHeight() - insets.bottom);
		stage.setMinWidth(width);
		stage.setMinHeight(height + 20.0);
		mcuMonitor.bind(stage);
		
		mcuIDs.add(id);
		
		return stage;
	}
}
