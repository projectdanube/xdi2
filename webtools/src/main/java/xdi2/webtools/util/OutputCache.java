package xdi2.webtools.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xdi2.core.Graph;

public class OutputCache {

	private static final long EXPIRE_MILLISECONDS = 2 * 60 * 1000;	// 2 minutes

	private static Map<String, GraphCacheEntry> outputCache = new HashMap<String, GraphCacheEntry> ();

	private static class GraphCacheEntry {

		private Date date;
		private Graph data;

		private GraphCacheEntry(Graph data) {

			this.date = new Date();
			this.data = data;
		}
	}

	public static void put(String outputId, Graph data) {

		outputCache.put(outputId, new GraphCacheEntry(data));
	}

	public static Graph get(String outputId) {

		expireCache();

		GraphCacheEntry outputCacheEntry = outputCache.get(outputId);
		if (outputCacheEntry == null) return null;

		return outputCacheEntry.data;
	}

	private static void expireCache() {

		List<String> deleteEntries = new ArrayList<String> ();

		for (Entry<String, GraphCacheEntry> entry : outputCache.entrySet()) {

			if (entry.getValue().date.getTime() + EXPIRE_MILLISECONDS < System.currentTimeMillis()) {

				deleteEntries.add(entry.getKey());
			}
		}

		for (String deleteEntry : deleteEntries) outputCache.remove(deleteEntry);
	}
}
