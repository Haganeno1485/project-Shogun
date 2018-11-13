package nodemcu;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MCUHelper {
	
	/**
	 * Initialize a new date and time and then place it
	 * to the given label.
	 * @param date placement label for date
	 * @param time placement label for time
	 */
	public static void initDate(Label date, Label time) {
		Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
			String day, todayDate, month, year;
	    	String second, minute, hour;
	    	day = LocalDateTime.now().getDayOfWeek().
	    			getDisplayName(TextStyle.FULL, Locale.getDefault());
	    	todayDate = formatString(LocalDateTime.now().getDayOfMonth());
	    	month = LocalDateTime.now().getMonth().name();
	    	month = month.substring(0, 1) + month.substring(1).toLowerCase();
	    	year = String.valueOf(LocalDateTime.now().getYear());
	        second = formatString(LocalDateTime.now().getSecond());
	        minute = formatString(LocalDateTime.now().getMinute());
	        hour = formatString(LocalDateTime.now().getHour());
	        date.setText(day + ", " + todayDate + " " + month + " " + year);
	        time.setText(hour + ":" + (minute) + ":" + second);
	        time.autosize();
	    }),
	         new KeyFrame(Duration.seconds(1))
	    );
	    clock.setCycleCount(Animation.INDEFINITE);
	    clock.play();
	}
	
	/**
	 * Format integer into two-character string.
	 * @param unformatted integer
	 * @return return two-character string with "0" preceding if 
	 * the given parameter is a one-digit integer otherwise return
	 * the whole integer as string.
	 */
	public static String formatString(int unformatted) {
		String formatted = String.valueOf(unformatted);
		if (formatted.length() < 2)
			return "0" + formatted;
		else return formatted;
	}
	
	public static String[] getUnique(String...elements) {
		ArrayList<String> uniqueString = new ArrayList<>();
		for (String element : elements) {
			if (uniqueString.contains(element))
				continue;
			else
				uniqueString.add(element);
		}
		String[] u = {};
		return uniqueString.toArray(u);
	}
	
	public static synchronized void ringAlert(Alert.AlertType type, String message) {
		Alert nodeAlert = new Alert(type);
		nodeAlert.getDialogPane().getChildren().remove(1);
		nodeAlert.getDialogPane().getChildren().remove(1);
		nodeAlert.setHeaderText(message);
		nodeAlert.show();
	}

}
