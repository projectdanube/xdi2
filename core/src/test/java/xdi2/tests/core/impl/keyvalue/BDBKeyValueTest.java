package xdi2.tests.core.impl.keyvalue;

import java.io.File;
import java.io.IOException;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.EnvironmentConfig;

import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.impl.keyvalue.bdb.BDBKeyValueStore;

public class BDBKeyValueTest extends AbstractKeyValueTest {

	public static final String DEFAULT_DATABASE_PATH = "./xdi2-bdb/";

	@Override
	protected KeyValueStore getKeyValueStore(String id) throws IOException {

		String databaseName = "xdi2-keyvalue." + id;

		// we use the current working directory

		File file = new File(DEFAULT_DATABASE_PATH);

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

			keyValueStore = new BDBKeyValueStore(DEFAULT_DATABASE_PATH, databaseName, environmentConfig, databaseConfig);
			keyValueStore.init();
		} catch (Exception ex) {

			throw new IOException("Cannot open database: " + ex.getMessage());
		}

		// done

		return keyValueStore;
	}
}
