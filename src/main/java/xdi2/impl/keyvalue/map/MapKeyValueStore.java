package xdi2.impl.keyvalue.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import xdi2.impl.keyvalue.AbstractKeyValueStore;
import xdi2.impl.keyvalue.KeyValueStore;
import xdi2.impl.keyvalue.map.MapGraphFactory.SetFactory;
import xdi2.util.iterators.EmptyIterator;

/**
 * This class defines access to a map. It is used by the
 * MapGraphFactory class to create graphs stored in maps.
 * 
 * @author markus
 */
class MapKeyValueStore extends AbstractKeyValueStore implements KeyValueStore {

	private Map<String, Set<String>> map;
	private SetFactory setFactory;

	MapKeyValueStore(Map<String, Set<String>> map, SetFactory setFactory) {

		this.map = map;
		this.setFactory = setFactory;
	}

	public void put(String key, String value) {

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

	public void delete(String key, String value) {

		Set<String> set = this.map.get(key);
		if (set == null) return;

		set.remove(value);
		if (set.isEmpty()) this.map.remove(key);
	}

	public void clear() {

		this.map.clear();
	}

	public void close() {

		this.map.clear();
		this.map = null;
	}
}
