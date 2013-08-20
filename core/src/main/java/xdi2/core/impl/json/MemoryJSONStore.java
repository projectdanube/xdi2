package xdi2.core.impl.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonObject;

public class MemoryJSONStore implements JSONStore {

	private Map<String, JsonObject> jsonObjects;

	public MemoryJSONStore() {

		this.jsonObjects = new HashMap<String, JsonObject> ();
	}

	@Override
	public JsonObject load(String id) throws IOException {

		return this.jsonObjects.get(id);
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
