package application;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NodeWindow {
	
	static NodeWindow nodeWindow = null;
	
	private int runTime = 0;
	private HashMap<String, LineChartManager<Number, Number>> chartManagers =
			new HashMap<>();
	private ArrayList<String> runningNode = new ArrayList<>();
	// private ArrayList<Number> runTime = new ArrayList<>();
	private HashMap<String, Number> nodeChartYAxis = 
			new HashMap<>();
	private HBox nodeContainer = null;
	private Stage nodeStage = null;
	
	/**
	 * Private constructor
	 */
	private NodeWindow() {
		
	}
	
	/**
	 * Get instance of this class
	 * @return NodeWindow
	 */
	public static NodeWindow getInstance() {
		if (nodeWindow == null) {
			nodeWindow = new NodeWindow();
			return nodeWindow;
		} else {
			return nodeWindow;
		}
	}
	
	/**
	 * Open new window or append new content if 
	 * the window is already shown.
	 * @param name id and key for all List or Map type resources.
	 * @param owner the owner of this stage.
	 * @return NodeWindow stage.
	 */
	public Stage show(String name, Stage owner) {
		String nodeName = name.replace(' ', '_');
		if (runningNode.contains(nodeName)) {
			this.ringAlert(AlertType.WARNING, (name + " is already opened!"));
			return this.nodeStage;
		}
		try {
			Parent nodeRoot = this.getNodeContent(nodeName);
			if (nodeStage == null ) {
				Stage nodeStage = new Stage();
				
				if (nodeRoot == null) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.getDialogPane().getChildren().remove(1);
					alert.getDialogPane().getChildren().remove(1);
					alert.setHeaderText("Something has gone wrong!");
					alert.show();
				}
				
				Scene nodeScene = new Scene(nodeRoot);
				nodeScene.getStylesheets().add(getClass().
						getResource("application.css").toExternalForm());
				
				nodeStage.initModality(Modality.WINDOW_MODAL);
				nodeStage.setTitle("NodeMCU Monitor");
				nodeStage.setScene(nodeScene);
				
				nodeStage.setOnShown(e -> {
					
				});
				
				nodeStage.setOnCloseRequest(e -> {
					
					this.destroy();
				});
				
				nodeStage.show();
				
				nodeStage.setMinWidth(nodeStage.getWidth());
				nodeStage.setMinHeight(nodeStage.getHeight());
				
				this.nodeContainer = (HBox)nodeRoot.lookup("#node_container");
				this.nodeStage = nodeStage;
				this.runningNode.add(nodeName);
			} else {
				nodeContainer.getChildren().add(nodeRoot);
				nodeStage.sizeToScene();
				nodeStage.setMinWidth(nodeStage.getWidth());
				nodeStage.setMinHeight(nodeStage.getHeight());
				runningNode.add(nodeName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.nodeStage;
	}
	
	/**
	 * Close this window and free all resources
	 */
	public void destroy() {
		if (nodeStage != null) {
			this.nodeStage.close();
			this.runningNode.clear();
			this.nodeContainer = null;
			this.nodeStage = null;
		}
	}
	
	/**
	 * Get the content of this NodeWindow's stage. If the NodeWindow's
	 * stage hasn't been shown, then a new window will be opened with this
	 * node as content, otherwise this node will be appended to the stage.
	 * @param name id and key for all List or Map type resources.
	 * @return content node.
	 */
	private Parent getNodeContent(String name) {
		Parent nodeRoot = null;
		
		try {
			nodeRoot = FXMLLoader.load(getClass().getResource("/fxml/window-node.fxml"));
		} catch (Exception e) {
			this.ringAlert(AlertType.ERROR, "Error loading fxml!");
			return null;
		}
		
		/*
		ArrayList<TextField> nodeTxtFieldVolt = 
				new ArrayList<TextField>(Arrays.asList(
						(TextField)nodeRoot.lookup("#node_text_volt1"),
						(TextField)nodeRoot.lookup("#node_text_volt2"),
						(TextField)nodeRoot.lookup("#node_text_volt3")
				));
		ArrayList<TextField> nodeTxtFieldCurrent = 
				new ArrayList<TextField>(Arrays.asList(
						(TextField)nodeRoot.lookup("#node_text_current1"),
						(TextField)nodeRoot.lookup("#node_text_current2"),
						(TextField)nodeRoot.lookup("#node_text_current3")
				));//*/
		Button nodeBtnConnect = 
				(Button)nodeRoot.lookup("#node_button_connect");
		Button nodeBtnDisconnect = 
				(Button)nodeRoot.lookup("#node_button_disconnect");
		//*/
		
		Button nodeBtnClose = null;
		FileInputStream input = null;
		HBox nodeNameBar = (HBox)nodeRoot.lookup("#node_identifier");
		Label nodeLblName = (Label)nodeRoot.lookup("#node_name");
		Label nodeLblDate = (Label)nodeRoot.lookup("#node_label_date");
		Label nodeLblTime = (Label)nodeRoot.lookup("#node_label_time");
		@SuppressWarnings("unchecked")
		LineChart<Number, Number> chart = 
				(LineChart<Number, Number>)nodeRoot.lookup("#node_chart_line");
		LineChartManager<Number, Number> chartManager = 
				new LineChartManager<>("chart_" + name, chart);
		MenuBar nodeMenuBar = (MenuBar)nodeRoot.lookup("#node_menubar_menu");
		Menu nodeFileMenu = nodeMenuBar.getMenus().get(0);
		MenuItem nodeMenuItemFileOpen = nodeFileMenu.getItems().get(0);
		MenuItem nodeMenuItemFileSave = nodeFileMenu.getItems().get(1);
		MenuItem nodeMenuItemFileExport = nodeFileMenu.getItems().get(3);
		MenuItem nodeMenuItemFilePrint = nodeFileMenu.getItems().get(4);
		
		String chartKey = "chart_" + name;
		chart.setAnimated(false);
		chart.setLegendVisible(false);
		chart.setCreateSymbols(false);
		chart.setHorizontalGridLinesVisible(false);
		chart.setVerticalGridLinesVisible(false);
		((NumberAxis)chart.getXAxis()).setAutoRanging(true);
		((NumberAxis)chart.getYAxis()).setAutoRanging(true);
		((NumberAxis)chart.getYAxis()).setUpperBound(1);
		((NumberAxis)chart.getYAxis()).setLowerBound(-1);
		chartManager.attach("vr");
		
		this.chartManagers.put(chartKey, chartManager);
		this.nodeChartYAxis.put(chartKey, 0);
		
		this.updateChart(chartKey).start();
		
		try {
			input =  new FileInputStream("src/images/btn-close.jpg");
			Image image = new Image(input);
	        ImageView imageView = new ImageView(image);
	        imageView.setFitWidth(15);
	        imageView.setFitHeight(15);
			
	        nodeBtnClose = new Button("", imageView);
	        nodeBtnClose.setPadding(new Insets(0, 0, 0, 0));
		} catch (Exception e) {
			this.ringAlert(AlertType.ERROR, e.getMessage());
		}
		
		try {
			input.close();
		} catch(Exception e) {
			this.ringAlert(AlertType.ERROR, e.getMessage());
		}
        
        nodeNameBar.getChildren().add(nodeBtnClose);
        nodeLblName.setText(name);
        
        nodeBtnClose.setOnAction(e -> {
        	if (runningNode.size() == 1) {
        		this.destroy();
        	}
        	else {
        		this.nodeContainer.getChildren().
        			remove(nodeContainer.lookup("#vbox_" +name));
        		this.chartManagers.remove("chart_" + name);
        		this.runningNode.remove(name);
        		this.nodeStage.sizeToScene();
        	}
        });
        
        nodeBtnConnect.setOnAction(e -> {
        	chartManager.attach("vr");
        });
        
        nodeBtnDisconnect.setOnAction(e -> {
        	chartManager.detach("vr");
        });
        
		nodeMenuItemFileOpen.setOnAction(e -> {
			System.out.println("Opening File");
		});
		
		nodeMenuItemFileSave.setOnAction(e -> {
			System.out.println("Saving File");
		});
		
		nodeMenuItemFileExport.setOnAction(e -> {
			System.out.println("Exporting image");
		});
		
		nodeMenuItemFilePrint.setOnAction(e -> {
			System.out.println("Printing file");
		});
		
		WindowUtil.initDate(nodeLblDate, nodeLblTime);
		
		Parent nodeContent = (Parent) nodeRoot.lookup("#node_content");
		nodeContent.setId("vbox_" + name);
		
		if (this.nodeStage != null)
			return nodeContent;
		return nodeRoot;
	}
	
	/**
	 * Issue an alert with just a header.
	 * @param type alert type.
	 * @param message message to be alerted.
	 */
	private synchronized void ringAlert(Alert.AlertType type, String message) {
		Alert nodeAlert = new Alert(null);
		nodeAlert.setAlertType(type);
		nodeAlert.getDialogPane().getChildren().remove(1);
		nodeAlert.getDialogPane().getChildren().remove(1);
		nodeAlert.setHeaderText(message);
		nodeAlert.show();
	}
	
	/**
	 * Update chart of this content by creating a new Thread.
	 * @param name id and key for all List or Map type resources.
	 * @return 
	 */
	private Thread updateChart(String name) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Thread running");
				boolean flag = true;
				while (flag) {
					try {
						Thread.sleep(500);
						flag = NodeWindow.this.renewChart(name);
					} catch(Exception e) {
						e.printStackTrace();
						NodeWindow.this.ringAlert(AlertType.ERROR, e.getMessage());
					}
				}
			}
		});
		t.setName("thread-" + name);
		return t;
	}
	
	/**
	 * Synchronized method to be used by Thread returned by updateChart() method
	 * to renew chart data series and axis range.
	 * @param name id and key for all List or Map type resources.
	 * @return return false if name is not in the chart list, true otherwise.
	 */
	private synchronized boolean renewChart(String name) {
		if (!this.chartManagers.containsKey(name))
			return false;
		try {
			LineChartManager<Number, Number> cm = 
					this.chartManagers.get(name);
			cm.updateSeries("vr", new Data<>(this.runTime++, 4));
			// cm.attach("vr");
			/*LineChart<Number, Number> nodeLineChart =
					NodeWindow.this.nodeChartContainer.get(name);
			HashMap<String, Number> dataY = NodeWindow.this.nodeChartYAxis;
			NumberAxis xAxis = (NumberAxis) nodeLineChart.getXAxis();
			Series<Number, Number> dataSeries = WindowUtil.getSeries(name+"_vr");
			int size = dataSeries.getData().size();
			int dataXAxis = dataSeries.getData().get(size - 1).getXValue().intValue() + 1;
			double dataYAxis = dataY.get(name).doubleValue() + 0.3;
			if (dataYAxis > 90) dataYAxis = 0;
			dataY.remove(name);
			dataY.put(name, dataYAxis);
			System.out.println(Thread.currentThread().getName() + 
					": Add new data: " + dataXAxis + "," + dataYAxis);
			dataSeries.getData().add(new Data<Number, Number>
				(dataXAxis, Math.sin(dataYAxis)));
			if (size > 40)
				dataSeries.getData().remove(0);
			xAxis.setLowerBound(dataSeries.getData().get(0).
					getXValue().intValue());
			xAxis.setUpperBound(dataSeries.getData().get(size - 1).
					getXValue().intValue() + 1);//*/
		} catch (Exception e) {
			e.printStackTrace();
			this.ringAlert(AlertType.ERROR, e.getMessage());
		}
		return true;
	}
}
