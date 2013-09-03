package xdi2.core.impl.json;

import java.io.IOException;
import java.util.UUID;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates graphs in JSON objects.
 * 
 * @author markus
 */
public abstract class AbstractJSONGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public AbstractJSONGraphFactory() {

		super();
	}

	@Override
	public final Graph openGraph(String identifier) throws IOException {

		if (identifier == null) identifier = UUID.randomUUID().toString();

		JSONStore jsonStore = this.openJSONStore(identifier);

		return new JSONGraph(this, identifier, jsonStore);
	}

	/**
	 * This must be overridden by subclasses to instantiate the JSON store.
	 * @param identifier An optional identifier to distinguish JSON stores from one another.
	 */
	protected abstract JSONStore openJSONStore(String identifier) throws IOException;
}
