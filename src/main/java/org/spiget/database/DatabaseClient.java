package org.spiget.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.bson.Document;
import org.spiget.data.UpdateRequest;
import org.spiget.data.author.Author;
import org.spiget.data.author.ListedAuthor;
import org.spiget.data.category.Category;
import org.spiget.data.category.ListedCategory;
import org.spiget.data.resource.ListedResource;
import org.spiget.data.resource.Resource;
import org.spiget.data.resource.ResourceReview;
import org.spiget.data.resource.update.ResourceUpdate;
import org.spiget.data.resource.version.ListedResourceVersion;
import org.spiget.data.webhook.Webhook;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Log4j2
public class DatabaseClient {

	private String          dbName;
	private String          host;
	private int             port;
	private MongoCredential credential;

	private MongoClient   mongoClient;
	private MongoDatabase mongoDatabase;

	public DatabaseClient(String dbName, String host, int port, String user, char[] pass, String authDatabase) {
		this.dbName = dbName;
		this.host = host;
		this.port = port;
		this.credential = MongoCredential.createScramSha1Credential(user, authDatabase, pass);
	}

	public int collectionCount() {
		int c = 0;
		for (String ignored : db().listCollectionNames()) {
			c++;
		}
		return c;
	}

	public void updateSystemStats(String prefix) {
		Runtime runtime = Runtime.getRuntime();
		MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		updateStatus(prefix + "system.memory.max", (maxMemory / 1024));
		updateStatus(prefix + "system.memory.free", (freeMemory / 1024));
		updateStatus(prefix + "system.memory.allocated", (allocatedMemory / 1024));
		updateStatus(prefix + "system.memory.used", (heapMemoryUsage.getUsed() / 1024));
		updateStatus(prefix + "system.memory.committed", (heapMemoryUsage.getCommitted() / 1024));
	}

	// Resource

	public Resource getResource(int id) {
		Document document = getResourcesCollection().find(new Document("_id", id)).limit(1).first();
		if (document == null) { return null; }
		JsonObject json = DatabaseParser.toJson(document);
		return SpigetGson.RESOURCE.fromJson(json, Resource.class);
	}

	public UpdateResult updateResource(ListedResource resource) {
		long unix = System.currentTimeMillis() / 1000;
		Document document = DatabaseParser.toDocument(SpigetGson.RESOURCE.toJsonTree(resource));
		return getResourcesCollection().updateOne(new Document("_id", resource.getId()), new Document("$set", document
				.append("fetch", new Document("latest", unix))));
	}

	public void insertResource(ListedResource resource) {
		long unix = System.currentTimeMillis() / 1000;
		Document document = DatabaseParser.toDocument(SpigetGson.RESOURCE.toJsonTree(resource));
		getResourcesCollection().insertOne(document
				.append("fetch", new Document("latest", unix)
						.append("first", unix)));
	}

	public void deleteResource(int id) {
		MongoCollection<Document> collection = getResourcesCollection();
		collection.deleteOne(new Document("_id", id));
	}

	// Resource Versions

	public UpdateResult updateOrInsertVersion(ListedResource resource, ListedResourceVersion version) {
		Document document = DatabaseParser.toDocument(SpigetGson.RESOURCE_VERSION.toJsonTree(version));
		return getResourceVersionsCollection().updateOne(new Document("_id", version.getId()), new Document("$set", document), new UpdateOptions().upsert(true));
	}

	// Resource Updates

	public UpdateResult updateOrInsertUpdate(ListedResource resource, ResourceUpdate update) {
		Document document = DatabaseParser.toDocument(SpigetGson.RESOURCE_UPDATE.toJsonTree(update));
		return getResourceUpdatesCollection().updateOne(new Document("_id", update.getId()), new Document("$set", document), new UpdateOptions().upsert(true));
	}

	// Resource Reviews

	public UpdateResult updateOrInsertReview(ListedResource resource, ResourceReview review) {
		Document document = DatabaseParser.toDocument(SpigetGson.RESOURCE_REVIEW.toJsonTree(review));
		return getResourceReviewsCollection().updateOne(new Document("_id", review.getId()), new Document("$set", document), new UpdateOptions().upsert(true));
	}

	// Author

