package xdi2.impl.keyvalue.bdb;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.exceptions.Xdi2RuntimeException;
import xdi2.impl.keyvalue.AbstractKeyValueStore;
import xdi2.impl.keyvalue.KeyValueStore;
import xdi2.util.iterators.IteratorListMaker;
import xdi2.util.iterators.ReadOnlyIterator;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * This class defines access to a BDB based datastore. It is used by the
 * BDBGraphFactory class to create graphs stored in BDB.
 * 
 * @author markus
 */
class BDBKeyValueStore extends AbstractKeyValueStore implements KeyValueStore {

	private final static Logger log = LoggerFactory.getLogger(BDBKeyValueStore.class);

	private String databasePath;
	private String databaseName;
	private EnvironmentConfig environmentConfig;
	private DatabaseConfig databaseConfig;

	private Environment environment;
	private Database database;
	private Transaction transaction;

	BDBKeyValueStore(String databasePath, String databaseName, EnvironmentConfig environmentConfig, DatabaseConfig databaseConfig) {

		this.databasePath = databasePath;
		this.databaseName = databaseName;
		this.environmentConfig = environmentConfig;
		this.databaseConfig = databaseConfig;
	}

	void openDatabase() throws DatabaseException {

		log.debug("Opening database...");

		this.environment = new Environment(new File(this.databasePath), this.environmentConfig);
		this.database = this.environment.openDatabase(null, this.databaseName, this.databaseConfig);
	}

	void closeDatabase() {

		log.debug("Closing database...");

		try {

			this.database.close();
			this.environment.close();
		} catch (DatabaseException ex) {

			log.error("Cannot close database: " + ex.getMessage(), ex);
		} finally {

			this.database = null;
			this.environment = null;
		}
	}

	@Override
	public void beginTransaction() {

		log.trace("beginTransaction()");

		if (this.transaction != null) throw new Xdi2RuntimeException("Already have an open transaction.");
		
		log.debug("Beginning Transaction...");

		try {

			this.transaction = this.environment.beginTransaction(null, null);
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot begin transaction: " + ex.getMessage(), ex);
		}

		log.debug("Began transaction...");
	}

	@Override
	public void commitTransaction() {

		log.trace("commitTransaction()");

		if (this.transaction == null) throw new Xdi2RuntimeException("No open transaction.");

		try {

			this.transaction.commit();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot commit transaction: " + ex.getMessage(), ex);
		}

		log.debug("Committed transaction...");
	}

	@Override
	public void rollbackTransaction() {

		log.trace("rollbackTransaction()");

		if (this.transaction == null) throw new Xdi2RuntimeException("No open transaction.");

		log.debug("Rolling back transaction...");

		try {

			this.transaction.abort();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot roll back transaction: " + ex.getMessage(), ex);
		}

		log.debug("Rolled back transaction...");
	}

