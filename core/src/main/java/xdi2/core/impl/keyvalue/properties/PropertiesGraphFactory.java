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

	public static final File DEFAULT_PROPERTIES_FILE = new File(".", "xdi2-graph.properties");
	public static final boolean DEFAULT_AUTO_SAVE = false;

	private File file;
	private boolean autoSave;

	public PropertiesGraphFactory() {

		super(DEFAULT_SUPPORT_GET_CONTEXTNODES, DEFAULT_SUPPORT_GET_RELATIONS);

		this.file = DEFAULT_PROPERTIES_FILE;
		this.autoSave = DEFAULT_AUTO_SAVE;
	}

	@Override
	protected KeyValueStore openKeyValueStore() throws IOException {

		// open file

		PropertiesKeyValueStore keyValueStore;

		keyValueStore = new PropertiesKeyValueStore(this.file, this.autoSave);
		keyValueStore.load();

		// done

		return keyValueStore;
	}

	public File getFile() {

		return this.file;
	}

	public void setFile(File file) {

		this.file = file;
	}

	public boolean isAutoSave() {

		return this.autoSave;
	}

	public void setAutoSave(boolean autoSave) {

		this.autoSave = autoSave;
	}
}
