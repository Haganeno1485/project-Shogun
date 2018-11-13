package application;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class LineChartManager<X, Y> {
	
	private volatile boolean flagActive = false;
	private boolean flagInit = false;
	private String id = null;
	private String[] keys = {
		"vr", "vs", "vt", "ir", "is", "it", "pr", "ps", "pt"	
	};
	private HashMap<String, XYChart.Series<X, Y>> chartSeries = 
			new HashMap<>();
	private LineChart<X, Y> lineChart = null;
	
	private void init(LineChart<X, Y> lineChart) {
		this.lineChart = lineChart;
		this.id = lineChart.getId();
		this.flagActive = true;
		this.initSeries();
	}
	
	private void initSeries() {
		if (this.flagInit) return;
		for (String key : keys) {
			this.chartSeries.put(id+key, new XYChart.Series<X, Y>());
		}
		this.flagInit = true;
	}
	
	public LineChartManager() {};
	
	public LineChartManager(LineChart<X, Y> lineChart) {
		this.init(lineChart);
	}
	
	public LineChartManager(String id, LineChart<X, Y> lineChart) {
		lineChart.setId(id);
		this.init(lineChart);
	}

	public synchronized void attach(String key) {
		key = this.id + key;
		if (this.lineChart.getData().
				contains(this.chartSeries.get(key)))
			return;
		this.lineChart.getData().
			add(this.chartSeries.get(key));
	}
	
	public void attachAll() {
		for (String k : keys) {
			String key = this.id + k;
			XYChart.Series<X, Y> series = this.chartSeries.get(key);
			if (this.lineChart.getData().contains(series))
				continue;
			else this.lineChart.getData().add(series);
		}
	}
	
	public void deactivate() {
		this.flagActive = false;
	}
	
	public void detach(String key) {
		key = this.id + key;
		if (!(this.lineChart.getData().
				contains(this.chartSeries.get(key))))
			return;
		this.lineChart.getData().
			remove(this.chartSeries.get(key));
	}
	
	public void detachAll() {
		for (String k : keys) {
			String key = this.id + k;
			XYChart.Series<X, Y> series = this.chartSeries.get(key);
			if (!(this.lineChart.getData().contains(series)))
				continue;
			else this.lineChart.getData().remove(series);
		}
	}
	
	public XYChart.Series<X, Y> getSeries(String key) {
		key = this.id + key;
		return this.chartSeries.get(key);
	}

	public synchronized boolean isActive() {
		return this.flagActive;
	}
	
	public void updateSeries(String key, XYChart.Data<X, Y> data) {
		key = this.id + key;
		this.chartSeries.get(key).getData().add(data);
	}
	
	public LineChart<X, Y> getChart() {
		return this.lineChart;
	}
	
	public String getId() {
		return this.lineChart.getId();
	}

}
