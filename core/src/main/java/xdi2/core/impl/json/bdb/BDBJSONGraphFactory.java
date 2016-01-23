package xdi2.core.impl.json.bdb;

import java.io.File;
import java.io.IOException;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.EnvironmentConfig;

import xdi2.core.GraphFactory;
import xdi2.core.impl.json.AbstractJSONGraphFactory;
import xdi2.core.impl.json.JSONStore;

/**
 * GraphFactory that creates BDB JSON graphs.
 * 
 * @author markus
 */
public class BDBJSONGraphFactory extends AbstractJSONGraphFactory implements GraphFactory {

	public static final String DEFAULT_DATABASE_PATH = "./xdi2-bdb-json/";

	private String databasePath;

	public BDBJSONGraphFactory() { 

		super();

		this.databasePath = DEFAULT_DATABASE_PATH;
	}

	@Override
	protected JSONStore openJSONStore(String identifier) throws IOException {

		// check identifier

		String databasePath = this.getDatabasePath();
		String databaseName = "xdi2-bdb-json-graph." + identifier;

		// open store

		File file = new File(databasePath);

		JSONStore jsonStore;

		try {

			if (! file.exists()) file.mkdir();

			EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			environmentConfig.setLocking(true);
			environmentConfig.setTransactional(true);

			DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			databaseConfig.setSortedDuplicates(false);
			databaseConfig.setTransactional(true);

			jsonStore = new BDBJSONStore(databasePath, databaseName, environmentConfig, databaseConfig);
			jsonStore.init();
		} catch (Exception ex) {

			throw new IOException("Cannot open database: " + ex.getMessage(), ex);
		}

		// done

		return jsonStore;
	}

	/*
	 * Getters and setters
	 */
	
	public String getDatabasePath() {

		return this.databasePath;
	}

	public void setDatabasePath(String path) {

		this.databasePath = path;
	}
}
