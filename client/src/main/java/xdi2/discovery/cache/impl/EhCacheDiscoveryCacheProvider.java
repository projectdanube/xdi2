package xdi2.discovery.cache.impl;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import xdi2.discovery.cache.DiscoveryCacheKey;
import xdi2.discovery.cache.DiscoveryCacheProvider;

public class EhCacheDiscoveryCacheProvider extends DiscoveryCacheProvider {

	private Cache registryCache;
	private Cache authorityCache;

	public EhCacheDiscoveryCacheProvider(Cache registryCache, Cache authorityCache) {

		this.registryCache = registryCache;
		this.authorityCache = authorityCache;
	}

	public EhCacheDiscoveryCacheProvider() {

		this.registryCache = null;
		this.authorityCache = null;
	}

	private synchronized void initDefault() {

		if (this.registryCache != null) return;
		if (this.authorityCache != null) return;

		CacheManager cacheManager = CacheManager.create(EhCacheDiscoveryCacheProvider.class.getResourceAsStream("ehcache.xml"));
		cacheManager.addCache(EhCacheDiscoveryCacheProvider.class.getCanonicalName() + "-default-registry-cache");
		cacheManager.addCache(EhCacheDiscoveryCacheProvider.class.getCanonicalName() + "-default-authority-cache");
		this.registryCache = cacheManager.getCache(EhCacheDiscoveryCacheProvider.class.getCanonicalName() + "-default-registry-cache");
		this.authorityCache = cacheManager.getCache(EhCacheDiscoveryCacheProvider.class.getCanonicalName() + "-default-authority-cache");
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