	public Author getAuthor(int id) {
		Document document = getAuthorsCollection().find(new Document("_id", id)).limit(1).first();
		if (document == null) { return null; }
		JsonObject json = DatabaseParser.toJson(document);
		return SpigetGson.AUTHOR.fromJson(json, Author.class);
	}

	public UpdateResult updateAuthor(ListedAuthor author) {
		long unix = System.currentTimeMillis() / 1000;
		Document document = DatabaseParser.toDocument(SpigetGson.AUTHOR.toJsonTree(author));
		return getAuthorsCollection().updateOne(new Document("_id", author.getId()), new Document("$set", document
				.append("fetch", new Document("latest", unix))));
	}

	public void insertAuthor(ListedAuthor author) {
		long unix = System.currentTimeMillis() / 1000;
		Document document = DatabaseParser.toDocument(SpigetGson.AUTHOR.toJsonTree(author));
		getAuthorsCollection().insertOne(document
				.append("fetch", new Document("latest", unix)
						.append("first", unix)));
	}

	public UpdateResult updateOrInsertAuthor(ListedAuthor author) {
		Document document = DatabaseParser.toDocument(SpigetGson.AUTHOR.toJsonTree(author));
		return getAuthorsCollection().updateOne(new Document("_id", author.getId()), new Document("$set", document), new UpdateOptions().upsert(true));
	}

	// Category

	public Category getCategory(int id) {
		Document document = getCategoriesCollection().find(new Document("_id", id)).limit(1).first();
		if (document == null) { return null; }
		JsonObject json = DatabaseParser.toJson(document);
		return SpigetGson.CATEGORY.fromJson(json, Category.class);
	}

	public UpdateResult updateCategory(ListedCategory category) {
		Document document = DatabaseParser.toDocument(SpigetGson.CATEGORY.toJsonTree(category));
		return getCategoriesCollection().updateOne(new Document("_id", category.getId()), new Document("$set", document));
	}

	public void insertCategory(ListedCategory category) {
		Document document = DatabaseParser.toDocument(SpigetGson.CATEGORY.toJsonTree(category));
		getCategoriesCollection().insertOne(document);
	}

	public UpdateResult updateOrInsertCategory(ListedCategory category) {
		Document document = DatabaseParser.toDocument(SpigetGson.CATEGORY.toJsonTree(category));
		return getCategoriesCollection().updateOne(new Document("_id", category.getId()), new Document("$set", document), new UpdateOptions().upsert(true));
	}

	// Status

	public UpdateResult updateStatus(String key, Object value) {
		return getStatusCollection().updateOne(new Document("key", key), new Document("$set", new Document("key", key).append("value", value)), new UpdateOptions().upsert(true));
	}

	public <T> T getStatus(String key) {
		FindIterable<Document> documents = getStatusCollection().find(new Document("key", key)).limit(1);
		if (documents != null) {
			for (Document document : documents) {
				return (T) document.get("value");
			}
		}
		return null;
	}

	public UpdateResult renameStatus(String fromKey, String toKey) {
		return getStatusCollection().updateOne(new Document("key", fromKey), new Document("$set", new Document("key", toKey)));
	}

	// Webhook

	public Set<Webhook> getWebhooks(String eventType) {
		MongoCollection<Document> collection = getWebhooksCollection();
		FindIterable<Document> iterable = eventType == null ? collection.find() : collection.find(new Document("events", eventType));
		Set<Webhook> set = new HashSet<>();
		if (iterable != null) {
			for (Document document : iterable) {
				set.add(SpigetGson.WEBHOOK.fromJson(DatabaseParser.toJson(document), Webhook.class));
			}
		}
		return set;
	}

	public void updateWebhookStatus(Webhook webhook) {
		MongoCollection<Document> collection = getWebhooksCollection();
		collection.updateOne(new Document("_id", webhook.id),
				new Document("$set",
						new Document("failedConnections", webhook.failedConnections)
								.append("failStatus", webhook.failStatus)));
	}

	public void deleteWebhook(Webhook webhook) {
		MongoCollection<Document> collection = getWebhooksCollection();
		collection.deleteOne(new Document("_id", webhook.id));
	}

	// Metrics
	public void insertMetricsData(JsonObject data) {
		Document document = DatabaseParser.toDocument(data);
		getMetricsCollection().insertOne(document);
	}

