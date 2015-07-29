package xdi2.discovery.cache;

import java.io.Serializable;

public class NullDiscoveryCache implements DiscoveryCache {

	public NullDiscoveryCache() {

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