	public void put(String key, String value) {

		log.trace("put(" + key + "," + value + ")");
		
		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry(value.getBytes());

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			OperationStatus status = this.database.put(transaction, dbKey, dbValue);
			if (! status.equals(OperationStatus.SUCCESS)) throw new Xdi2RuntimeException("Unsuccessful");

			if (this.transaction == null) transaction.commit();
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot write to database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public String getOne(String key) {

		log.trace("getOne(" + key + ")");

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			OperationStatus status = this.database.get(transaction, dbKey, dbValue, null);
			if (! status.equals(OperationStatus.SUCCESS)) return(null);
			String value = new String(dbValue.getData());

			if (this.transaction == null) transaction.commit();
			return value;
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	public Iterator<String> getAll(String key) {

		log.trace("getAll(" + key + ")");

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			IteratorListMaker<String> i = new IteratorListMaker<String> (new CursorDuplicatesIterator(transaction, dbKey, dbValue));
			List<String> list = i.list();

			if (this.transaction == null) transaction.commit();
			return list.iterator();
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public boolean contains(String key) {

		log.trace("contains(" + key + ")");

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			OperationStatus status = this.database.get(transaction, dbKey, dbValue, null);
			if ((! status.equals(OperationStatus.SUCCESS)) && (! status.equals(OperationStatus.NOTFOUND))) throw new Xdi2RuntimeException();

			if (this.transaction == null) transaction.commit();
			return status.equals(OperationStatus.SUCCESS);
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public boolean contains(String key, String value) {

		log.trace("contains(" + key + "," + value + ")");

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry(value.getBytes());

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			OperationStatus status = this.database.getSearchBoth(transaction, dbKey, dbValue, null);
			if ((! status.equals(OperationStatus.SUCCESS)) && (! status.equals(OperationStatus.NOTFOUND))) throw new Xdi2RuntimeException();

			if (this.transaction == null) transaction.commit();
			return status.equals(OperationStatus.SUCCESS);
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void delete(String key) {

		log.trace("delete(" + key + ")");

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			this.database.delete(transaction, dbKey);

			if (this.transaction == null) transaction.commit();
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot delete from database: " + ex.getMessage(), ex);
		}
	}

	public void delete(String key, String value) {

		log.trace("delete(" + key + "," + value + ")");

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry(value.getBytes());

		Cursor cursor = null;

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			cursor = this.database.openCursor(transaction, null);
			cursor.getSearchBoth(dbKey, dbValue, null);

			OperationStatus status = cursor.delete();
			if (! status.equals(OperationStatus.SUCCESS)) throw new Xdi2RuntimeException();

			cursor.close();
			
			if (this.transaction == null) transaction.commit();
		} catch (Exception ex) {

			if (cursor != null) cursor.close();

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot delete from database: " + ex.getMessage(), ex);
		}
	}

	public void clear() {

		log.trace("clear()");

		if (this.transaction != null) throw new Xdi2RuntimeException("Cannot clear store with an open transaction.");

		this.closeDatabase();

		try {

			EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setLocking(true);
			environmentConfig.setTransactional(true);

			Environment environment = new Environment(new File(this.databasePath), environmentConfig);
			Transaction transaction = environment.beginTransaction(null, null);
			environment.truncateDatabase(transaction, this.databaseName, false);
			transaction.commit();
			environment.close();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot delete and re-create dabatase: " + ex.getMessage(), ex);
		} finally {

			this.openDatabase();
		}
	}

	public void close() {

		log.trace("close()");

		this.closeDatabase();
	}

	private class CursorDuplicatesIterator extends ReadOnlyIterator<String> {

		private DatabaseEntry dbKey;
		private DatabaseEntry dbValue;
		private Cursor cursor;
		private OperationStatus status;

		private CursorDuplicatesIterator(Transaction transaction, DatabaseEntry dbKey, DatabaseEntry dbValue) {

			this.dbKey = dbKey;
			this.dbValue = dbValue;

			this.cursor = null;

			try {

				this.cursor = BDBKeyValueStore.this.database.openCursor(transaction, null);
				this.status = this.cursor.getSearchKey(this.dbKey, this.dbValue, null);
				if (! this.status.equals(OperationStatus.SUCCESS)) this.cursor.close();
			} catch (DatabaseException ex) {

				throw new Xdi2RuntimeException("Cannot read from this.database.", ex);
			}
		}

		public boolean hasNext() {

			return(this.status.equals(OperationStatus.SUCCESS));
		}

		public String next() {

			log.trace("CursorDuplicatesIterator.next()");

			String element = new String(this.dbValue.getData());

			try {

				if (! this.status.equals(OperationStatus.SUCCESS)) return null;
				this.status = this.cursor.getNextDup(this.dbKey, this.dbValue, null);

				if (! this.status.equals(OperationStatus.SUCCESS)) {

					this.cursor.close();
				}
			} catch (DatabaseException ex) {

				throw new Xdi2RuntimeException("Cannot read from database.", ex);
			}

			return element;
		}
	}
}
