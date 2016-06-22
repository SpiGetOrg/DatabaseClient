package org.spiget.database;

import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.spiget.data.author.Author;
import org.spiget.data.author.ListedAuthor;
import org.spiget.data.resource.ListedResource;
import org.spiget.data.resource.Resource;

import java.io.IOException;
import java.util.Collections;

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

	public int databaseCount() {
		int c = 0;
		for (String s : mongoClient.listDatabaseNames()) {
			c++;
		}
		return c;
	}

	// Resource

	public Resource getResource(int id) {
		Document document = getResourcesCollection().find(new Document("_id", id)).limit(1).first();
		if (document == null) { return null; }
		JsonObject json = DatabaseParser.toJson(document);
		return SpigetGson.RESOURCE.fromJson(json, Resource.class);
	}

	public UpdateResult updateResource(ListedResource resource) {
		Document document = DatabaseParser.toDocument(SpigetGson.RESOURCE.toJsonTree(resource));
		return getResourcesCollection().updateOne(new Document("_id", resource.getId()), new Document("$set", document));
	}

	public void insertResource(ListedResource resource) {
		Document document = DatabaseParser.toDocument(SpigetGson.RESOURCE.toJsonTree(resource));
		getResourcesCollection().insertOne(document);
	}

	// Author

	public Author getAuthor(int id) {
		Document document = getAuthorsCollection().find(new Document("_id", id)).limit(1).first();
		if (document == null) { return null; }
		JsonObject json = DatabaseParser.toJson(document);
		return SpigetGson.AUTHOR.fromJson(json, Author.class);
	}

	public UpdateResult updateAuthor(ListedAuthor author) {
		Document document = DatabaseParser.toDocument(SpigetGson.AUTHOR.toJsonTree(author));
		return getAuthorsCollection().updateOne(new Document("_id", author.getId()), new Document("$set", document));
	}

	public void insertAuthor(ListedAuthor author) {
		Document document = DatabaseParser.toDocument(SpigetGson.AUTHOR.toJsonTree(author));
		getAuthorsCollection().insertOne(document);
	}

	// Status

	public UpdateResult updateStatus(String key, Object value) {
		return getStatusCollection().updateOne(new Document("key", key), new Document("$set", new Document("value", value)));
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

	public MongoCollection<Document> getAuthorsCollection() {
		return db().getCollection("authors");
	}

	public MongoCollection<Document> getResourcesCollection() {
		return db().getCollection("resources");
	}

	public MongoCollection<Document> getCategoriesCollection() {
		return db().getCollection("categories");
	}

	public MongoCollection<Document> getStatusCollection() {
		return db().getCollection("status");
	}

	public MongoCollection<Document> getWebhooksCollection() {
		return db().getCollection("webhooks");
	}

}
