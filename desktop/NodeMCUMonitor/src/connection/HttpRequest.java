package connection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.scene.control.Alert.AlertType;
import nodemcu.MCUHelper;
import nodemcu.MCUMonitor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;

public class HttpRequest {
	
	private boolean enableLogging = false;
	
	private MCUMonitor monitor = null;
	private final String USER_AGENT = "Mozilla/5.0";
	private File log = null;
	
	public HttpRequest(MCUMonitor monitor) {
		this.monitor = monitor;
	}
	
	// HTTP GET request
	public String sendGet(String url, boolean enableDebug) throws Exception {
		
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			// optional default is GET
			con.setRequestMethod("GET");
	
			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
	
			int responseCode = con.getResponseCode();
			if (enableDebug) {
				this.log("\nSending 'GET' request to URL : " + url);
				this.log("\nResponse Code : " + responseCode);
			}
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				monitor.onRequestDone(inputLine);
				response.append(inputLine);
			}
			in.close();
	
			return response.toString();
		} catch (ConnectException ex) {
			this.log("There is no internet connection.");
		} catch (Exception ex) {
			this.log("Unknown error occured");
			ex.printStackTrace();
		}
		
		return "-1";
	}

	// HTTP POST request
	public String sendPost(String url, String param, boolean enableDebug) throws Exception {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(param);
			wr.flush();
			wr.close();
	
			int responseCode = con.getResponseCode();
			this.log("\n> Sending 'POST' request to URL : " + url);
			this.log("\n> Post parameters : " + param);
			this.log("\n> Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			boolean isEmpty = true;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine + "\n");
				isEmpty = false;
			}
			
			if (isEmpty) monitor.onRequestDone("0");
			
			in.close();
			con.disconnect();
	
			//return result
			return response.toString();
		} catch (ConnectException ex) {
			MCUHelper.ringAlert(AlertType.ERROR, "No internet connection!");
			this.log("> There is no internet connection.");
			this.monitor.stop();
		} catch (SocketException ex) {
			this.log("> Cannot connect to socked");
		} catch (Exception ex) {
			this.log("> Unknown error occured");
			this.log("> " + ex.getMessage());
			ex.printStackTrace();
		}
		return "-1";
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
				if (this.monitor != null) this.monitor.stop();
			}
		}
	}

}
