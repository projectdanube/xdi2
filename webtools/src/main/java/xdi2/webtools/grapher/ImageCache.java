package xdi2.webtools.grapher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ImageCache {

	private static final long EXPIRE_MILLISECONDS = 2 * 60 * 1000;	// 2 minutes

	private static Map<String, ImageCacheEntry> imageCache = new HashMap<String, ImageCacheEntry> ();

	private static class ImageCacheEntry {

		private Date date;
		private byte[] data;

		private ImageCacheEntry(byte[] data) {

			this.date = new Date();
			this.data = data;
		}
	}

	public static void put(String imageId, byte[] data) {

		imageCache.put(imageId, new ImageCacheEntry(data));
	}

	public static byte[] get(String imageId) {

		expireCache();

		ImageCacheEntry imageCacheEntry = imageCache.get(imageId);
		if (imageCacheEntry == null) return null;

		return imageCacheEntry.data;
	}

	private static void expireCache() {

		List<String> deleteEntries = new ArrayList<String> ();

		for (Entry<String, ImageCacheEntry> entry : imageCache.entrySet()) {

			if (entry.getValue().date.getTime() + EXPIRE_MILLISECONDS < System.currentTimeMillis()) {

				deleteEntries.add(entry.getKey());
			}
		}

		for (String deleteEntry : deleteEntries) imageCache.remove(deleteEntry);
	}
}
