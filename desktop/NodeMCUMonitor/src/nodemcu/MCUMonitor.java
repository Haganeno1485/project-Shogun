package nodemcu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import fxmlcontroller.MCUNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class MCUMonitor{
	
	private volatile boolean flagUpdateSeries = true;
	private volatile int runtime = 0;
	
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
	
	public MCUMonitor(String id) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.load(getClass().getResource("/fxml/mcunode.fxml").
					openStream());
			this.mcuNode = loader.getController();
			this.mcuNode.mcuContainer.setId(id);
			this.init();
			this.plot("vr", "ir");
			// this.threadRun().start();
			MCUHelper.initDate(this.mcuNode.mcuLblDate, this.mcuNode.mcuLblTime);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCause().getMessage());
			MCUHelper.ringAlert(AlertType.ERROR, "FXML load failed!");
		}
	}
	
	public String getId() {
		return this.mcuNode.mcuContainer.getId();
	}
	
	public Node getMonitor() {
		return this.mcuNode.nodeContainer;
	}
	
	private void init() {
		this.prepareButton();
		this.prepareChart();
		this.prepareLabel();
		this.prepareMenu();
		this.prepareTextField();
	}
	
	private void prepareButton() {
		
		this.mcuNode.mcuBtnConnect.setOnAction(e -> {
			this.flagUpdateSeries = true;
		});
		
		this.mcuNode.mcuBtnDisconnect.setOnAction(e -> {
			this.flagUpdateSeries = false;
		});
	}
	
	private void prepareChart() {
		for (String code : this.seriesCodes) {
			String key = this.getId() + code;
			XYChart.Series<Number, Number> series =
					new XYChart.Series<>();
			series.setName(code.substring(0, 1).toUpperCase() + 
					code.substring(1));
			series.getData().add(new Data<>(0, 0));
			this.mcuSeries.put(key, series);
		}
		
		this.mcuNode.mcuChart.setCreateSymbols(false);
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
		
		this.mcuChartXAxis = (NumberAxis) 
				this.mcuNode.mcuChart.getXAxis();
		this.mcuChartYAxis = (NumberAxis) 
				this.mcuNode.mcuChart.getYAxis();
		
		mcuChartXAxis.setAutoRanging(false);
		mcuChartYAxis.setAutoRanging(true);
	}
	
	public void plot(String...elements) {
		this.mcuNode.mcuChart.getData().clear();
		this.mcuNode.mcuChart.setAnimated(false);
		elements = MCUHelper.getUnique(elements);
		for (String element : elements) {
			String key = this.getId() + element;
			XYChart.Series<Number, Number> series = 
					this.mcuSeries.get(key);
			series.getData().add(new Data<>(0, 0));
			this.mcuNode.mcuChart.getData().add(series);
		}
	}
	
	private void prepareLabel() {
		this.mcuNode.mcuLblTime.textProperty().addListener(e -> {
			if (this.flagUpdateSeries) {
				Random rand = new Random();
				for (TextField tf : this.mcuDataFields) {
					tf.setText(String.valueOf(rand.nextInt(50) - 25));
				}
				this.runtime++;
			}
		});
	}
	
	private void prepareMenu() {
		
	}
	
	private void prepareTextField() {
		mcuDataFields.addAll(
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
		
		for (TextField tf : mcuDataFields) {
			String code = tf.getId();
			String c = code.substring(code.length() - 2).toLowerCase();
			tf.textProperty().addListener(e -> {
				int runtime = MCUMonitor.this.runtime;
				double value = Double.parseDouble(tf.getText());
				this.mcuSeries.get(this.getId()+c).getData().
					add(new Data<>(runtime, value));
				if (runtime > 30) {
					this.mcuChartXAxis.setUpperBound(runtime + 2);
					this.mcuChartXAxis.setLowerBound(runtime - 30);
				} else {
					this.mcuChartXAxis.setUpperBound(35);
				}
			});
		}
	}
	
	public void stop() {
		this.flagUpdateSeries = false;
	}
	
}
