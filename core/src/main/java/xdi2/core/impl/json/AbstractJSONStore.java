package xdi2.core.impl.json;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorRemover;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class AbstractJSONStore implements JSONStore {

	@Override
	public Map<String, JsonObject> loadWithPrefix(String id) throws IOException {

		JsonObject jsonObject = this.load(id);

		if (jsonObject == null)
			return Collections.emptyMap();
		else
			return Collections.singletonMap(id, jsonObject);
	}

	@Override
	public void saveToArray(String id, String key, JsonPrimitive jsonPrimitive) throws IOException {

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

	@Override
	public void saveToObject(String id, String key, JsonElement jsonElement) throws IOException {

		JsonObject jsonObject = this.load(id);

		if (jsonObject == null) {

			jsonObject = new JsonObject();
			jsonObject.add(key, jsonElement);
		} else {

			jsonObject.add(key, jsonElement);
		}

		this.save(id, jsonObject);
	}

	@Override
	public void deleteFromArray(String id, String key, JsonPrimitive jsonPrimitive) throws IOException {

		JsonObject jsonObject = this.load(id);
		if (jsonObject == null) return;

		JsonArray jsonArray = jsonObject.getAsJsonArray(key);
		if (jsonArray == null) return;

		new IteratorRemover<JsonElement> (jsonArray.iterator(), jsonPrimitive).remove();

		this.save(id, jsonObject);
	}

	@Override
	public void deleteFromObject(String id, String key) throws IOException {

		JsonObject jsonObject = this.load(id);
		if (jsonObject == null) return;

		jsonObject.remove(key);

		this.save(id, jsonObject);
	}
}
