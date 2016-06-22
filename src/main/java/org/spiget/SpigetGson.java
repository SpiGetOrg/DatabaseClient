package org.spiget;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spiget.data.author.ListedAuthor;
import org.spiget.data.category.ListedCategory;

public class SpigetGson {

	public static final Gson RESOURCE = new GsonBuilder()
			.registerTypeHierarchyAdapter(ListedAuthor.class, new IdOnlySerializer())
			.registerTypeHierarchyAdapter(ListedCategory.class, new IdOnlySerializer())
			.create();

	public static final Gson AUTHOR = new GsonBuilder()
			.create();

	public static final Gson CATEGORY = new GsonBuilder()
			.create();

}
