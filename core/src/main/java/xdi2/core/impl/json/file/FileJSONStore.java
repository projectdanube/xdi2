package xdi2.core.impl.json.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.json.AbstractJSONStore;
import xdi2.core.impl.json.JSONStore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

public class FileJSONStore extends AbstractJSONStore implements JSONStore {

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private String prefix;

	public FileJSONStore(String prefix) {

		this.prefix = prefix;
	}

	@Override
	public void init() throws IOException {

	}

	@Override
	public void close() {

	}

	@Override
	protected JsonObject loadInternal(String id) throws IOException {

		String filename = filename(this.getPrefix(), id);

		File file = new File(filename);
		if (! file.exists()) return new JsonObject();

		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		JsonObject jsonGraphObject = gson.getAdapter(JsonObject.class).fromJson(bufferedReader);

		return jsonGraphObject;
	}

	@Override
	protected void saveInternal(String id, JsonObject object) throws IOException {

		String filename = filename(this.getPrefix(), id);

		FileWriter fileWriter = new FileWriter(new File(filename));
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		JsonWriter jsonWriter = new JsonWriter(bufferedWriter);

		gson.toJson(object, jsonWriter);

		jsonWriter.flush();
		bufferedWriter.flush();
		fileWriter.flush();
	}

	@Override
	protected void deleteInternal(final String id) throws IOException {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String filename) {

				return filename.startsWith(id);
			}
		});

		for (File file : files) file.delete();
	}

	public String getPrefix() {

		return this.prefix;
	}

	/*
	 * Helper methods
	 */

	private static String filename(String prefix, String id) {

		StringBuilder buffer = new StringBuilder();

		try {

			if (prefix != null) {

				buffer.append(URLEncoder.encode(prefix, "UTF-8"));
				buffer.append("_");
			}

			buffer.append(URLEncoder.encode(id, "UTF-8"));
		} catch (UnsupportedEncodingException ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		buffer.append(".json");

		return buffer.toString();
	}

	public static void cleanup() {

		cleanup(new File("."));
	}

	public static void cleanup(File path) {

		File[] files = path.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String filename) {

				return filename.endsWith(".json");
			}
		});

		for (File file : files) file.delete();
	}
}
