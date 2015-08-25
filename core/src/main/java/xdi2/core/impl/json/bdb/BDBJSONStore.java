package xdi2.core.impl.json.bdb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.json.AbstractJSONStore;
import xdi2.core.impl.json.JSONStore;

/**
 * This class is used by the BDBJSONGraphFactory class
 * to create JSON based graphs stored in BDB.
 * 
 * @author markus
 */
public class BDBJSONStore extends AbstractJSONStore implements JSONStore {

	private static final Logger log = LoggerFactory.getLogger(BDBJSONStore.class);

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	private String databasePath;
	private String databaseName;
	private EnvironmentConfig environmentConfig;
	private DatabaseConfig databaseConfig;

	private Environment environment;
	private Database database;
	private boolean databaseOpenedInTransaction;

	private Transaction transaction;

	public BDBJSONStore(String databasePath, String databaseName, EnvironmentConfig environmentConfig, DatabaseConfig databaseConfig) {

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
	public JsonObject load(String id) throws IOException {

		if (log.isTraceEnabled()) log.trace("load(" + id + ")");

		DatabaseEntry dbKey = new DatabaseEntry(id.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			OperationStatus status = this.database.get(transaction, dbKey, dbValue, null);
			if ((! status.equals(OperationStatus.SUCCESS)) && (! status.equals(OperationStatus.NOTFOUND))) throw new Xdi2RuntimeException();

			if (this.transaction == null) transaction.commit();
			if (! status.equals(OperationStatus.SUCCESS)) return null;

			JsonObject jsonObject = gson.getAdapter(JsonObject.class).fromJson(new InputStreamReader(new ByteArrayInputStream(dbValue.getData())));

			return jsonObject;
		} catch (Exception ex) {

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot read from database: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void save(String id, JsonObject jsonObject) throws IOException {

		if (log.isTraceEnabled()) log.trace("save(" + id + "," + jsonObject + ")");

		DatabaseEntry dbKey = new DatabaseEntry(id.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry(gson.toJson(jsonObject).getBytes());

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
	public void delete(String id) throws IOException {

		if (log.isTraceEnabled()) log.trace("delete(" + id + ")");

		DatabaseEntry dbKey = new DatabaseEntry(id.getBytes());
		DatabaseEntry dbValue = new DatabaseEntry();

		Cursor cursor = null;

		Transaction transaction = this.transaction;
		if (transaction == null) transaction = this.environment.beginTransaction(null, null);

		try {

			OperationStatus status;

			cursor = this.database.openCursor(transaction, null);

			status = cursor.getSearchKeyRange(dbKey, dbValue, null);
			if (status.equals(OperationStatus.NOTFOUND)) {

				cursor.close();
				if (this.transaction == null) transaction.commit();
				return;
			}

			while (status.equals(OperationStatus.SUCCESS)) {

				if (new String(dbKey.getData()).startsWith(id)) {

					status = cursor.delete();
					if (! status.equals(OperationStatus.SUCCESS)) throw new Xdi2RuntimeException();
				} else {

					break;
				}

				status = cursor.getNext(dbKey, dbValue, null);
			}

			cursor.close();

			if (this.transaction == null) transaction.commit();
		} catch (Exception ex) {

			if (cursor != null) cursor.close();

			if (this.transaction == null) transaction.abort();
			throw new Xdi2RuntimeException("Cannot delete from database: " + ex.getMessage(), ex);
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

	/*
	 * Getters and setters
	 */

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
}
