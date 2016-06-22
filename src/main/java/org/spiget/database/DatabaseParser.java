package org.spiget.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bson.Document;

public class DatabaseParser {

	public static JsonObject toJson(Document document) {
		if (document == null) { return null; }
		return new JsonParser().parse(document.toJson()).getAsJsonObject();
	}

	public static Document toDocument(JsonElement jsonObject) {
		return Document.parse(jsonObject.toString());
	}

}
