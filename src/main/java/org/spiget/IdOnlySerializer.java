package org.spiget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.spiget.data.IdReference;

import java.lang.reflect.Type;

public class IdOnlySerializer implements JsonSerializer<IdReference> {

	public JsonElement serialize(IdReference src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", src.getId());
		return jsonObject;
	}

}
