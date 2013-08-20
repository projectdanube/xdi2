package xdi2.core.impl.json;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates graphs in JSON objects.
 * 
 * @author markus
 */
public class JSONGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public static final JSONStore DEFAULT_JSONSTORE = new FileJSONStore();

	private JSONStore jsonStore;

	public JSONGraphFactory() {

		super();

		this.jsonStore = DEFAULT_JSONSTORE;
	}

	@Override
	public Graph openGraph(String identifier) throws IOException {

		return new JSONGraph(this, identifier, this.getJsonStore());
	}

	public JSONStore getJsonStore() {

		return this.jsonStore;
	}

	public void setJsonStore(JSONStore jsonStore) {

		this.jsonStore = jsonStore;
	}
}
