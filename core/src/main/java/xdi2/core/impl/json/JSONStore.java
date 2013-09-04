package xdi2.core.impl.json;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * The JSON based graph storage implementations needs a JSONStore to function.
 * This defines basic operations on a JSON based datastore.
 * 
 * @author markus
 */
public interface JSONStore {

	public void init() throws IOException;
	public void close();

	public JsonObject load(String id) throws IOException;
	public void save(String id, JsonObject jsonObject) throws IOException;
	public void saveToArray(String id, String key, JsonPrimitive jsonPrimitive) throws IOException;
	public void saveToObject(String id, String key, JsonElement jsonElement) throws IOException;
	public void delete(String id) throws IOException;
	public void deleteFromArray(String id, String key, JsonPrimitive jsonPrimitive) throws IOException;
	public void deleteFromObject(String id, String key) throws IOException;
}
