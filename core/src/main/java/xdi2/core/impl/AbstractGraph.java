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
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

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
	public ContextNode setDeepContextNode(XDI3Segment contextNodeXri) {

		return this.getRootContextNode(false).setDeepContextNode(contextNodeXri);
	}

	@Override
	public ContextNode getDeepContextNode(XDI3Segment contextNodeXri, boolean subgraph) {

		if (XDIConstants.XRI_S_ROOT.equals(contextNodeXri)) {

			return this.getRootContextNode(subgraph);
		} else {

			return this.getRootContextNode(false).getDeepContextNode(contextNodeXri, subgraph);
		}
	}

	@Override
	public ContextNode getDeepContextNode(XDI3Segment contextNodeXri) {

		return this.getRootContextNode(false).getDeepContextNode(contextNodeXri);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDI3Segment contextNodeXri) {

		return this.getRootContextNode(false).getDeepContextNodes(contextNodeXri);
	}

	@Override
	public Relation setDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.getRootContextNode(false).setDeepRelation(contextNodeXri, arcXri, targetContextNodeXri);
	}

	@Override
	public Relation setDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, ContextNode targetContextNode) {

		return this.getRootContextNode(false).setDeepRelation(contextNodeXri, arcXri, targetContextNode);
	}

	@Override
	public Relation getDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.getRootContextNode(false).getDeepRelation(contextNodeXri, arcXri, targetContextNodeXri);
	}

	@Override
	public Relation getDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri) {

		return this.getRootContextNode(false).getDeepRelation(contextNodeXri, arcXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeXri, XDI3Segment arcXri) {

		return this.getRootContextNode(false).getDeepRelations(contextNodeXri, arcXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeXri) {

		return this.getRootContextNode(false).getDeepRelations(contextNodeXri);
	}

	@Override
	public Literal setDeepLiteral(XDI3Segment contextNodeXri, Object literalData) {

		return this.getRootContextNode(false).setDeepLiteral(contextNodeXri, literalData);
	}

	@Override
	public Literal setDeepLiteralString(XDI3Segment contextNodeXri, String literalData) {

		return this.getRootContextNode(false).setDeepLiteralString(contextNodeXri, literalData);
	}

	@Override
	public Literal setDeepLiteralNumber(XDI3Segment contextNodeXri, Double literalData) {

		return this.getRootContextNode(false).setDeepLiteralNumber(contextNodeXri, literalData);
	}

	@Override
	public Literal setDeepLiteralBoolean(XDI3Segment contextNodeXri, Boolean literalData) {

		return this.getRootContextNode(false).setDeepLiteralBoolean(contextNodeXri, literalData);
	}

	@Override
	public Literal getDeepLiteral(XDI3Segment contextNodeXri, Object literalData) {

		return this.getRootContextNode(false).getDeepLiteral(contextNodeXri, literalData);
	}

	@Override
	public Literal getDeepLiteralString(XDI3Segment contextNodeXri, String literalData) {

		return this.getRootContextNode(false).getDeepLiteralString(contextNodeXri, literalData);
	}

	@Override
	public Literal getDeepLiteralNumber(XDI3Segment contextNodeXri, Double literalData) {

		return this.getRootContextNode(false).getDeepLiteralNumber(contextNodeXri, literalData);
	}

	@Override
	public Literal getDeepLiteralBoolean(XDI3Segment contextNodeXri, Boolean literalData) {

		return this.getRootContextNode(false).getDeepLiteralBoolean(contextNodeXri, literalData);
	}

	@Override
	public Literal getDeepLiteral(XDI3Segment contextNodeXri) {

		return this.getRootContextNode(false).getDeepLiteral(contextNodeXri);
	}

	/*
	 * Methods related to statements
	 */

	@Override
	public Statement setStatement(XDI3Statement statementXri) {

		return this.getRootContextNode(false).setStatement(statementXri);
	}

	@Override
	public Statement getStatement(XDI3Statement statementXri) {

		return this.getRootContextNode(false).getStatement(statementXri);
	}

	@Override
	public boolean containsStatement(XDI3Statement statementXri) {

		return this.getRootContextNode(false).containsStatement(statementXri);
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
