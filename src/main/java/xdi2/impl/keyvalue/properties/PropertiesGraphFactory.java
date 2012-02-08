package xdi2.impl.keyvalue.properties;

import java.io.File;
import java.io.IOException;

import xdi2.GraphFactory;
import xdi2.impl.keyvalue.AbstractKeyValueGraphFactory;
import xdi2.impl.keyvalue.KeyValueStore;

/**
 * GraphFactory that creates properties file graphs.
 * 
 * @author markus
 */
public final class PropertiesGraphFactory extends AbstractKeyValueGraphFactory implements GraphFactory {

	public static final boolean DEFAULT_SUPPORT_GET_CONTEXTNODES = true; 
	public static final boolean DEFAULT_SUPPORT_GET_RELATIONS = true; 
	public static final boolean DEFAULT_SUPPORT_GET_LITERALS = true; 

	public static final String DEFAULT_PROPERTIES_PATH = "./xdi2-graph.properties";

	private String path;

	public PropertiesGraphFactory() {

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS, DEFAULT_SUPPORT_GET_LITERALS);
		
		this.path = DEFAULT_PROPERTIES_PATH;
	}

	protected KeyValueStore getKeyValueStore() throws IOException {

		// we use the current working directory

		File file = new File(this.path);

		// open file

		PropertiesKeyValueStore keyValueStore;

		keyValueStore = new PropertiesKeyValueStore(file);
		keyValueStore.load();

		// done
		
		return keyValueStore;
	}

	public String getPath() {

		return this.path;
	}

	public void setPath(String path) {

		this.path = path;
	}
}
