package xdi2.core.impl.json.memory;

import java.io.IOException;

import xdi2.core.GraphFactory;
import xdi2.core.impl.json.AbstractJSONGraphFactory;
import xdi2.core.impl.json.JSONStore;

/**
 * GraphFactory that creates in-memory JSON graphs.
 * 
 * @author markus
 */
public class MemoryJSONGraphFactory extends AbstractJSONGraphFactory implements GraphFactory {

	public MemoryJSONGraphFactory() { 

		super();
	}

	@Override
	protected JSONStore openJSONStore(String identifier) throws IOException {

		// open store

		JSONStore jsonStore;

		try {

			jsonStore = new MemoryJSONStore();
			jsonStore.init();
		} catch (Exception ex) {

			throw new IOException("Cannot open JSON store: " + ex.getMessage(), ex);
		}

		// done

		return jsonStore;
	}
}
