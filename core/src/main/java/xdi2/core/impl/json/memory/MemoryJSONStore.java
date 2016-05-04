package xdi2.core.impl.json.memory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xdi2.core.impl.json.AbstractJSONStore;
import xdi2.core.impl.json.JSONStore;

public class MemoryJSONStore extends AbstractJSONStore implements JSONStore {

	private Map<String, JsonObject> jsonObjects;

	public MemoryJSONStore() {

		this.jsonObjects = new ConcurrentHashMap<String, JsonObject> ();
	}

	@Override
	public void init() throws IOException {

	}

	@Override
	public void close() {

		this.jsonObjects.clear();
		this.jsonObjects = null;
	}

	private static JsonElement deepCopy(JsonElement jsonElement) {

		if (jsonElement instanceof JsonObject) return deepCopy((JsonObject) jsonElement);
		if (jsonElement instanceof JsonArray) return deepCopy((JsonArray) jsonElement);

		return jsonElement;
	}

	private static JsonObject deepCopy(JsonObject jsonObject) {

		JsonObject result = new JsonObject();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

			result.add(entry.getKey(), deepCopy(entry.getValue()));
		}

		return result;
	}

	private static JsonArray deepCopy(JsonArray jsonArray) {

		JsonArray result = new JsonArray();

		for (JsonElement element : jsonArray) {

			result.add(deepCopy(element));

		}
		return result;
	}

	@Override
	public JsonObject load(String id) throws IOException {

		JsonObject jsonObject = this.jsonObjects.get(id);
		if (jsonObject == null) return null;

		return deepCopy(jsonObject);
	}

	@Override
	public void save(String id, JsonObject jsonObject) throws IOException {

		this.jsonObjects.put(id, jsonObject);
	}

	@Override
	public void delete(String id) throws IOException {

		for (Iterator<Entry<String, JsonObject>> iterator = this.jsonObjects.entrySet().iterator(); iterator.hasNext(); ) {

			if (iterator.next().getKey().startsWith(id)) iterator.remove();
		}
	}
}
