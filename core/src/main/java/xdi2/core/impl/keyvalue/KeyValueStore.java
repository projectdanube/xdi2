package xdi2.core.impl.keyvalue;

import java.io.IOException;
import java.util.Iterator;

/**
 * The key/value based graph implementation needs a KeyValueStore in order
 * to work. This defines basic operations on a key/value pair based datastore.
 * 
 * @author markus
 */
public interface KeyValueStore {

	public void init() throws IOException;
	public void close();

	public void set(String key, String value);
	public String getOne(String key);
	public Iterator<String> getAll(String key);
	public boolean contains(String key);
	public boolean contains(String key, String value);
	public void delete(String key);
	public void delete(String key, String value);
	public void replace(String key, String value);
	public long count(String key);
	public void clear();

	public boolean supportsTransactions();
	public void beginTransaction();
	public void commitTransaction();
	public void rollbackTransaction();
}
