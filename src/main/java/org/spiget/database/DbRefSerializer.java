package org.spiget.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.spiget.data.IdReference;

import java.lang.reflect.Type;

public class DbRefSerializer implements JsonSerializer<IdReference> {

	private String collection;

	public DbRefSerializer(String collection) {
		this.collection = collection;
	}

	@Override
	public JsonElement serialize(IdReference src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$ref", this.collection);
		jsonObject.addProperty("$id", src.getId());
		return jsonObject;
	}
}