	// Update Requests
	public Set<UpdateRequest> getUpdateRequests(int limit) {
		MongoCollection<Document> collection = getUpdateRequestsCollection();
		FindIterable<Document> iterable = collection.find().projection(Projections.fields(Projections.exclude("requested"))).limit(limit);
		Set<UpdateRequest> set = new HashSet<>();
		if (iterable != null) {
			for (Document document : iterable) {
				JsonObject json = DatabaseParser.toJson(document);
				try {
					set.add(SpigetGson.UPDATE_REQUEST.fromJson(json, UpdateRequest.class));
				} catch (JsonSyntaxException e) {
					log.log(Level.WARN, "Failed to parse UpdateRequest "+document.getObjectId("_id")+" to from json", e);
					log.warn(json.toString());
					throw e;
				}
			}
		}
		return set;
	}

	public void deleteUpdateRequest(UpdateRequest request) {
		MongoCollection<Document> collection = getUpdateRequestsCollection();
		collection.deleteMany(new Document("requestedId", request.getRequestedId()));
	}

	public ServerAddress connect(int timeout) throws IOException {
		if (mongoClient == null) {
			log.info("Connecting to MongoDB " + this.host + ":" + this.port + "...");
			mongoClient = new MongoClient(new ServerAddress(this.host, this.port), Collections.singletonList(this.credential), MongoClientOptions.builder().connectTimeout(timeout).build());
		}
		return mongoClient.getAddress();
	}

	public void disconnect() throws IOException {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	public MongoDatabase db() {
		if (mongoDatabase == null) {
			log.info("Initializing database '" + dbName + "'");
			mongoDatabase = mongoClient.getDatabase(dbName);
		}
		return mongoDatabase;
	}

	public MongoCollection<Document> authorsCollection;
	public MongoCollection<Document> resourcesCollection;
	public MongoCollection<Document> resourceVersionsCollection;
	public MongoCollection<Document> resourceUpdatesCollection;
	public MongoCollection<Document> resourceReviewsCollection;
	public MongoCollection<Document> categoriesCollection;
	public MongoCollection<Document> statusCollection;
	public MongoCollection<Document> webhooksCollection;
	public MongoCollection<Document> metricsCollection;
	public MongoCollection<Document> updateRequestsCollection;

	public MongoCollection<Document> getAuthorsCollection() {
		if (authorsCollection != null) { return authorsCollection; }
		return authorsCollection = db().getCollection("authors");
	}

	public MongoCollection<Document> getResourcesCollection() {
		if (resourcesCollection != null) { return resourcesCollection; }
		return resourcesCollection = db().getCollection("resources");
	}

	public MongoCollection<Document> getResourceVersionsCollection() {
		if (resourceVersionsCollection != null) { return resourceVersionsCollection; }
		return resourceVersionsCollection = db().getCollection("resource_versions");
	}

	public MongoCollection<Document> getResourceUpdatesCollection() {
		if (resourceUpdatesCollection != null) { return resourceUpdatesCollection; }
		return resourceUpdatesCollection = db().getCollection("resource_updates");
	}

	public MongoCollection<Document> getResourceReviewsCollection() {
		if (resourceReviewsCollection != null) { return resourceReviewsCollection; }
		return resourceReviewsCollection = db().getCollection("resource_reviews");
	}

	public MongoCollection<Document> getCategoriesCollection() {
		if (categoriesCollection != null) { return categoriesCollection; }
		return categoriesCollection = db().getCollection("categories");
	}

	public MongoCollection<Document> getStatusCollection() {
		if (statusCollection != null) { return statusCollection; }
		return statusCollection = db().getCollection("status");
	}

	public MongoCollection<Document> getWebhooksCollection() {
		if (webhooksCollection != null) { return webhooksCollection; }
		return webhooksCollection = db().getCollection("webhooks");
	}

	public MongoCollection<Document> getMetricsCollection() {
		if (metricsCollection != null) { return metricsCollection; }
		return metricsCollection = db().getCollection("metrics");
	}

	public MongoCollection<Document> getUpdateRequestsCollection() {
		if (updateRequestsCollection != null) { return updateRequestsCollection; }
		return updateRequestsCollection = db().getCollection("update_requests");
	}

}
