package xdi2.webtools.grapher;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class ImageCache {

	private static CacheManager cacheManager;
	private static Cache imageCache;

	public static void init() {

		cacheManager = CacheManager.create(ImageCache.class.getResourceAsStream("ehcache.xml"));

		imageCache = cacheManager.getCache("images");
		if (imageCache == null) throw new RuntimeException("Can not find cache.");
	}

	public static void shutdown() {

		imageCache.flush();
		cacheManager.shutdown();
	}

	public static void put(String imageId, byte[] data) {

		imageCache.put(new Element(imageId, data));
	}

	public static byte[] get(String imageId) {

		Element element = imageCache.get(imageId);
		if (element == null) return null;
		return (byte[]) element.getValue();
	}
}
