package org.spiget.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.spiget.data.IdReference;
import org.spiget.data.UuidHolder;

import java.lang.reflect.Type;
import java.util.UUID;

public class IdAndUuidSerializer extends IdOnlySerializer {

    public JsonElement serialize(IdReference src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = (JsonObject) super.serialize(src, typeOfSrc, context);
        if (src instanceof UuidHolder) {
            UUID uuid = ((UuidHolder) src).getUuid();
            if (uuid != null) { jsonObject.addProperty("uuid", uuid.toString()); }
        }
        return jsonObject;
    }

}
