package xdi2.discovery.cache.impl;

import java.io.Serializable;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.discovery.cache.DiscoveryCacheKey;
import xdi2.discovery.cache.DiscoveryCacheProvider;

public class WhirlyCacheDiscoveryCacheProvider extends DiscoveryCacheProvider {

	public static final long DEFAULT_TTL = 120 * 1000;

	private Cache registryCache;
	private Cache authorityCache;
	private long ttl;

	public WhirlyCacheDiscoveryCacheProvider(Cache registryCache, Cache authorityCache, long ttl) {

		this.registryCache = registryCache;
		this.authorityCache = authorityCache;
		this.ttl = ttl;
	}

	public WhirlyCacheDiscoveryCacheProvider(Cache registryCache, Cache authorityCache) {

		this.registryCache = registryCache;
		this.authorityCache = authorityCache;
		this.ttl = DEFAULT_TTL;
	}

	public WhirlyCacheDiscoveryCacheProvider() {

		this.registryCache = null;
		this.authorityCache = null;
		this.ttl = DEFAULT_TTL;
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

		this.registryCache.store(discoveryCacheKey, value, this.getTtl());
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

		this.authorityCache.store(discoveryCacheKey, value, this.getTtl());
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

	public long getTtl() {

		return this.ttl;
	}

	public void setTtl(long ttl) {

		this.ttl = ttl;
	}
}
