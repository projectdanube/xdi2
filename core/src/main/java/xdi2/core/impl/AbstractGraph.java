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
	public ContextNode setDeepContextNode(XDIAddress contextNodeXDIAddress) {

		return this.getRootContextNode(false).setDeepContextNode(contextNodeXDIAddress);
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress contextNodeXDIAddress, boolean subgraph) {

		if (XDIConstants.XDI_ADD_ROOT.equals(contextNodeXDIAddress)) {

			return this.getRootContextNode(subgraph);
		} else {

			return this.getRootContextNode(false).getDeepContextNode(contextNodeXDIAddress, subgraph);
		}
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress contextNodeXDIAddress) {

		return this.getRootContextNode(false).getDeepContextNode(contextNodeXDIAddress);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDIAddress contextNodeXDIAddress) {

		return this.getRootContextNode(false).getDeepContextNodes(contextNodeXDIAddress);
	}

	@Override
	public Relation setDeepRelation(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress) {

		return this.getRootContextNode(false).setDeepRelation(contextNodeXDIAddress, XDIaddress, targetContextNodeXDIAddress);
	}

	@Override
	public Relation setDeepRelation(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress, ContextNode targetContextNode) {

		return this.getRootContextNode(false).setDeepRelation(contextNodeXDIAddress, XDIaddress, targetContextNode);
	}

	@Override
	public Relation getDeepRelation(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress) {

		return this.getRootContextNode(false).getDeepRelation(contextNodeXDIAddress, XDIaddress, targetContextNodeXDIAddress);
	}

	@Override
	public Relation getDeepRelation(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress) {

		return this.getRootContextNode(false).getDeepRelation(contextNodeXDIAddress, XDIaddress);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress) {

		return this.getRootContextNode(false).getDeepRelations(contextNodeXDIAddress, XDIaddress);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress contextNodeXDIAddress) {

		return this.getRootContextNode(false).getDeepRelations(contextNodeXDIAddress);
	}

	@Override
	public Literal setDeepLiteral(XDIAddress contextNodeXDIAddress, Object literalData) {

		return this.getRootContextNode(false).setDeepLiteral(contextNodeXDIAddress, literalData);
	}

	@Override
	public Literal setDeepLiteralString(XDIAddress contextNodeXDIAddress, String literalData) {

		return this.getRootContextNode(false).setDeepLiteralString(contextNodeXDIAddress, literalData);
	}

	@Override
	public Literal setDeepLiteralNumber(XDIAddress contextNodeXDIAddress, Double literalData) {

		return this.getRootContextNode(false).setDeepLiteralNumber(contextNodeXDIAddress, literalData);
	}

	@Override
	public Literal setDeepLiteralBoolean(XDIAddress contextNodeXDIAddress, Boolean literalData) {

		return this.getRootContextNode(false).setDeepLiteralBoolean(contextNodeXDIAddress, literalData);
	}

	@Override
	public Literal getDeepLiteral(XDIAddress contextNodeXDIAddress, Object literalData) {

		return this.getRootContextNode(false).getDeepLiteral(contextNodeXDIAddress, literalData);
	}

	@Override
	public Literal getDeepLiteralString(XDIAddress contextNodeXDIAddress, String literalData) {

		return this.getRootContextNode(false).getDeepLiteralString(contextNodeXDIAddress, literalData);
	}

	@Override
	public Literal getDeepLiteralNumber(XDIAddress contextNodeXDIAddress, Double literalData) {

		return this.getRootContextNode(false).getDeepLiteralNumber(contextNodeXDIAddress, literalData);
	}

	@Override
	public Literal getDeepLiteralBoolean(XDIAddress contextNodeXDIAddress, Boolean literalData) {

		return this.getRootContextNode(false).getDeepLiteralBoolean(contextNodeXDIAddress, literalData);
	}

	@Override
	public Literal getDeepLiteral(XDIAddress contextNodeXDIAddress) {

		return this.getRootContextNode(false).getDeepLiteral(contextNodeXDIAddress);
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
