package xdi2.core.impl.keyvalue.bdb;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import xdi2.core.GraphFactory;
import xdi2.core.impl.keyvalue.AbstractKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.KeyValueStore;

import com.sleepycat.collections.CurrentTransaction;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * GraphFactory that creates BDB graphs.
 * 
 * @author markus
 */
public class BDBKeyValueGraphFactory extends AbstractKeyValueGraphFactory implements GraphFactory {

	public static final boolean DEFAULT_SUPPORT_GET_CONTEXTNODES = true; 
	public static final boolean DEFAULT_SUPPORT_GET_RELATIONS = true; 
	public static final boolean DEFAULT_SUPPORT_GET_LITERALS = true; 

	public static final String DEFAULT_DATABASE_PATH = "./xdi2-bdb/";

	private String databasePath;

	public BDBKeyValueGraphFactory() { 

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS);

		this.databasePath = DEFAULT_DATABASE_PATH;
	}

	@Override
	protected KeyValueStore openKeyValueStore(String identifier) throws IOException {

		// check identifier

		String databasePath = this.getDatabasePath();
		String databaseName = "xdi2-bdb-keyvalue-graph." + identifier;

		// open store

		File file = new File(databasePath);

		KeyValueStore keyValueStore;

		try {

			if (! file.exists()) file.mkdir();

			EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			environmentConfig.setLocking(true);
			environmentConfig.setTransactional(true);

			DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			databaseConfig.setSortedDuplicates(true);
			databaseConfig.setTransactional(true);

			keyValueStore = new BDBKeyValueStore(databasePath, databaseName, environmentConfig, databaseConfig);
			keyValueStore.init();
		} catch (Exception ex) {

			throw new IOException("Cannot open database: " + ex.getMessage(), ex);
		}

		// done

		return keyValueStore;
	}

	public void dumpGraph(String identifier, PrintStream stream) throws IOException {

		// check identifier

		String databasePath = this.getDatabasePath();
		String databaseName = "xdi2-bdb-keyvalue-graph." + identifier;

		// we use the current working directory

		File file = new File(this.getDatabasePath());

		try {

			// open database

			if (! file.exists()) file.mkdir();

			EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			environmentConfig.setLocking(true);
			environmentConfig.setTransactional(true);

			DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			databaseConfig.setSortedDuplicates(true);
			databaseConfig.setTransactional(true);

			// dump it

			DatabaseEntry dbKey = new DatabaseEntry();
			DatabaseEntry dbValue = new DatabaseEntry();

			Environment environment = new Environment(new File(databasePath), environmentConfig);
			Database database = environment.openDatabase(null, databaseName, databaseConfig);
			Transaction transaction = CurrentTransaction.getInstance(environment).beginTransaction(null);
			Cursor cursor = database.openCursor(transaction, null);

			OperationStatus status = cursor.getFirst(dbKey, dbValue, null);

			while (status.equals(OperationStatus.SUCCESS)) {

				stream.println(new String(dbKey.getData()) + " --> " + new String(dbValue.getData()));

				status = cursor.getNext(dbKey, dbValue, null);
			}

			transaction.commit();
			cursor.close();
			database.close();
			environment.close();
		} catch (Exception ex) {

			throw new IOException("Cannot dump database: " + ex.getMessage());
		}
	}

	public String getDatabasePath() {

		return this.databasePath;
	}

	public void setDatabasePath(String path) {

		this.databasePath = path;
	}
}
