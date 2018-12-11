package nodemcu;

import java.util.HashMap;
import java.util.Iterator;

public class MCUDecoder {
	private HashMap<String, String> decodedString = new HashMap<>();
	
	public void decodeString(String str) {
		str = str.substring(1);
		str = str.substring(0, str.length() - 1) + ",";
		System.out.println(str);
		while (str.indexOf(',') >= 0) {
			System.out.println("Iterating . . ");
			int indexOfColon = str.indexOf(':');
			int indexOfComma = str.indexOf(',');
			
			String type = str.substring(0, indexOfColon).trim();
			String content = str.substring(indexOfColon + 1, indexOfComma).trim();
			String key = type.substring(1, type.length()-1).trim();
			String value = content.substring(1, content.length()-1).trim();
			
			System.out.println("Key: " + key + ", Value: " + value);
			decodedString.put(key, value);
			
			if (indexOfComma == str.length() - 1) break;
			else {
				str = str.substring(indexOfComma + 1);
			}
		}
	}
	
	public static String encodeString(String type, String content) {
		String str  = "{\"TYPE\":\"" + type + "\", \"CONTENT\":\"" + content + "\"}";
		System.out.println("Encoded message: " + str);
		return str;
	}
}
