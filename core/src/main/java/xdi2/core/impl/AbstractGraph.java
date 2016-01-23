package xdi2.core.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.LiteralNode;
import xdi2.core.Node;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.ReadOnlyIterator;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = -5285276230236236923L;

	private GraphFactory graphFactory;
	private String identifier;

	protected AbstractGraph(GraphFactory graphFactory, String identifier) {

		this.graphFactory = graphFactory;
		this.identifier = identifier;
	}

	/*
	 * General methods
	 */

	@Override
	public GraphFactory getGraphFactory() {

		return this.graphFactory;
	}

	@Override
	public String getIdentifier() {

		return this.identifier;
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.getRootContextNode(false);
	}

	@Override
	public void clear() {

		this.getRootContextNode(false).clear();
	}

	@Override
	public boolean isEmpty() {

		return this.getRootContextNode(false).isEmpty();
	}

	@Override
	public String toString(String format, Properties parameters) {

		if (format == null) format = XDIWriterRegistry.getDefault().getFormat();

		XDIWriter writer = XDIWriterRegistry.forFormat(format, parameters);
		if (writer == null) throw new Xdi2RuntimeException("Unknown format for XDI serialization: " + format);

		StringWriter buffer = new StringWriter();

		try {

			writer.write(this, buffer);
		} catch (IOException ex) {

			return "[Exception: " + ex.getMessage() + "]";
		}

		return buffer.toString();
	}

	@Override
	public String toString(String format) {

		return this.toString(format, null);
	}

	@Override
	public String toString(MimeType mimeType) {

		if (mimeType == null) throw new NullPointerException();

		XDIWriter writer = XDIWriterRegistry.forMimeType(mimeType);
		if (writer == null) throw new Xdi2RuntimeException("Unknown MIME type for XDI serialization: " + mimeType);

		StringWriter buffer = new StringWriter();

		try {

			writer.write(this, buffer);
		} catch (IOException ex) {

			return "[Exception: " + ex.getMessage() + "]";
		}

		return buffer.toString();
	}

	/*
	 * Deep methods for nodes
	 */

	@Override
	public Node setDeepNode(XDIAddress nodeXDIAddress) {

		return this.getRootContextNode(false).setDeepNode(nodeXDIAddress);
	}

	@Override
	public Node getDeepNode(XDIAddress nodeXDIAddress, boolean subgraph) {

		if (XDIConstants.XDI_ADD_ROOT.equals(nodeXDIAddress)) {

			return this.getRootContextNode(subgraph);
		} else {

			return this.getRootContextNode(false).getDeepNode(nodeXDIAddress, subgraph);
		}
	}

	@Override
	public Node getDeepNode(XDIAddress nodeXDIAddress) {

		return this.getRootContextNode(false).getDeepNode(nodeXDIAddress);
	}

	/*
	 * Deep methods for context nodes
	 */

	@Override
	public ContextNode setDeepContextNode(XDIAddress contextNodeXDIAddress) {

		return this.getRootContextNode(false).setDeepContextNode(contextNodeXDIAddress);
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress contextNodeXDIAddress, boolean subgraph) {

		return this.getRootContextNode(false).getDeepContextNode(contextNodeXDIAddress, subgraph);
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress contextNodeXDIAddress) {

		return this.getRootContextNode(false).getDeepContextNode(contextNodeXDIAddress);
	}

	/*
	 * Deep methods for literal nodes
	 */

	@Override
	public LiteralNode setDeepLiteralNode(XDIAddress literalNodeXDIAddress) {

		return this.getRootContextNode(false).setDeepLiteralNode(literalNodeXDIAddress);
	}

	@Override
	public LiteralNode getDeepLiteralNode(XDIAddress literalNodeXDIAddress, boolean subgraph) {

		return this.getRootContextNode(false).getDeepLiteralNode(literalNodeXDIAddress, subgraph);
	}

	@Override
	public LiteralNode getDeepLiteralNode(XDIAddress literalNodeXDIAddress) {

		return this.getRootContextNode(false).getDeepLiteralNode(literalNodeXDIAddress);
	}

	/*
	 * Deep methods for relations
	 */

	@Override
	public Relation setDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		return this.getRootContextNode(false).setDeepRelation(relativeContextNodeXDIAddress, XDIaddress, targetXDIAddress);
	}

	@Override
	public Relation setDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress, Node targetNode) {

		return this.getRootContextNode(false).setDeepRelation(relativeContextNodeXDIAddress, XDIaddress, targetNode);
	}

	@Override
	public Relation getDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		return this.getRootContextNode(false).getDeepRelation(relativeContextNodeXDIAddress, XDIaddress, targetXDIAddress);
	}

	@Override
	public Relation getDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress) {

		return this.getRootContextNode(false).getDeepRelation(relativeContextNodeXDIAddress, XDIaddress);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress) {

		return this.getRootContextNode(false).getDeepRelations(relativeContextNodeXDIAddress, XDIaddress);
	}

	/*
	 * Methods related to statements
	 */

	@Override
	public Statement setStatement(XDIStatement XDIstatement) {

		return this.getRootContextNode(false).setStatement(XDIstatement);
	}

	@Override
	public Statement getStatement(XDIStatement XDIstatement) {

		return this.getRootContextNode(false).getStatement(XDIstatement);
	}

	@Override
	public boolean containsStatement(XDIStatement XDIstatement) {

		return this.getRootContextNode(false).containsStatement(XDIstatement);
	}

	@Override
	public ReadOnlyIterator<Statement> getAllStatements() {

		return this.getRootContextNode(false).getAllStatements();
	}

	@Override
	public long getAllStatementCount() {

		return this.getRootContextNode(false).getAllStatementCount();
	}

	/*
	 * Methods related to transactions
	 */

	@Override
	public boolean supportsTransactions() {

		return false;
	}

	@Override
	public void beginTransaction() {

	}

	@Override
	public void commitTransaction() {

	}

	@Override
	public void rollbackTransaction() {

	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.toString(null, null);
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Graph)) return false;
		if (object == this) return true;

		Graph other = (Graph) object;

		// TODO: do this without serializing to string

		return this.toString(new MimeType("text/xdi;implied=1;ordered=1")).equals(other.toString(new MimeType("text/xdi;implied=1;ordered=1")));
	}

	@Override
	public int hashCode() {

		// TODO: do this without serializing to string

		return this.toString(new MimeType("text/xdi;implied=1;ordered=1")).hashCode();
	}

	@Override
	public int compareTo(Graph other) {

		if (other == null || other == this) return 0;

		// TODO: do this without serializing to string

		String string1 = this.toString(new MimeType("text/xdi;implied=1;ordered=1"));
		String string2 = other.toString(new MimeType("text/xdi;implied=1;ordered=1"));

		return string1.compareTo(string2);
	}
}
