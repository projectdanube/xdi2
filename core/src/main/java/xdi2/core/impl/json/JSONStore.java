package xdi2.core.impl.json;

import java.io.IOException;

import com.google.gson.JsonObject;

public interface JSONStore {

	public JsonObject load(String id) throws IOException;
	public void save(String id, JsonObject jsonObject) throws IOException;
	public void delete(String id) throws IOException;
}
