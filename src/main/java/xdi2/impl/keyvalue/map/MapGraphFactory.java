package xdi2.impl.keyvalue.map;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xdi2.GraphFactory;
import xdi2.impl.keyvalue.AbstractKeyValueGraphFactory;
import xdi2.impl.keyvalue.KeyValueStore;

/**
 * GraphFactory that creates map graphs.
 * 
 * @author markus
 */
public final class MapGraphFactory extends AbstractKeyValueGraphFactory implements GraphFactory {

	public static final boolean DEFAULT_SUPPORT_GET_CONTEXTNODES = true; 
	public static final boolean DEFAULT_SUPPORT_GET_RELATIONS = true; 

	public static final MapFactory DEFAULT_MAP_FACTORY = new MapFactory() {

		@Override
		public Map<String, Set<String>> newMap() {
			
			return new HashMap<String, Set<String>> ();
		}
		
	};
	public static final SetFactory DEFAULT_SET_FACTORY = new SetFactory() {

		@Override
		public Set<String> newSet(String key) {

			return new HashSet<String> ();
		}
		
	};

	private MapFactory mapFactory;
	private SetFactory setFactory;

	public MapGraphFactory() {

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS);

		this.mapFactory = DEFAULT_MAP_FACTORY;
		this.setFactory = DEFAULT_SET_FACTORY;
	}

	protected KeyValueStore getKeyValueStore() throws IOException {

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

	public interface MapFactory {

		public Map<String, Set<String> > newMap();
	}

	public interface SetFactory {

		public Set<String> newSet(String key);
	}
}
