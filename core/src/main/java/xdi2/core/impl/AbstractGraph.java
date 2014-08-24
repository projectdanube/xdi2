package xdi2.core.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.Literal;
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
		StringWriter buffer = new StringWriter();

		try {

			writer.write(this, buffer);
		} catch (IOException ex) {

			return "[Exception: " + ex.getMessage() + "]";
		}

		return buffer.toString();
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
	 * Deep methods
	 */

	@Override
	public ContextNode setDeepContextNode(XDIAddress contextNodeAddress) {

		return this.getRootContextNode(false).setDeepContextNode(contextNodeAddress);
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress contextNodeAddress, boolean subgraph) {

		if (XDIConstants.XDI_ADD_ROOT.equals(contextNodeAddress)) {

			return this.getRootContextNode(subgraph);
		} else {

			return this.getRootContextNode(false).getDeepContextNode(contextNodeAddress, subgraph);
		}
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress contextNodeAddress) {

		return this.getRootContextNode(false).getDeepContextNode(contextNodeAddress);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDIAddress contextNodeAddress) {

		return this.getRootContextNode(false).getDeepContextNodes(contextNodeAddress);
	}

	@Override
	public Relation setDeepRelation(XDIAddress contextNodeAddress, XDIAddress arc, XDIAddress targetContextNodeAddress) {

		return this.getRootContextNode(false).setDeepRelation(contextNodeAddress, arc, targetContextNodeAddress);
	}

	@Override
	public Relation setDeepRelation(XDIAddress contextNodeAddress, XDIAddress arc, ContextNode targetContextNode) {

		return this.getRootContextNode(false).setDeepRelation(contextNodeAddress, arc, targetContextNode);
	}

	@Override
	public Relation getDeepRelation(XDIAddress contextNodeAddress, XDIAddress arc, XDIAddress targetContextNodeAddress) {

		return this.getRootContextNode(false).getDeepRelation(contextNodeAddress, arc, targetContextNodeAddress);
	}

	@Override
	public Relation getDeepRelation(XDIAddress contextNodeAddress, XDIAddress arc) {

		return this.getRootContextNode(false).getDeepRelation(contextNodeAddress, arc);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress contextNodeAddress, XDIAddress arc) {

		return this.getRootContextNode(false).getDeepRelations(contextNodeAddress, arc);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress contextNodeAddress) {

		return this.getRootContextNode(false).getDeepRelations(contextNodeAddress);
	}

	@Override
	public Literal setDeepLiteral(XDIAddress contextNodeAddress, Object literalData) {

		return this.getRootContextNode(false).setDeepLiteral(contextNodeAddress, literalData);
	}

	@Override
	public Literal setDeepLiteralString(XDIAddress contextNodeAddress, String literalData) {

		return this.getRootContextNode(false).setDeepLiteralString(contextNodeAddress, literalData);
	}

	@Override
	public Literal setDeepLiteralNumber(XDIAddress contextNodeAddress, Double literalData) {

		return this.getRootContextNode(false).setDeepLiteralNumber(contextNodeAddress, literalData);
	}

	@Override
	public Literal setDeepLiteralBoolean(XDIAddress contextNodeAddress, Boolean literalData) {

		return this.getRootContextNode(false).setDeepLiteralBoolean(contextNodeAddress, literalData);
	}

	@Override
	public Literal getDeepLiteral(XDIAddress contextNodeAddress, Object literalData) {

		return this.getRootContextNode(false).getDeepLiteral(contextNodeAddress, literalData);
	}

	@Override
	public Literal getDeepLiteralString(XDIAddress contextNodeAddress, String literalData) {

		return this.getRootContextNode(false).getDeepLiteralString(contextNodeAddress, literalData);
	}

	@Override
	public Literal getDeepLiteralNumber(XDIAddress contextNodeAddress, Double literalData) {

		return this.getRootContextNode(false).getDeepLiteralNumber(contextNodeAddress, literalData);
	}

	@Override
	public Literal getDeepLiteralBoolean(XDIAddress contextNodeAddress, Boolean literalData) {

		return this.getRootContextNode(false).getDeepLiteralBoolean(contextNodeAddress, literalData);
	}

	@Override
	public Literal getDeepLiteral(XDIAddress contextNodeAddress) {

		return this.getRootContextNode(false).getDeepLiteral(contextNodeAddress);
	}

	/*
	 * Methods related to statements
	 */

	@Override
	public Statement setStatement(XDIStatement statementAddress) {

		return this.getRootContextNode(false).setStatement(statementAddress);
	}

	@Override
	public Statement getStatement(XDIStatement statementAddress) {

		return this.getRootContextNode(false).getStatement(statementAddress);
	}

	@Override
	public boolean containsStatement(XDIStatement statementAddress) {

		return this.getRootContextNode(false).containsStatement(statementAddress);
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
