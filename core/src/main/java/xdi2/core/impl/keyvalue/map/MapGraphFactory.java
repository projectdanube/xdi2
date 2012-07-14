package xdi2.core.impl.keyvalue.map;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import xdi2.core.GraphFactory;
import xdi2.core.impl.keyvalue.AbstractKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.KeyValueStore;

/**
 * GraphFactory that creates map graphs.
 * 
 * @author markus
 */
public final class MapGraphFactory extends AbstractKeyValueGraphFactory implements GraphFactory {

	public static final boolean DEFAULT_SUPPORT_GET_CONTEXTNODES = true; 
	public static final boolean DEFAULT_SUPPORT_GET_RELATIONS = true; 

	private static final MapFactory DEFAULT_MAP_FACTORY = new DefaultMapFactory();
	private static final SetFactory DEFAULT_SET_FACTORY = new DefaultSetFactory();
	
	private MapFactory mapFactory;
	private SetFactory setFactory;

	public MapGraphFactory() {

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS);

		this.mapFactory = DEFAULT_MAP_FACTORY;
		this.setFactory = DEFAULT_SET_FACTORY;
	}

	@Override
	protected KeyValueStore openKeyValueStore() throws IOException {

		// open map

		Map<String, Set<String>> map = this.mapFactory.newMap();

		KeyValueStore keyValueStore = new MapKeyValueStore(map, this.setFactory);

		// done

		return keyValueStore;
	}

	public MapFactory getMapFactory() {

		return this.mapFactory;
	}

	public void setMapFactory(MapFactory mapFactory) {

		this.mapFactory = mapFactory;
	}

	public SetFactory getSetFactory() {

		return this.setFactory;
	}

	public void setSetFactory(SetFactory setFactory) {

		this.setFactory = setFactory;
	}
}
