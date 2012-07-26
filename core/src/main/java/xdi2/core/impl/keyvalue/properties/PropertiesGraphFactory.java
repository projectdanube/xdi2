package xdi2.core.impl.keyvalue.properties;

import java.io.File;
import java.io.IOException;

import xdi2.core.GraphFactory;
import xdi2.core.impl.keyvalue.AbstractKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.KeyValueStore;

/**
 * GraphFactory that creates properties file graphs.
 * 
 * @author markus
 */
public class PropertiesGraphFactory extends AbstractKeyValueGraphFactory implements GraphFactory {

	public static final boolean DEFAULT_SUPPORT_GET_CONTEXTNODES = true; 
	public static final boolean DEFAULT_SUPPORT_GET_RELATIONS = true; 

	public static final String DEFAULT_PROPERTIES_PATH = "xdi2-graph.properties";
	public static final boolean DEFAULT_AUTO_SAVE = true;

	private String path;
	private boolean autoSave;

	public PropertiesGraphFactory() {

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS);

		this.path = DEFAULT_PROPERTIES_PATH;
		this.autoSave = DEFAULT_AUTO_SAVE;
	}

	@Override
	protected KeyValueStore openKeyValueStore() throws IOException {

		// open file

		PropertiesKeyValueStore keyValueStore;

		keyValueStore = new PropertiesKeyValueStore(this.path, this.autoSave);
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

	public boolean isAutoSave() {

		return this.autoSave;
	}

	public void setAutoSave(boolean autoSave) {

		this.autoSave = autoSave;
	}
}
