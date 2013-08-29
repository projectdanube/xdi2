package xdi2.core.impl.keyvalue.map;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import xdi2.core.impl.keyvalue.AbstractKeyValueStore;
import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.util.iterators.EmptyIterator;

/**
 * This class defines access to a map. It is used by the
 * MapKeyValueGraphFactory class to create graphs stored in maps.
 * 
 * @author markus
 */
public class MapKeyValueStore extends AbstractKeyValueStore implements KeyValueStore {

	private Map<String, Set<String>> map;
	private SetFactory setFactory;

	public MapKeyValueStore(Map<String, Set<String>> map, SetFactory setFactory) {

		this.map = map;
		this.setFactory = setFactory;
	}

	@Override
	public void init() throws IOException {

	}

	@Override
	public void close() {

		this.map.clear();
		this.map = null;
	}

	@Override
	public void set(String key, String value) {

		Set<String> set = this.map.get(key);

		if (set == null) {

			set = this.setFactory.newSet(key);
			this.map.put(key, set);
		}

		set.add(value);
	}

	@Override
	public String getOne(String key) {

		Set<String> set = this.map.get(key);
		if (set == null) return null;

		return set.iterator().next();
	}

	@Override
	public Iterator<String> getAll(String key) {

		Set<String> set = this.map.get(key);
		if (set == null) return new EmptyIterator<String> ();

		return set.iterator();
	}

	@Override
	public boolean contains(String key) {

		return this.map.containsKey(key);
	}

	@Override
	public boolean contains(String key, String value) {

		Set<String> set = this.map.get(key);
		if (set == null) return false;

		return set.contains(value);
	}

	@Override
	public void delete(String key) {

		this.map.remove(key);
	}

	@Override
	public void delete(String key, String value) {

		Set<String> set = this.map.get(key);
		if (set == null) return;

		set.remove(value);
		if (set.isEmpty()) this.map.remove(key);
	}

	@Override
	public void clear() {

		this.map.clear();
	}

	public Map<String, Set<String>> getMap() {

		return this.map;
	}

	public SetFactory getSetFactory() {

		return this.setFactory;
	}
}
