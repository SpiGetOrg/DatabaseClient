package org.spiget.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spiget.data.author.ListedAuthor;
import org.spiget.data.category.ListedCategory;
import org.spiget.data.resource.update.ResourceUpdate;
import org.spiget.data.resource.version.ListedResourceVersion;

public class SpigetGson {

	public static final Gson RESOURCE = new GsonBuilder()
			.registerTypeHierarchyAdapter(ListedAuthor.class, new DbRefSerializer("authors"))
			.registerTypeHierarchyAdapter(ListedCategory.class, new DbRefSerializer("categories"))
			.registerTypeHierarchyAdapter(ListedResourceVersion.class, new DbRefSerializer("resource_versions"))
			.registerTypeHierarchyAdapter(ResourceUpdate.class, new DbRefSerializer("resource_updates"))
			.create();

	public static final Gson RESOURCE_VERSION = new GsonBuilder()
			.create();

	public static final Gson RESOURCE_UPDATE = new GsonBuilder()
			.create();

	public static final Gson AUTHOR = new GsonBuilder()
			.create();

	public static final Gson CATEGORY = new GsonBuilder()
			.create();

}
