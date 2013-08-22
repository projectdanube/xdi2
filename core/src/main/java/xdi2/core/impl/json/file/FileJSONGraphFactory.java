package xdi2.core.impl.json.file;

import java.io.IOException;

import xdi2.core.GraphFactory;
import xdi2.core.impl.json.AbstractJSONGraphFactory;
import xdi2.core.impl.json.JSONStore;

/**
 * GraphFactory that creates file-based JSON graphs.
 * 
 * @author markus
 */
public class FileJSONGraphFactory extends AbstractJSONGraphFactory implements GraphFactory {

	public static final String DEFAULT_PREFIX = null;

	private String prefix;

	public FileJSONGraphFactory() { 

		super();

		this.prefix = DEFAULT_PREFIX;
	}

	@Override
	protected JSONStore openJSONStore(String identifier) throws IOException {

		// check identifier

		String prefix = this.getPrefix();

		if (identifier != null) {

			prefix = "xdi2-bdb-graph." + identifier;
		}

		// open store

		JSONStore jsonStore;

		try {

			jsonStore = new FileJSONStore(prefix);
			jsonStore.init();
		} catch (Exception ex) {

			throw new IOException("Cannot open JSON store: " + ex.getMessage(), ex);
		}

		// done

		return jsonStore;
	}

	public String getPrefix() {

		return this.prefix;
	}

	public void setPrefix(String prefix) {

		this.prefix = prefix;
	}
}
