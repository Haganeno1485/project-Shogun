package nodemcu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import connection.HttpRequest;
import fxmlcontroller.MCUNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;

import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class MCUMonitor{
	
	private boolean enableLogging = false;
	private volatile boolean flagUpdateSeries = false;
	private volatile int runtime = 0;
	private int lastId = -1;
	
	private File log = null;
	private ObservableList<TextField> mcuDataFields = 
			FXCollections.observableArrayList();
	private HashMap<String, XYChart.Series<Number, Number>> 
		mcuSeries = new HashMap<>();
	
	private MCUNode mcuNode = null;
	private NumberAxis mcuChartXAxis, mcuChartYAxis = null;
	private String []seriesCodes = {
			"vr", "vs", "vt",
			"ir", "is", "it",
			"pr", "ps", "pt",
		};
	
	/**
	 * This class constructor
	 * @param id this class instance id
	 */
	public MCUMonitor(String id) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.load(getClass().getResource("/fxml/mcunode.fxml").
					openStream());
			this.mcuNode = loader.getController();
			this.mcuNode.mcuContainer.setId(id);
			this.initComponents();
			this.plot("vr", "ir");
			MCUHelper.initDate(this.mcuNode.mcuLblDate, this.mcuNode.mcuLblTime);
		} catch (IOException e) {
			this.log(e.getMessage());
			this.log(e.getCause().getMessage());
			MCUHelper.ringAlert(AlertType.ERROR, "FXML load failed!");
		}
	}
	
	/**
	 * Get the id of this monitor instance
	 * @return monitor id
	 */
	public String getId() {
		return this.mcuNode.mcuContainer.getId();
	}
	
	public Node getMonitor() {
		return this.mcuNode.nodeContainer;
	}
	
	/**
	 * Initiate the components for the monitor
	 */
	private void initComponents() {
		this.initChart();
		this.initLabels();
		this.initMenus();
		this.initTextFields();
		this.prepareButton();
	}
	
	/**
	 * Initate the button(s)
	 */
	private void prepareButton() {
		this.mcuNode.mcuBtnConnect.setOnAction(e -> {
			try {
				if (!flagUpdateSeries) {
					this.mcuNode.mcuBtnConnect.setText("Disconnect");
					this.flagUpdateSeries = true;
				} else {
					this.flagUpdateSeries = false;
					this.mcuNode.mcuBtnConnect.setText("Connect");
				}
			} catch (Exception s) {
				s.printStackTrace();
			}
		});
	}
	
	/**
	 * Initiate the chart
	 */
	private void initChart() {
		// Create series for the chart
		for (String code : this.seriesCodes) {
			String key = this.getId() + code;
			String seriesName = code.substring(0, 1).toUpperCase() + 
					code.substring(1);
			XYChart.Series<Number, Number> series =
					new XYChart.Series<>();
			series.setName(seriesName);
			series.getData().add(new Data<>(0, 0));
			this.mcuSeries.put(key, series);
		}
		
		this.mcuNode.mcuChart.setCreateSymbols(false);
		
		// Add a listener to plot group menu
		this.mcuNode.mcuPlotGroup.selectedToggleProperty().
			addListener(e -> {
				RadioMenuItem selected = 
						(RadioMenuItem)this.mcuNode.mcuPlotGroup.
							getSelectedToggle();
				String strSelected = selected.getText().toLowerCase().trim();
				
				if (strSelected.equals("vi - t line r")) {
					this.plot("vr", "ir");
				} 
				if (strSelected.equals("vi - t line s")) {
					this.plot("vs", "is");
				} 
				if (strSelected.equals("vi - t line t")) {
					this.plot("vt", "it");
				} 
				if (strSelected.equals("vi - t all line")) {
					this.plot("vr", "vs", "vt", "ir", "is", "it");
				}
				if (strSelected.equals("p - t all line")){
					this.plot("pr", "ps", "pt");
				}
			});//*/
		
		// Configure the X and Y axis
		this.mcuChartXAxis = (NumberAxis) 
				this.mcuNode.mcuChart.getXAxis();
		this.mcuChartYAxis = (NumberAxis) 
				this.mcuNode.mcuChart.getYAxis();
		this.mcuChartXAxis.setAutoRanging(false);
		this.mcuChartYAxis.setAutoRanging(true);
	}
	
	/**
	 * Plot the selected elements in plot group menu
	 * @param elements the selected elements
	 */
	public void plot(String...elements) {
		this.mcuNode.mcuChart.getData().clear();
		this.mcuNode.mcuChart.setAnimated(false);
		String[] u = {};
		elements = MCUHelper.<String>unique(elements).toArray(u);
		for (String element : elements) {
			String key = this.getId() + element;
			this.mcuNode.mcuChart.getData().add(this.mcuSeries.get(key));
		}
	}
	
	/**
	 * Initiate the labels
	 */
	private void initLabels() {
		// Add a listener to the time label
		this.mcuNode.mcuLblTime.textProperty().addListener(e -> {
			// A http post request will be send everytime the time label
			// text changed.
			if (flagUpdateSeries) {
				HttpRequest http = new HttpRequest(MCUMonitor.this);
				String url = "http://monitor00.000webhostapp.com/monitor/mcutransfer.php";
				String param = "lastID=" + lastId+"&code="+this.getId();
				String response;
				try {
					response = http.sendPost(url, param, false);
					MCUMonitor.this.onRequestDone(response);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void initMenus() {
		
	}
	
	/**
	 * Initiate the textfields
	 */
	private void initTextFields() {
		this.mcuNode.gridContainer.setAlignment(Pos.CENTER);
		this.mcuDataFields.addAll(
				this.mcuNode.mcuDataVR,
				this.mcuNode.mcuDataVS,
				this.mcuNode.mcuDataVT,
				this.mcuNode.mcuDataIR,
				this.mcuNode.mcuDataIS,
				this.mcuNode.mcuDataIT,
				this.mcuNode.mcuDataPR,
				this.mcuNode.mcuDataPS,
				this.mcuNode.mcuDataPT
			);
		
		// Add a listener to the textfields.
		for (TextField tf : mcuDataFields) {
			String code = tf.getId();
			String c = code.substring(code.length() - 2).toLowerCase();
			tf.textProperty().addListener(e -> {
				// A new data will be added to the chart series everytime the 
				// textfields text changed
				int runtime = MCUMonitor.this.runtime;
				double value = Double.parseDouble(tf.getText());
				ObservableList<Data<Number, Number>> data = this.mcuSeries.get(this.getId()+c).getData();
				data.add(new Data<>(runtime, value));
				int dataSize = data.size();
				if (dataSize > 100);
					data.remove(0, dataSize - 100);
					
			});
		}
	}
	
	public void onRequestDone(String message) {
		if (message.equals("0") || message.equals("<br />")) {
			return;
		}
		try {
			int indexOfNewline = message.indexOf('\n');
			while (message.contains("\n")) {
				String piece = message.substring(0, indexOfNewline);
				ArrayList<String> array = new ArrayList<>();
				int indexOfSpace = piece.indexOf(' ');
				while (indexOfSpace > 0) {
					array.add(piece.substring(0, indexOfSpace));
					piece = piece.substring(indexOfSpace + 1);
					indexOfSpace = piece.indexOf(' ');
				}
				if (piece.length() > 0) array.add(piece);
				this.lastId = Integer.parseInt(array.get(0));
				for (int i = 0; i < 9 ; i++) {
					this.mcuDataFields.get(i).setText(array.get(i + 2));
				}
				this.runtime = this.lastId;
				if (indexOfNewline == message.lastIndexOf(' '));
				message = message.substring(indexOfNewline + 1);
				indexOfNewline = message.indexOf("\n");
			}
			if (runtime > 30) {
				this.mcuChartXAxis.setUpperBound(runtime + 2);
				this.mcuChartXAxis.setLowerBound(runtime - 30);
			} else {
				this.mcuChartXAxis.setUpperBound(35);
			}
		} catch (Exception e) {
			this.log(e.getMessage());
			//e.printStackTrace();
		}
	}
	
	public void bind(Stage stage) {
		double ratio = this.mcuNode.mcuChart.getHeight() / 
				100 * stage.getHeight();
		this.mcuNode.mcuContainer.prefHeightProperty().
			bind(stage.heightProperty());
		this.mcuNode.mcuContainer.prefWidthProperty().
			bind(stage.widthProperty());
		this.mcuNode.mcuChartContainer.prefHeightProperty().
			bind(stage.heightProperty().multiply(ratio));
		this.mcuNode.mcuChart.prefWidthProperty().
			bind(this.mcuNode.mcuChartContainer.
					widthProperty().subtract(20.0));
		this.mcuNode.mcuChart.prefHeightProperty().
			bind(this.mcuNode.mcuChartContainer.
					heightProperty().subtract(20.0));//*/
	}
	
	public void stop() {
		this.mcuNode.mcuBtnConnect.setText("Connect");
		this.flagUpdateSeries = false;
	}
	
	// Log information into file
	private void log(String txt) {
		if (this.enableLogging) {
			try {
				if (this.log == null) {
					String timeStamp = new SimpleDateFormat("dd-MM-yyyy").
							format((Calendar.getInstance().getTime()));
					log = new File(timeStamp + this.getClass().toString() + 
							"-log.txt");
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(this.log, true));
				writer.write(txt);
				writer.newLine();
				writer.close();
			} catch (Exception e) {
				MCUHelper.ringAlert(AlertType.ERROR, e.getMessage());
				this.stop();
			}
		}
	}
	
}
