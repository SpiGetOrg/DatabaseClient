package org.spiget.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spiget.data.author.ListedAuthor;
import org.spiget.data.category.ListedCategory;

public class SpigetGson {

	public static final Gson RESOURCE = new GsonBuilder()
			.registerTypeHierarchyAdapter(ListedAuthor.class, new DbRefSerializer("authors"))
			.registerTypeHierarchyAdapter(ListedCategory.class, new DbRefSerializer("categories"))
			.create();

	public static final Gson AUTHOR = new GsonBuilder()
			.create();

	public static final Gson CATEGORY = new GsonBuilder()
			.create();

}
