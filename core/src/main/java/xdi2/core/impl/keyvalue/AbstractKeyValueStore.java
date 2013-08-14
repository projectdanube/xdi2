package xdi2.core.impl.keyvalue;

import java.util.Iterator;

import xdi2.core.util.iterators.IteratorFirstItem;
import xdi2.core.util.iterators.IteratorCounter;

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

	@Override
	public String getOne(String key) {

		return new IteratorFirstItem<String> (this.getAll(key)).item();
	}

	@Override
	public boolean contains(String key) {

		return this.getAll(key).hasNext();
	}

	@Override
	public boolean contains(String key, String value) {

		for (Iterator<String> values = this.getAll(key); values.hasNext(); ) {

			String value2 = values.next();

			if (value.equals(value2)) return true;
		}

		return false;
	}

	@Override
	public void delete(String key) {

		for (Iterator<String> values = this.getAll(key); values.hasNext(); ) {

			String value = values.next();

			this.delete(key, value);
		}
	}

	@Override
	public void replace(String key, String value) {

		this.delete(key);
		if (value != null) this.set(key, value);
	}

	@Override
	public long count(String key) {

		return new IteratorCounter(this.getAll(key)).count();
	}

	@Override
	public boolean supportsTransactions() {

		return false;
	}

	@Override
	public void beginTransaction() {

	}

	@Override
	public void commitTransaction() {

	}

	@Override
	public void rollbackTransaction() {

	}
}
