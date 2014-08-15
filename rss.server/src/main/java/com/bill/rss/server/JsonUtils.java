package com.bill.rss.server;

import org.codehaus.jackson.map.ObjectMapper;

import com.bill.rss.domain.ReaderException;

public class JsonUtils {


	public static String convertObjectToJson(Object objectToConvert) {
		String json = "{}";
	    ObjectMapper mapper = new ObjectMapper();
	    try {
			json = mapper.writeValueAsString(objectToConvert);
		} catch (Exception exception) {
			throw new ReaderException("Unable to generate JSON", exception);
		}
		return json;
	}
}
