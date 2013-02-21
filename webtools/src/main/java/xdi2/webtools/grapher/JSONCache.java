package xdi2.webtools.grapher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JSONCache {

	private static final long EXPIRE_MILLISECONDS = 2 * 60 * 1000;	// 2 minutes

	private static Map<String, JSONCacheEntry> jsonCache = new HashMap<String, JSONCacheEntry> ();

	private static class JSONCacheEntry {

		private Date date;
		private String data;

		private JSONCacheEntry(String data) {

			this.date = new Date();
			this.data = data;
		}
	}

	public static void put(String jsonId, String data) {

		jsonCache.put(jsonId, new JSONCacheEntry(data));
	}

	public static String get(String jsonId) {

		expireCache();

		JSONCacheEntry jsonCacheEntry = jsonCache.get(jsonId);
		if (jsonCacheEntry == null) return null;

		return jsonCacheEntry.data;
	}

	private static void expireCache() {

		List<String> deleteEntries = new ArrayList<String> ();

		for (Entry<String, JSONCacheEntry> entry : jsonCache.entrySet()) {

			if (entry.getValue().date.getTime() + EXPIRE_MILLISECONDS < System.currentTimeMillis()) {

				deleteEntries.add(entry.getKey());
			}
		}

		for (String deleteEntry : deleteEntries) jsonCache.remove(deleteEntry);
	}
}
