package xdi2.discovery.cache;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ServiceLoader;

public abstract class DiscoveryCacheProvider {

	private static DiscoveryCacheProvider instance;

	public static DiscoveryCacheProvider get() {

		DiscoveryCacheProvider result = instance;

		if (result == null) {

			synchronized(DiscoveryCacheProvider.class) {

				result = instance;

				if (result == null) {

					ServiceLoader<DiscoveryCacheProvider> serviceLoader = ServiceLoader.load(DiscoveryCacheProvider.class);
					Iterator<DiscoveryCacheProvider> iterator = serviceLoader.iterator();
					if (! iterator.hasNext()) throw new RuntimeException("No " + DiscoveryCacheProvider.class.getName() + " registered");

					instance = result = iterator.next();
				}
			}
		}

		return result;
	}

	public static void set(DiscoveryCacheProvider instance) {

		DiscoveryCacheProvider.instance = instance;
	}

	public abstract Serializable getRegistry(DiscoveryCacheKey discoveryCacheKey);
	public abstract void putRegistry(DiscoveryCacheKey discoveryCacheKey, Serializable value);

	public abstract Serializable getAuthority(DiscoveryCacheKey discoveryCacheKey);
	public abstract void putAuthority(DiscoveryCacheKey discoveryCacheKey, Serializable value);
}
