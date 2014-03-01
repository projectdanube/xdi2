package xdi2.core.impl.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.AbstractGraph;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorRemover;

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

	private StringBuffer logBuffer;
	private boolean logEnabled;

	JSONGraph(GraphFactory graphFactory, String identifier, JSONStore jsonStore) {

		super(graphFactory, identifier);

		this.jsonStore = jsonStore;

		this.jsonRootContextNode = new JSONContextNode(this, null, null, XDIConstants.XRI_S_ROOT);
		this.jsonObjects = new HashMap<String, JsonObject> ();

		this.logBuffer = new StringBuffer();
		this.logEnabled = false;
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
	 * Getters and setters
	 */

	public JSONStore getJsonStore() {

		return this.jsonStore;
	}

	public StringBuffer getLogBuffer() {

		return this.logBuffer;
	}

	public boolean getLogEnabled() {

		return this.logEnabled;
	}

	public void setLogEnabled(boolean logEnabled) {

		this.logEnabled = logEnabled;
	}

	public void resetLogBuffer() {

		this.logBuffer = new StringBuffer();
	}

	/*
	 * Helper methods
	 */

	JsonObject jsonLoad(String id) {

		JsonObject jsonObjectCached = null;
		JsonObject jsonObject = null;

		try {

			jsonObjectCached = this.jsonObjects.get(id);
			
			if (jsonObjectCached != null) {
			
				jsonObject = jsonObjectCached;
				return jsonObject;
			}

			try {

				jsonObject = this.jsonStore.load(id);
				if (jsonObject == null) jsonObject = new JsonObject();

				this.jsonObjects.put(id, jsonObject);

				return jsonObject;
			} catch (IOException ex) {

				throw new Xdi2RuntimeException("Cannot load JSON at " + id + ": " + ex.getMessage(), ex);
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("load( " + id + " , " + jsonObject + " , cache " + (jsonObjectCached != null ? "HIT" : "MISS") + " )");

			if (this.getLogEnabled()) this.logBuffer.append("load( " + id + " , " + jsonObject + " , cache " + (jsonObjectCached != null ? "HIT" : "MISS") + " )\n");
		}
	}

	void jsonSave(String id, JsonObject jsonObject) {

		if (log.isTraceEnabled()) log.trace("save( " + id + " , " + jsonObject + " )");

		if (this.getLogEnabled()) this.logBuffer.append("save( " + id + " , " + jsonObject + " )\n");

		try {

			this.jsonStore.save(id, jsonObject);

			this.jsonObjects.put(id, jsonObject);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot save JSON at " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonSaveToArray(String id, String key, JsonPrimitive jsonPrimitive) {

		if (log.isTraceEnabled()) log.trace("saveToArray( " + id + " , " + key + " , " + jsonPrimitive + " )");

		if (this.getLogEnabled()) this.logBuffer.append("saveToArray( " + id + " , " + key + " , " + jsonPrimitive + " )\n");

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

			throw new Xdi2RuntimeException("Cannot save JSON to array " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonSaveToObject(String id, String key, JsonElement jsonElement) {

		if (log.isTraceEnabled()) log.trace("saveToObject( " + id + " , " + key + " , " + jsonElement + " )");

		if (this.getLogEnabled()) this.logBuffer.append("saveToObject( " + id + " , " + key + " , " + jsonElement + " )\n");

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

			throw new Xdi2RuntimeException("Cannot save JSON to object " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonDelete(String id) {

		if (log.isTraceEnabled()) log.trace("delete( " + id + " )");

		if (this.getLogEnabled()) this.logBuffer.append("delete( " + id + " )\n");

		try {

			this.jsonStore.delete(id);

			for (Iterator<Entry<String, JsonObject>> iterator = this.jsonObjects.entrySet().iterator(); iterator.hasNext(); ) {

				if (iterator.next().getKey().startsWith(id)) iterator.remove();
			}
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot delete JSON " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonDeleteFromArray(String id, String key, JsonPrimitive jsonPrimitive) {

		if (log.isTraceEnabled()) log.trace("deleteFromArray( " + id + " , " + key + " , " + jsonPrimitive + " )");

		if (this.getLogEnabled()) this.logBuffer.append("deleteFromArray( " + id + " , " + key + " , " + jsonPrimitive + " )\n");

		try {

			this.jsonStore.deleteFromArray(id, key, jsonPrimitive);

			JsonObject jsonObject = this.jsonObjects.get(id);
			if (jsonObject == null) return;

			JsonArray jsonArray = jsonObject.getAsJsonArray(key);
			if (jsonArray == null) return;

			new IteratorRemover<JsonElement> (jsonArray.iterator(), jsonPrimitive).remove();

			this.jsonObjects.put(id, jsonObject);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot remove JSON from array " + id + ": " + ex.getMessage(), ex);
		}
	}

	void jsonDeleteFromObject(String id, String key) {

		if (log.isTraceEnabled()) log.trace("deleteFromObject( " + id + " , " + key + " )");

		if (this.getLogEnabled()) this.logBuffer.append("deleteFromObject( " + id + " , " + key + " )\n");

		try {

			this.jsonStore.deleteFromObject(id, key);

			JsonObject jsonObject = this.jsonObjects.get(id);
			if (jsonObject == null) return;

			jsonObject.remove(key);

			this.jsonObjects.put(id, jsonObject);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot remove JSON from object " + id + ": " + ex.getMessage(), ex);
		}
	}
}
