package xdi2.impl.keyvalue.bdb;

import java.io.File;
import java.io.IOException;

import xdi2.GraphFactory;
import xdi2.impl.keyvalue.AbstractKeyValueGraphFactory;
import xdi2.impl.keyvalue.KeyValueStore;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.EnvironmentConfig;

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

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS, DEFAULT_SUPPORT_GET_LITERALS);

		this.databasePath = DEFAULT_DATABASE_PATH;
		this.databaseName = DEFAULT_DATABASE_NAME;
	}

	protected KeyValueStore getKeyValueStore() throws IOException {

		// we use the current working directory

		File file = new File(this.databasePath);

		// open database

		BDBKeyValueStore keyValueStore;

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

			keyValueStore = new BDBKeyValueStore(this.databasePath, this.databaseName, environmentConfig, databaseConfig);

			// test it

			keyValueStore.openDatabase();
			keyValueStore.closeDatabase();
			keyValueStore.openDatabase();
		} catch (Exception ex) {

			throw new IOException("Cannot open database: " + ex.getMessage());
		}

		// done

		return keyValueStore;
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
