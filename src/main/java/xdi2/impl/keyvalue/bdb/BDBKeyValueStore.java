package xdi2.impl.keyvalue.bdb;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xdi2.impl.keyvalue.AbstractKeyValueStore;
import xdi2.impl.keyvalue.KeyValueStore;
import xdi2.util.iterators.IteratorListMaker;
import xdi2.util.iterators.ReadOnlyIterator;

import com.sleepycat.collections.CurrentTransaction;
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

	private final static Log log = LogFactory.getLog(BDBKeyValueStore.class);

	private String databasePath;
	private String databaseName;
	private EnvironmentConfig environmentConfig;
	private DatabaseConfig databaseConfig;

	private Environment environment;
	private Database database;

	private List<Cursor> cursors = new ArrayList<Cursor> ();

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

		log.debug("Closing database (" + this.cursors.size() + " open cursors)...");

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

		log.debug("Beginning Transaction (" + this.cursors.size() + " open cursors)...");

		try {

			CurrentTransaction.getInstance(this.environment).beginTransaction(null);
		} catch (Exception ex) {

			throw new RuntimeException("Cannot begin transaction: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void commitTransaction() {

		log.debug("Committing database (" + this.cursors.size() + " open cursors)...");

		try {

			CurrentTransaction.getInstance(this.environment).commitTransaction();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot commit transaction: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void rollbackTransaction() {

		log.debug("Rolling back database (" + this.cursors.size() + " open cursors)...");

		try {

			CurrentTransaction.getInstance(this.environment).abortTransaction();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot roll back transaction: " + ex.getMessage(), ex);
		}
	}

	public void put(String key, String value) {

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry(value.getBytes());

		try {

			Transaction transaction = CurrentTransaction.getInstance(this.environment).getTransaction();
			OperationStatus status = this.database.put(transaction, dbKey, dbValue);
			if (! status.equals(OperationStatus.SUCCESS)) throw new RuntimeException();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot write to database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public String getOne(String key) {

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		try {

			Transaction transaction = CurrentTransaction.getInstance(this.environment).getTransaction();
			OperationStatus status = this.database.get(transaction, dbKey, dbValue, null);
			if (! status.equals(OperationStatus.SUCCESS)) return(null);
			return(new String(dbValue.getData()));
		} catch (DatabaseException ex) {

			throw new RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	public Iterator<String> getAll(String key) {

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		try {

			IteratorListMaker<String> i = new IteratorListMaker<String> (new CursorDuplicatesIterator(dbKey, dbValue));
			return i.list().iterator();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public boolean contains(String key) {

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		try {

			Transaction transaction = CurrentTransaction.getInstance(this.environment).getTransaction();
			OperationStatus status = this.database.get(transaction, dbKey, dbValue, null);
			if ((! status.equals(OperationStatus.SUCCESS)) && (! status.equals(OperationStatus.NOTFOUND))) throw new RuntimeException();
			return(status.equals(OperationStatus.SUCCESS));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public boolean contains(String key, String value) {

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry(value.getBytes());

		try {

			Transaction transaction = CurrentTransaction.getInstance(this.environment).getTransaction();
			OperationStatus status = this.database.getSearchBoth(transaction, dbKey, dbValue, null);
			if ((! status.equals(OperationStatus.SUCCESS)) && (! status.equals(OperationStatus.NOTFOUND))) throw new RuntimeException();
			return(status.equals(OperationStatus.SUCCESS));
		} catch (Exception ex) {

			throw new RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void delete(String key) {

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());

		try {

			Transaction transaction = CurrentTransaction.getInstance(this.environment).getTransaction();
			this.database.delete(transaction, dbKey);
		} catch (Exception ex) {

			throw new RuntimeException("Cannot delete from database: " + ex.getMessage(), ex);
		}
	}

	public void delete(String key, String value) {

		DatabaseEntry dbKey = new DatabaseEntry(key.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry(value.getBytes());

		Cursor cursor = null;

		try {

			Transaction transaction = CurrentTransaction.getInstance(this.environment).getTransaction();
			cursor = this.database.openCursor(transaction, null);
			this.cursors.add(cursor);
			cursor.getSearchBoth(dbKey, dbValue, null);

			OperationStatus status = cursor.delete();
			if (! status.equals(OperationStatus.SUCCESS)) throw new RuntimeException();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot delete from database: " + ex.getMessage(), ex);
		} finally {

			if (cursor != null) cursor.close();
			this.cursors.remove(cursor);
		}
	}

	public void clear() {

		boolean hadTransaction = CurrentTransaction.getInstance(this.environment).getTransaction() != null;

		if (hadTransaction) this.commitTransaction();
		this.closeDatabase();

		try {

			EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setLocking(true);
			environmentConfig.setTransactional(true);

			Environment environment = new Environment(new File(this.databasePath), environmentConfig);
			CurrentTransaction.getInstance(environment).beginTransaction(null);
			Transaction transaction = CurrentTransaction.getInstance(environment).getTransaction();
			environment.truncateDatabase(transaction, this.databaseName, false);
			CurrentTransaction.getInstance(environment).commitTransaction();
			environment.close();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot delete and re-create dabatase: " + ex.getMessage(), ex);
		} finally {

			this.openDatabase();
			if (hadTransaction) this.beginTransaction();
		}
	}

	public void close() {

		this.closeDatabase();
	}

	public void dump(PrintStream stream) {

		DatabaseEntry dbKey = new DatabaseEntry();
		DatabaseEntry dbValue = new DatabaseEntry();

		Cursor cursor = null;

		this.beginTransaction();

		try {

			Transaction transaction = CurrentTransaction.getInstance(this.environment).getTransaction();
			cursor = this.database.openCursor(transaction, null);
			this.cursors.add(cursor);
			OperationStatus status = cursor.getFirst(dbKey, dbValue, null);

			while (status.equals(OperationStatus.SUCCESS)) {

				stream.println(new String(dbKey.getData()) + " --> " + new String(dbValue.getData()));

				status = cursor.getNext(dbKey, dbValue, null);
			}
		} catch (DatabaseException ex) {

			throw new RuntimeException("Cannot dump database.", ex);
		} finally {

			if (cursor != null) cursor.close();
			this.cursors.remove(cursor);
		}

		this.commitTransaction();
	}

	public void dump() {

		this.dump(System.out);
	}

	public Environment getEnvironment() {

		return(this.environment);
	}

	public Database getDatabase() {

		return(this.database);
	}

	private class CursorDuplicatesIterator extends ReadOnlyIterator<String> {

		private DatabaseEntry dbKey;
		private DatabaseEntry dbValue;
		private Cursor cursor;
		private OperationStatus status;

		private CursorDuplicatesIterator(DatabaseEntry dbKey, DatabaseEntry dbValue) {

			this.dbKey = dbKey;
			this.dbValue = dbValue;

			this.cursor = null;

			try {

				Transaction transaction = CurrentTransaction.getInstance(BDBKeyValueStore.this.environment).getTransaction();
				this.cursor = BDBKeyValueStore.this.database.openCursor(transaction, null);
				BDBKeyValueStore.this.cursors.add(this.cursor);
				this.status = this.cursor.getSearchKey(this.dbKey, this.dbValue, null);
				if (! this.status.equals(OperationStatus.SUCCESS)) this.cursor.close();
			} catch (DatabaseException ex) {

				throw new RuntimeException("Cannot read from this.database.", ex);
			}
		}

		public boolean hasNext() {

			return(this.status.equals(OperationStatus.SUCCESS));
		}

		public String next() {

			String element = new String(this.dbValue.getData());

			try {

				if (! this.status.equals(OperationStatus.SUCCESS)) return null;
				this.status = this.cursor.getNextDup(this.dbKey, this.dbValue, null);

				if (! this.status.equals(OperationStatus.SUCCESS)) {

					this.cursor.close();
					BDBKeyValueStore.this.cursors.remove(this.cursor);
				}
			} catch (DatabaseException ex) {

				throw new RuntimeException("Cannot read from database.", ex);
			}

			return(element);
		}
	}
}
