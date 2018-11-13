package nodemcu;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class MCUNode{
	private ArrayList<XYChart.Series<Number, Number>> 
		mcuSeries = new ArrayList<>();
	private LineChart<Number, Number> mcuChart = null;
	
	private MCUWindow mcuWindow = null;
	
	private Menu mcuFileMenu = null;
	private Menu mcuViewMenu = null;
	
	private MenuBar mcuMenuBar = null;
	
	private Node mcuNode = null;
	private Node mcuWholeNode = null;
	
	private String mcuNodeId = null;
	
	public MCUNode(String id, MCUWindow mcuWindow) {
		this.mcuNodeId = id;
		this.mcuWindow = mcuWindow;
		try {
			FXMLLoader loader = new FXMLLoader();
			this.mcuWholeNode = loader.load(
					getClass().getResource("/fxml/mcumonitor.fxml").openStream());
			System.out.println("Loaded");
			this.mcuNode = this.mcuWholeNode.lookup("#mcuNode");
			this.mcuNode.setId(id);
			this.init();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCause().getMessage());
			MCUHelper.ringAlert(AlertType.ERROR, "FXML load failed!");
		}
	}
	
	private void init() {
		this.prepareChart();
		this.prepareHeader();
		this.prepareMenu();
	}
	
	@SuppressWarnings("unchecked")
	private void prepareChart() {
		String []seriesCodes = {
			"vr", "vs", "vt",
			"ir", "is", "it",
			"pr", "ps", "pt"
		};
		
		this.mcuChart = (LineChart<Number, Number>)
				this.mcuNode.lookup("#node_chart");
		
		for (String code : seriesCodes) {
			XYChart.Series<Number, Number> series =
					new XYChart.Series<>();
			series.setName(this.mcuNodeId + code);
			series.getData().add(new Data<>(0, 0));
			this.mcuSeries.add(series);
		}
		
	}
	
	private void prepareHeader() {
		Button btnClose = null;
		FileInputStream input = null;
		HBox hBox = (HBox)this.mcuNode.lookup("#node_identifier");
		Label nodeLabel = (Label)hBox.lookup("#node_name");
		try {
			input =  new FileInputStream("src/images/btn-close.jpg");
			Image image = new Image(input);
	        ImageView imageView = new ImageView(image);
	        imageView.setFitWidth(15);
	        imageView.setFitHeight(15);
			
	        btnClose = new Button("", imageView);
	        btnClose.setPadding(new Insets(0, 0, 0, 0));
	        
	        btnClose.setOnAction(e -> {
	        	mcuWindow.closeNode(this);
	        });
	        
	        hBox.getChildren().add(btnClose);
	        nodeLabel.setText(this.mcuNodeId.replace('_', ' '));
	        input.close();
		} catch (Exception e) {
			MCUHelper.ringAlert(AlertType.ERROR, e.getMessage());
		}
		
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void prepareMenu() {
		this.mcuMenuBar = (MenuBar)this.mcuNode.lookup("#node_menubar");
		this.mcuFileMenu = this.mcuMenuBar.getMenus().get(0);
		this.mcuViewMenu = this.mcuMenuBar.getMenus().get(1);
		
		
	}
	
	public String getId() {
		return this.mcuNodeId;
	}
	
	public Node getMcuNode() {
		return this.mcuNode;
	}
	
	public Node getMcuWholeNode() {
		return this.mcuWholeNode;
	}
	
	public void stop() {
		
	}
	
}
