package org.spiget.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spiget.data.author.ListedAuthor;
import org.spiget.data.category.ListedCategory;
import org.spiget.data.resource.ResourceReview;
import org.spiget.data.resource.update.ResourceUpdate;
import org.spiget.data.resource.version.ListedResourceVersion;

public class SpigetGson {

	public static final Gson RESOURCE = new GsonBuilder()
			.registerTypeHierarchyAdapter(ListedAuthor.class, new IdOnlySerializer())
			.registerTypeHierarchyAdapter(ListedCategory.class, new IdOnlySerializer())
			.registerTypeHierarchyAdapter(ListedResourceVersion.class, new IdOnlySerializer())
			.registerTypeHierarchyAdapter(ResourceUpdate.class, new IdOnlySerializer())
			.registerTypeHierarchyAdapter(ResourceReview.class, new IdOnlySerializer())
			.create();

	public static final Gson RESOURCE_VERSION = new GsonBuilder()
			.create();

	public static final Gson RESOURCE_UPDATE = new GsonBuilder()
			.create();

	public static final Gson RESOURCE_REVIEW = new GsonBuilder()
			.registerTypeHierarchyAdapter(ListedAuthor.class, new IdOnlySerializer())
			.create();

	public static final Gson AUTHOR = new GsonBuilder()
			.create();

	public static final Gson CATEGORY = new GsonBuilder()
			.create();

	public static final Gson WEBHOOK = new GsonBuilder()
			.create();

	public static final Gson UPDATE_REQUEST = new GsonBuilder()
			.create();

}
