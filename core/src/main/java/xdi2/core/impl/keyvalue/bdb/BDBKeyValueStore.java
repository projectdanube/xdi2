package xdi2.core.impl.keyvalue.bdb;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.keyvalue.AbstractKeyValueStore;
import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.ReadOnlyIterator;

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
 * BDBKeyValueGraphFactory class to create graphs stored in BDB.
 * 
 * @author markus
 */
public class BDBKeyValueStore extends AbstractKeyValueStore implements KeyValueStore {

	private static final Logger log = LoggerFactory.getLogger(BDBKeyValueStore.class);

	private String databasePath;
	private String databaseName;
	private EnvironmentConfig environmentConfig;
	private DatabaseConfig databaseConfig;

	private Environment environment;
	private Database database;
	private boolean databaseOpenedInTransaction;

	private Transaction transaction;

	public BDBKeyValueStore(String databasePath, String databaseName, EnvironmentConfig environmentConfig, DatabaseConfig databaseConfig) {

		this.databasePath = databasePath;
		this.databaseName = databaseName;
		this.environmentConfig = environmentConfig;
		this.databaseConfig = databaseConfig;
	}

	@Override
	public void init() throws IOException {

		if (log.isDebugEnabled()) log.debug("Opening environment and database...");

		this.environment = new Environment(new File(this.databasePath), this.environmentConfig);
		this.database = this.environment.openDatabase(null, this.databaseName, this.databaseConfig);
		this.databaseOpenedInTransaction = false;
	}

	@Override
	public void close() {

		if (log.isDebugEnabled()) log.debug("Closing environment and database...");

		try {

			this.database.close();
			this.environment.close();
		} catch (DatabaseException ex) {

			log.error("Cannot close environment and database: " + ex.getMessage(), ex);
		} finally {

			this.database = null;
			this.environment = null;
		}
	}

	@Override
	public void set(String key, String value) {

		if (log.isTraceEnabled()) log.trace("set(" + key + "," + value + ")");

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

		if (log.isTraceEnabled()) log.trace("getOne(" + key + ")");

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			OperationStatus status = this.database.get(transaction, dbKey, dbValue, null);
			if ((! status.equals(OperationStatus.SUCCESS)) && (! status.equals(OperationStatus.NOTFOUND))) throw new Xdi2RuntimeException();

			if (this.transaction == null) transaction.commit();
			return status.equals(OperationStatus.SUCCESS) ? new String(dbValue.getData()) : null;
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public Iterator<String> getAll(String key) {

		if (log.isTraceEnabled()) log.trace("getAll(" + key + ")");

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

		if (log.isTraceEnabled()) log.trace("contains(" + key + ")");

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

		if (log.isTraceEnabled()) log.trace("contains(" + key + "," + value + ")");

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

		if (log.isTraceEnabled()) log.trace("delete(" + key + ")");

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

	@Override
	public void delete(String key, String value) {

		log.info("delete(" + key + "," + value + ")");

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry(value.getBytes());

		Cursor cursor = null;

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			OperationStatus status;

			cursor = this.database.openCursor(transaction, null);

			status = cursor.getSearchBoth(dbKey, dbValue, null);
			if (status.equals(OperationStatus.NOTFOUND)) {

				cursor.close();
				if (this.transaction == null) transaction.commit();
				return;
			}
			if (! status.equals(OperationStatus.SUCCESS)) throw new Xdi2RuntimeException();

			status = cursor.delete();
			if (! status.equals(OperationStatus.SUCCESS)) throw new Xdi2RuntimeException();

			cursor.close();

			if (this.transaction == null) transaction.commit();
		} catch (Exception ex) {

			if (cursor != null) cursor.close();

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot delete from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void clear() {

		if (log.isTraceEnabled()) log.trace("clear()");

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			this.database.close();

			this.environment.truncateDatabase(transaction, this.databaseName, false);

			if (this.transaction == null) transaction.commit();
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot truncate dabatase: " + ex.getMessage(), ex);
		} finally {

			this.database = this.environment.openDatabase(this.transaction, this.databaseName, this.databaseConfig);
			this.databaseOpenedInTransaction = (this.transaction != null);
		}
	}

	@Override
	public boolean supportsTransactions() {

		return true;
	}

	@Override
	public void beginTransaction() {

		if (log.isTraceEnabled()) log.trace("beginTransaction()");

		if (this.transaction != null) throw new Xdi2RuntimeException("Already have an open transaction.");

		if (log.isDebugEnabled()) log.debug("Beginning Transaction...");

		try {

			this.transaction = this.environment.beginTransaction(null, null);
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot begin transaction: " + ex.getMessage(), ex);
		}

		if (log.isDebugEnabled()) log.debug("Began transaction...");
	}

	@Override
	public void commitTransaction() {

		if (log.isTraceEnabled()) log.trace("commitTransaction()");

		if (this.transaction == null) throw new Xdi2RuntimeException("No open transaction.");

		try {

			this.transaction.commit();
			this.transaction = null;
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot commit transaction: " + ex.getMessage(), ex);
		}

		if (log.isDebugEnabled()) log.debug("Committed transaction...");
	}

	@Override
	public void rollbackTransaction() {

		if (log.isTraceEnabled()) log.trace("rollbackTransaction()");

		if (this.transaction == null) throw new Xdi2RuntimeException("No open transaction.");

		if (log.isDebugEnabled()) log.debug("Rolling back transaction...");

		try {

			this.transaction.abort();
			this.transaction = null;
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot roll back transaction: " + ex.getMessage(), ex);
		} finally {

			if (this.databaseOpenedInTransaction) {

				this.database.close();
				this.database = this.environment.openDatabase(null, this.databaseName, this.databaseConfig);
			}
		}

		if (log.isDebugEnabled()) log.debug("Rolled back transaction...");
	}

	public String getDatabasePath() {

		return this.databasePath;
	}

	public String getDatabaseName() {

		return this.databaseName;
	}

	/*
	 * Helper methods
	 */

	public static void cleanup(String databasePath) {

		File path = new File(databasePath);

		if (path.exists()) {

			for (File file : path.listFiles()) {

				file.delete();
			}
		}
	}

	/*
	 * Helper classes
	 */

	private class CursorDuplicatesIterator extends ReadOnlyIterator<String> {

		private DatabaseEntry dbKey;
		private DatabaseEntry dbValue;
		private Cursor cursor;
		private OperationStatus status;

		private CursorDuplicatesIterator(Transaction transaction, DatabaseEntry dbKey, DatabaseEntry dbValue) {

			super(null);

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

		@Override
		public boolean hasNext() {

			return(this.status.equals(OperationStatus.SUCCESS));
		}

		@Override
		public String next() {

			if (log.isTraceEnabled()) log.trace("CursorDuplicatesIterator.next()");

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
