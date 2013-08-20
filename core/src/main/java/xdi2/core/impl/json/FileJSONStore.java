package xdi2.core.impl.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

public class FileJSONStore implements JSONStore {

	private static final JsonParser jsonParser = new JsonParser();
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	@Override
	public JsonObject load(String id) throws IOException {

		String filename = filename(id);

		File file = new File(filename);
		if (! file.exists()) return new JsonObject();

		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		JsonElement jsonGraphElement = jsonParser.parse(bufferedReader);

		if (! (jsonGraphElement instanceof JsonObject)) throw new IOException("JSON must be an object: " + jsonGraphElement);

		return (JsonObject) jsonGraphElement;
	}

	@Override
	public void save(String id, JsonObject object) throws IOException {

		String filename = filename(id);

		FileWriter fileWriter = new FileWriter(new File(filename));
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		JsonWriter jsonWriter = new JsonWriter(bufferedWriter);

		gson.toJson(object, jsonWriter);

		jsonWriter.flush();
		bufferedWriter.flush();
		fileWriter.flush();
	}

	public void delete(final String id) throws IOException {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String filename) {

				return filename.startsWith(id);
			}
		});

		for (File file : files) file.delete();
	}

	private static String filename(String id) {

		StringBuilder buffer = new StringBuilder();
		buffer.append(id);
		buffer.append(".json");

		return buffer.toString();
	}
}
