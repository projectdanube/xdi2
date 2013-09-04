package xdi2.core.impl.keyvalue.properties;

import java.io.IOException;

import xdi2.core.GraphFactory;
import xdi2.core.impl.keyvalue.AbstractKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.KeyValueStore;

/**
 * GraphFactory that creates properties file graphs.
 * 
 * @author markus
 */
public class PropertiesKeyValueGraphFactory extends AbstractKeyValueGraphFactory implements GraphFactory {

	public static final boolean DEFAULT_SUPPORT_GET_CONTEXTNODES = true; 
	public static final boolean DEFAULT_SUPPORT_GET_RELATIONS = true; 

	public PropertiesKeyValueGraphFactory() {

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS);
	}

	@Override
	protected KeyValueStore openKeyValueStore(String identifier) throws IOException {

		// check identifier

		String path = "xdi2-properties-keyvalue-graph." + identifier + ".properties";

		// open store

		KeyValueStore keyValueStore;

		keyValueStore = new PropertiesKeyValueStore(path);
		keyValueStore.init();

		// done

		return keyValueStore;
	}
}
