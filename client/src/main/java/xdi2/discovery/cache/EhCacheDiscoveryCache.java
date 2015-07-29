package xdi2.discovery.cache;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhCacheDiscoveryCache implements DiscoveryCache {

	private Cache registryCache;
	private Cache authorityCache;

	public EhCacheDiscoveryCache(Cache registryCache, Cache authorityCache) {

		this.registryCache = registryCache;
		this.authorityCache = authorityCache;
	}

	public EhCacheDiscoveryCache() {

		this.registryCache = null;
		this.authorityCache = null;
	}

	private void initDefault() {

		CacheManager cacheManager = CacheManager.create(EhCacheDiscoveryCache.class.getResourceAsStream("ehcache.xml"));
		cacheManager.addCache(EhCacheDiscoveryCache.class.getCanonicalName() + "-default-registry-cache");
		cacheManager.addCache(EhCacheDiscoveryCache.class.getCanonicalName() + "-default-authority-cache");
		this.registryCache = cacheManager.getCache(EhCacheDiscoveryCache.class.getCanonicalName() + "-default-registry-cache");
		this.authorityCache = cacheManager.getCache(EhCacheDiscoveryCache.class.getCanonicalName() + "-default-authority-cache");
	}

	@Override
	public Serializable getRegistry(DiscoveryCacheKey discoveryCacheKey) {

		if (this.registryCache == null) this.initDefault();

		Element element = this.registryCache.get(discoveryCacheKey);
		if (element == null) return null;

		return (Serializable) element.getObjectValue();
	}

	@Override
	public void putRegistry(DiscoveryCacheKey discoveryCacheKey, Serializable value) {

		if (this.registryCache == null) this.initDefault();

		this.registryCache.put(new Element(discoveryCacheKey, value));
	}

	@Override
	public Serializable getAuthority(DiscoveryCacheKey discoveryCacheKey) {

		if (this.authorityCache == null) this.initDefault();

		Element element = this.authorityCache.get(discoveryCacheKey);
		if (element == null) return null;

		return (Serializable) element.getObjectValue();
	}

	@Override
	public void putAuthority(DiscoveryCacheKey discoveryCacheKey, Serializable value) {

		if (this.authorityCache == null) this.initDefault();

		this.authorityCache.put(new Element(discoveryCacheKey, value));
	}
}
