package xdi2.impl.keyvalue;

import java.io.IOException;

import xdi2.Graph;
import xdi2.GraphFactory;
import xdi2.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates key/value graphs.
 * 
 * @author markus
 */
public abstract class AbstractKeyValueGraphFactory extends AbstractGraphFactory implements GraphFactory {

	private boolean supportGetContextNodes;
	private boolean supportGetRelations;
	private boolean supportGetLiterals;

	public AbstractKeyValueGraphFactory(boolean supportGetContextNodes, boolean supportGetRelations, boolean supportGetLiterals) {

		this.supportGetContextNodes = supportGetContextNodes;
		this.supportGetRelations = supportGetRelations;
		this.supportGetLiterals = supportGetLiterals;
	}

	public final Graph openGraph() throws IOException {

		KeyValueStore keyValueStore = this.getKeyValueStore();

		return new KeyValueGraph(keyValueStore, this.supportGetContextNodes, this.supportGetRelations, this.supportGetLiterals);
	}

	protected abstract KeyValueStore getKeyValueStore() throws IOException;

	public boolean isSupportGetContextNodes() {

		return this.supportGetContextNodes;
	}

	public void setSupportGetContextNodes(boolean supportGetContextNodes) {

		this.supportGetContextNodes = supportGetContextNodes;
	}

	public boolean isSupportGetRelations() {

		return this.supportGetRelations;
	}

	public void setSupportGetRelations(boolean supportGetRelations) {

		this.supportGetRelations = supportGetRelations;
	}

	public boolean isSupportGetLiterals() {

		return this.supportGetLiterals;
	}

	public void setSupportGetLiterals(boolean supportGetLiterals) {

		this.supportGetLiterals = supportGetLiterals;
	}
}
