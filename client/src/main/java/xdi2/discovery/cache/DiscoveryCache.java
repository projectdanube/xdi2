package xdi2.discovery.cache;

import java.io.Serializable;

public interface DiscoveryCache {

	public Serializable getRegistry(DiscoveryCacheKey discoveryCacheKey);
	public void putRegistry(DiscoveryCacheKey discoveryCacheKey, Serializable value);

	public Serializable getAuthority(DiscoveryCacheKey discoveryCacheKey);
	public void putAuthority(DiscoveryCacheKey discoveryCacheKey, Serializable value);
}
