package xdi2.discovery.cache.impl;

import java.io.Serializable;

import xdi2.discovery.cache.DiscoveryCacheKey;
import xdi2.discovery.cache.DiscoveryCacheProvider;

public class NullDiscoveryCacheProvider extends DiscoveryCacheProvider {

	public NullDiscoveryCacheProvider() {

	}

	@Override
	public Serializable getRegistry(DiscoveryCacheKey discoveryCacheKey) {

		return null;
	}

	@Override
	public void putRegistry(DiscoveryCacheKey discoveryCacheKey, Serializable value) {
	}

	@Override
	public Serializable getAuthority(DiscoveryCacheKey discoveryCacheKey) {

		return null;
	}

	@Override
	public void putAuthority(DiscoveryCacheKey discoveryCacheKey, Serializable value) {

	}
}
