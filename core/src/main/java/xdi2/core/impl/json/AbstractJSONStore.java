package xdi2.core.impl.json;

import java.io.IOException;

import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorRemover;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class AbstractJSONStore implements JSONStore {

	private StringBuffer logBuffer;
	private boolean logEnabled;

	public AbstractJSONStore() {

		this.logBuffer = new StringBuffer();
		this.logEnabled = true;
	}

	@Override
	public final JsonObject load(String id) throws IOException {

		JsonObject jsonObject = this.loadInternal(id);

		if (this.getLogEnabled()) this.logBuffer.append("load( " + id + " , " + jsonObject + " )\n");

		return jsonObject;
	}

	@Override
	public final void save(String id, JsonObject jsonObject) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("save( " + id + " , " + jsonObject + " )\n");

		this.saveInternal(id, jsonObject);
	}

	@Override
	public final void saveToArray(String id, String key, JsonPrimitive jsonPrimitive) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("saveToArray( " + id + " , " + key + " , " + jsonPrimitive + " )\n");

		this.saveToArrayInternal(id, key, jsonPrimitive);
	}

	@Override
	public final void saveToObject(String id, String key, JsonElement jsonElement) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("saveToObject( " + id + " , " + key + " , " + jsonElement + " )\n");

		this.saveToObjectInternal(id, key, jsonElement);
	}

	@Override
	public final void delete(String id) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("delete( " + id + " )\n");

		this.deleteInternal(id);
	}

	@Override
	public final void deleteFromArray(String id, String key, JsonPrimitive jsonPrimitive) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("deleteFromArray( " + id + " , " + key + " , " + jsonPrimitive + " )\n");

		this.deleteFromArrayInternal(id, key, jsonPrimitive);
	}

	@Override
	public final void deleteFromObject(String id, String key) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("deleteFromObject( " + id + " , " + key + " )\n");

		this.deleteFromObjectInternal(id, key);
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
	 * Internal methods
	 */

	protected abstract JsonObject loadInternal(String id) throws IOException;

	protected abstract void saveInternal(String id, JsonObject jsonObject) throws IOException;

	protected void saveToArrayInternal(String id, String key, JsonPrimitive jsonPrimitive) throws IOException {

		JsonObject jsonObject = this.load(id);

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

		this.save(id, jsonObject);
	}

	protected void saveToObjectInternal(String id, String key, JsonElement jsonElement) throws IOException {

		JsonObject jsonObject = this.load(id);

		if (jsonObject == null) {

			jsonObject = new JsonObject();
			jsonObject.add(key, jsonElement);
		} else {

			jsonObject.add(key, jsonElement);
		}

		this.save(id, jsonObject);
	}

	protected abstract void deleteInternal(String id) throws IOException;

	protected void deleteFromArrayInternal(String id, String key, JsonPrimitive jsonPrimitive) throws IOException {

		JsonObject jsonObject = this.load(id);
		if (jsonObject == null) return;

		JsonArray jsonArray = jsonObject.getAsJsonArray(key);
		if (jsonArray == null) return;

		new IteratorRemover<JsonElement> (jsonArray.iterator(), jsonPrimitive).remove();

		this.save(id, jsonObject);
	}

	protected void deleteFromObjectInternal(String id, String key) throws IOException {

		JsonObject jsonObject = this.load(id);
		if (jsonObject == null) return;

		jsonObject.remove(key);

		this.save(id, jsonObject);
	}
}
