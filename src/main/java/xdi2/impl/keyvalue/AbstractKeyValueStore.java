package xdi2.impl.keyvalue;

import java.util.Iterator;

import xdi2.util.iterators.FirstIteratorItem;
import xdi2.util.iterators.IteratorCounter;

/**
 * This abstract base class relieves subclasses from certain tasks 
 * by implementing them using other more basic methods.
 * 
 * The basic methods that still have to be implemented are:
 * - put(key, value)
 * - getAll(key, value)
 * - delete(key, value)
 * 
 * Subclasses are still encouraged to overwrite as many methods as
 * possible for better performance.
 * 
 * @author markus
 */
public abstract class AbstractKeyValueStore implements KeyValueStore {

	public String getOne(String key) {

		return new FirstIteratorItem<String> (this.getAll(key)).item();
	}

	public boolean contains(String key) {

		return this.getAll(key).hasNext();
	}

	public boolean contains(String key, String value) {

		for (Iterator<String> values = this.getAll(key); values.hasNext(); ) {

			String value2 = values.next();

			if (value.equals(value2)) return true;
		}

		return false;
	}

	public void delete(String key) {

		for (Iterator<String> values = this.getAll(key); values.hasNext(); ) {

			String value = values.next();

			this.delete(key, value);
		}
	}

	public void replace(String key, String value) {

		this.delete(key);
		if (value != null) this.put(key, value);
	}

	public int count(String key) {

		return new IteratorCounter(this.getAll(key)).count();
	}

	public void beginTransaction() {

	}

	public void commitTransaction() {

	}

	public void rollbackTransaction() {

	}
}
