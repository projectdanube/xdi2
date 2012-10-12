package xdi2.core.impl.keyvalue;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates key/value graphs.
 * 
 * @author markus
 */
public abstract class AbstractKeyValueGraphFactory extends AbstractGraphFactory implements GraphFactory {

	private boolean supportGetContextNodes;
	private boolean supportGetRelations;

	public AbstractKeyValueGraphFactory(boolean supportGetContextNodes, boolean supportGetRelations) {

		this.supportGetContextNodes = supportGetContextNodes;
		this.supportGetRelations = supportGetRelations;
	}

	@Override
	public final Graph openGraph(String identifier) throws IOException {

		KeyValueStore keyValueStore = this.openKeyValueStore(identifier);

		return new KeyValueGraph(this, keyValueStore, this.getSupportGetContextNodes(), this.getSupportGetRelations());
	}

	/**
	 * This must be overridden by subclasses to instantiate the key/value store.
	 * @param identifier An optional identifier to distinguish key/value stores from one another.
	 */
	protected abstract KeyValueStore openKeyValueStore(String identifier) throws IOException;

	public boolean getSupportGetContextNodes() {

		return this.supportGetContextNodes;
	}

	public void setSupportGetContextNodes(boolean supportGetContextNodes) {

		this.supportGetContextNodes = supportGetContextNodes;
	}

	public boolean getSupportGetRelations() {

		return this.supportGetRelations;
	}

	public void setSupportGetRelations(boolean supportGetRelations) {

		this.supportGetRelations = supportGetRelations;
	}
}
