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
public class BDBGraphFactory extends AbstractKeyValueGraphFactory implements GraphFactory {

	public static final boolean DEFAULT_SUPPORT_GET_CONTEXTNODES = true; 
	public static final boolean DEFAULT_SUPPORT_GET_RELATIONS = true; 
	public static final boolean DEFAULT_SUPPORT_GET_LITERALS = true; 

	public static final String DEFAULT_DATABASE_PATH = "./xdi2-bdb/";
	public static final String DEFAULT_DATABASE_NAME = "default";

	private String databasePath;
	private String databaseName;

	public BDBGraphFactory() { 

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS);

		this.databasePath = DEFAULT_DATABASE_PATH;
		this.databaseName = DEFAULT_DATABASE_NAME;
	}

	@Override
	protected KeyValueStore openKeyValueStore(String identifier) throws IOException {

		// check identifier

		if (identifier != null) {

			this.setDatabaseName("xdi2-test-graph." + identifier);
		}

		// we use the current working directory

		File file = new File(this.databasePath);

		KeyValueStore keyValueStore;

		try {

			// open store

			if (! file.exists()) file.mkdir();

			EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			environmentConfig.setLocking(true);
			environmentConfig.setTransactional(true);

			DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			databaseConfig.setSortedDuplicates(true);
			databaseConfig.setTransactional(true);

			keyValueStore = new BDBKeyValueStore(this.databasePath, this.databaseName, environmentConfig, databaseConfig);
			keyValueStore.init();
		} catch (Exception ex) {

			throw new IOException("Cannot open database: " + ex.getMessage(), ex);
		}

		// done

		return keyValueStore;
	}

	public void dumpGraph(PrintStream stream) throws IOException {

		// we use the current working directory

		File file = new File(this.databasePath);

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

			Environment environment = new Environment(new File(this.databasePath), environmentConfig);
			Database database = environment.openDatabase(null, this.databaseName, databaseConfig);
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

	public String getDatabaseName() {

		return this.databaseName;
	}

	public void setDatabaseName(String databaseName) {

		this.databaseName = databaseName;
	}
}
