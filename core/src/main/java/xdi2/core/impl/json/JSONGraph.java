package xdi2.core.impl.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.AbstractGraph;
import xdi2.core.util.iterators.IteratorContains;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JSONGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -7459785412219244590L;

	private static final Logger log = LoggerFactory.getLogger(JSONContextNode.class);

	private final JSONStore jsonStore;

	private final JSONContextNode jsonRootContextNode;
	private final Map<String, JsonObject> jsonObjects;

	JSONGraph(GraphFactory graphFactory, String identifier, JSONStore jsonStore) {

		super(graphFactory, identifier);

		this.jsonStore = jsonStore;

		this.jsonRootContextNode = new JSONContextNode(this, null, "()", null);
		this.jsonObjects = new HashMap<String, JsonObject> ();
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.jsonRootContextNode;
	}

	@Override
	public void close() {

		this.jsonStore.close();
	}

	/*
	 * Methods related to transactions
	 */

	@Override
	public boolean supportsTransactions() {

		return false;
	}

	@Override
	public void beginTransaction() {

		this.jsonObjects.clear();
	}

	@Override
	public void commitTransaction() {

		this.jsonObjects.clear();
	}

	@Override
	public void rollbackTransaction() {

		this.jsonObjects.clear();
	}

	/*
	 * Misc methods
	 */

	/**
	 * Returns the JSON store this graph is based on.
	 * WARNING: Do not alter the contents of the store using this method, or your XDI graph may get corrupted.
	 * @return The JSON store backing this graph.
	 */
	public JSONStore getJSONStore() {

		return this.jsonStore;
	}

	/*
	 * Helper methods
	 */

	JsonObject jsonLoad(String id) {

		if (log.isTraceEnabled()) log.trace("Loading JSON " + id);

		JsonObject jsonObject = this.jsonObjects.get(id);
		if (jsonObject != null) return jsonObject;

		try {

			jsonObject = this.jsonStore.load(id);
			if (jsonObject == null) jsonObject = new JsonObject();

			this.jsonObjects.put(id, jsonObject);

			return jsonObject;
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot load JSON at " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonSave(String id, JsonObject jsonObject) {

		if (log.isTraceEnabled()) log.trace("Saving JSON " + id);

		try {

			this.jsonStore.save(id, jsonObject);

			this.jsonObjects.put(id, jsonObject);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot save JSON at " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonSaveToArray(String id, String key, JsonPrimitive jsonPrimitive) {

		if (log.isTraceEnabled()) log.trace("Saving JSON to array " + id + " at " + key);

		try {

			this.jsonStore.saveToArray(id, key, jsonPrimitive);

			JsonObject jsonObject = this.jsonObjects.get(id);

			if (jsonObject == null) {

				jsonObject = new JsonObject();
				JsonArray jsonArray = new JsonArray();
				jsonArray.add(jsonPrimitive);
				jsonObject.add(key, jsonArray);
			} else {

				JsonArray jsonArray = jsonObject.getAsJsonArray(key);

				if (jsonArray == null) { 

					jsonArray = new JsonArray();
					jsonArray.add(jsonPrimitive);
					jsonObject.add(key, jsonArray);
				} else {

					if (! new IteratorContains<JsonElement> (jsonArray.iterator(), jsonPrimitive).contains()) jsonArray.add(jsonPrimitive);
				}
			}

			this.jsonObjects.put(id, jsonObject);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot save JSON to array at " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonSaveToObject(String id, String key, JsonElement jsonElement) {

		if (log.isTraceEnabled()) log.trace("Saving JSON to object " + id + " at " + key);

		try {

			this.jsonStore.saveToObject(id, key, jsonElement);

			JsonObject jsonObject = this.jsonObjects.get(id);

			if (jsonObject == null) {

				jsonObject = new JsonObject();
				jsonObject.add(key, jsonElement);
			} else {

				jsonObject.add(key, jsonElement);
			}

			this.jsonObjects.put(id, jsonObject);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot save JSON to array at " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonDelete(String id) {

		if (log.isTraceEnabled()) log.trace("Deleting JSON " + id);

		try {

			this.jsonStore.delete(id);
			this.jsonObjects.remove(id);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot delete JSON at " + id + ": " + ex.getMessage(), ex);
		}
	}
}
