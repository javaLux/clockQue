/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Christian
 * Class to convert Json String to Map.
 */
public class OwnJsonConverter {
	
	// logger instance
	private final static Logger LOGGER = ApplicationLogger.getAppLogger();

	private OwnJsonConverter() {}
	
	/**
	 * Method converts a given json in a hashmap data structure, it works with
	 * Jackson framework see also pom file
	 * 
	 * @param json 	-> [String]		Json string to convert
	 * @return 		-> [HashMap] 	converted json string as Map
	 */
	public static Map<String, Object> convertWeatherJsonToMap(String json) {
		Map<String, Object> mapOfContent = new HashMap<String, Object>();
		
		if((json != null) && !(json.isEmpty())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapOfContent = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
				return mapOfContent;
				
			} catch (Exception ex) {
				LOGGER.error("Failed to convert weather data from json to map.\nJson:\n" + json, ex);
			}
		}
		else {
			LOGGER.warn("Json string can not be null or empty.\n Json: " + json);
		}
		
		return mapOfContent;
	}
	
	/**
	 * Method converts a given json in a hashmap data structure, it works with
	 * Jackson framework see also pom file
	 * 
	 * @param json 	-> [String]						Json string to convert
	 * @return 		-> [List<Map<String, Object>>] 	converted json string as List of Map's
	 */
	public static List<Map<String, Object>> convertCitiesJsonToMap(String json) {
		List<Map<String, Object>> listOfContent = new LinkedList<Map<String, Object>>();
		
		if((json != null) && !(json.isEmpty())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				listOfContent = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>(){});
				return listOfContent;
				
			} catch (Exception ex) {
				LOGGER.error("Failed to convert cities data from json to map.\nJson:\n" + json, ex);
			}
		}
		else {
			LOGGER.warn("Json string can not be null or empty.\n Json: " + json);
		}
		
		return listOfContent;
	}
	
	/**
	 * Methods for a pretty output of large data collections
	 */
	public static void prettyMapOutput(Map<? extends Object, ? extends Object> mapToPrint) {
		
		for(Entry<? extends Object, ? extends Object> entry : mapToPrint.entrySet()) {
			System.out.println("Key: >" + entry.getKey() + "<, " + "Value: >" + entry.getValue() + "<");
		}
	}
	
	public static void prettyListOutput(List<Map<String, Object>> listToPrint) {
		
		for(Map<String, Object> map : listToPrint) {
			prettyMapOutput(map);
		}
	}
}
