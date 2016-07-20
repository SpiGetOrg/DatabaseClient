package org.spiget.database.test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import org.spiget.data.author.Author;
import org.spiget.data.category.Category;
import org.spiget.data.resource.Resource;
import org.spiget.data.resource.version.ResourceVersion;
import org.spiget.database.SpigetGson;

import java.io.IOException;

import static org.junit.Assert.*;


public class SerializerTest {

	@Test
	public void resourceSerializerTest() throws IOException {
		Resource resource = new Resource(1234, "a resource");
		resource.setAuthor(new Author(6643, "inventivetalent"));
		resource.setCategory(new Category(1, "fake category"));
		resource.setVersion(new ResourceVersion(0,"1.0"));

		String jsonString = SpigetGson.RESOURCE.toJson(resource);
		JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();

		assertTrue(jsonObject.has("author"));
		assertTrue(jsonObject.has("category"));

		JsonObject authorObject = jsonObject.getAsJsonObject("author");
		JsonObject categoryObject = jsonObject.getAsJsonObject("category");

		assertTrue(authorObject.has("id"));
		assertFalse(authorObject.has("name"));
		assertFalse(authorObject.has("icon"));

		assertTrue(categoryObject.has("id"));
		assertFalse(categoryObject.has("name"));
	}

}
