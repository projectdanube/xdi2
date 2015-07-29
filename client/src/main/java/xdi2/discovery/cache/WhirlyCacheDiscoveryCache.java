package xdi2.discovery.cache;

import java.io.Serializable;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

import xdi2.core.exceptions.Xdi2RuntimeException;

public class WhirlyCacheDiscoveryCache implements DiscoveryCache {

	private Cache registryCache;
	private Cache authorityCache;

	public WhirlyCacheDiscoveryCache(Cache registryCache, Cache authorityCache) {

		this.registryCache = registryCache;
		this.authorityCache = authorityCache;
	}

	public WhirlyCacheDiscoveryCache() {

		this.registryCache = null;
		this.authorityCache = null;
	}

	private void initDefault() {

		try {

			this.registryCache = CacheManager.getInstance().getCache("registry-cache");
			this.authorityCache = CacheManager.getInstance().getCache("authority-cache");
		} catch (CacheException ex) {

			throw new Xdi2RuntimeException("Cannot initialize cache: " + ex.getMessage(), ex);
		}
	}

	@Override
	public Serializable getRegistry(DiscoveryCacheKey discoveryCacheKey) {

		if (this.registryCache == null) this.initDefault();

		Object value = this.registryCache.retrieve(discoveryCacheKey);
		if (value == null) return null;

		return (Serializable) value;
	}

	@Override
	public void putRegistry(DiscoveryCacheKey discoveryCacheKey, Serializable value) {

		if (this.registryCache == null) this.initDefault();

		this.registryCache.store(discoveryCacheKey, value);
	}

	@Override
	public Serializable getAuthority(DiscoveryCacheKey discoveryCacheKey) {

		if (this.authorityCache == null) this.initDefault();

		Object value = this.authorityCache.retrieve(discoveryCacheKey);
		if (value == null) return null;

		return (Serializable) value;
	}

	@Override
	public void putAuthority(DiscoveryCacheKey discoveryCacheKey, Serializable value) {

		if (this.authorityCache == null) this.initDefault();

		this.authorityCache.store(discoveryCacheKey, value);
	}

	/*
	 * Getters and setters
	 */

	public Cache getRegistryCache() {

		return this.registryCache;
	}

	public void setRegistryCache(Cache registryCache) {

		this.registryCache = registryCache;
	}

	public Cache getAuthorityCache() {

		return this.authorityCache;
	}

	public void setAuthorityCache(Cache authorityCache) {

		this.authorityCache = authorityCache;
	}
}
