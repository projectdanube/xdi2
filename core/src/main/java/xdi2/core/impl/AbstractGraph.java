package xdi2.core.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = -5285276230236236923L;

	private static final Logger log = LoggerFactory.getLogger(AbstractContextNode.class);

	private GraphFactory graphFactory;

	protected AbstractGraph(GraphFactory graphFactory) {

		this.graphFactory = graphFactory;
	}

	/*
	 * General methods
	 */

	@Override
	public GraphFactory getGraphFactory() {

		return this.graphFactory;
	}

	@Override
	public void clear() {

		this.getRootContextNode().clear();
	}

	@Override
	public boolean isEmpty() {

		return this.getRootContextNode().isEmpty();
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
	public ContextNode createDeepContextNode(XDI3Segment contextNodeArcXris) {
		
		return this.getRootContextNode().createDeepContextNode(contextNodeArcXris);
	}

	@Override
	public ContextNode setDeepContextNode(XDI3Segment contextNodeArcXris) {

		return this.getRootContextNode().setDeepContextNode(contextNodeArcXris);
	}

	@Override
	public ContextNode getDeepContextNode(XDI3Segment contextNodeArcXris) {
		
		return this.getRootContextNode().getDeepContextNode(contextNodeArcXris);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDI3Segment contextNodeArcXris) {

		return this.getRootContextNode().getDeepContextNodes(contextNodeArcXris);
	}

	@Override
	public Relation createDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.getRootContextNode().createDeepRelation(contextNodeArcXris, arcXri, targetContextNodeXri);
	}

	@Override
	public Relation createDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, ContextNode targetContextNode) {
		
		return this.getRootContextNode().createDeepRelation(contextNodeArcXris, arcXri, targetContextNode);
	}

	@Override
	public Relation setDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {
		
		return this.getRootContextNode().setDeepRelation(contextNodeArcXris, arcXri, targetContextNodeXri);
	}

	@Override
	public Relation setDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, ContextNode targetContextNode) {

		return this.getRootContextNode().setDeepRelation(contextNodeArcXris, arcXri, targetContextNode);
	}

	@Override
	public Relation getDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.getRootContextNode().getDeepRelation(contextNodeArcXris, arcXri, targetContextNodeXri);
	}

	@Override
	public Relation getDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri) {

		return this.getRootContextNode().getDeepRelation(contextNodeArcXris, arcXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeArcXris, XDI3Segment arcXri) {

		return this.getRootContextNode().getDeepRelations(contextNodeArcXris, arcXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeArcXris) {

		return this.getRootContextNode().getDeepRelations(contextNodeArcXris);
	}

	@Override
	public Literal createDeepLiteral(XDI3Segment contextNodeArcXris, String literalData) {

		return this.getRootContextNode().createDeepLiteral(contextNodeArcXris, literalData);
	}

	@Override
	public Literal setDeepLiteral(XDI3Segment contextNodeArcXris, String literalData) {

		return this.getRootContextNode().setDeepLiteral(contextNodeArcXris, literalData);
	}

	@Override
	public Literal getDeepLiteral(XDI3Segment contextNodeArcXris, String literalData) {

		return this.getRootContextNode().getDeepLiteral(contextNodeArcXris, literalData);
	}

	@Override
	public Literal getDeepLiteral(XDI3Segment contextNodeArcXris) {

		return this.getRootContextNode().getDeepLiteral(contextNodeArcXris);
	}
	
	/*
	 * Methods related to statements
	 */

	@Override
	public Statement createStatement(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("createStatement(" + statementXri + ")");

		// find the root and the base context node of this statement

		XdiRoot root = XdiLocalRoot.findLocalRoot(this).findRoot(statementXri.getSubject(), true);
		XDI3Segment relativePart = root.getRelativePart(statementXri.getSubject());

		// inner root short notation?

		if (statementXri.hasInnerRootStatement()) {

			XDI3Segment subject = relativePart;
			XDI3Segment predicate = statementXri.getPredicate();

			XdiInnerRoot innerRoot = root.findInnerRoot(subject, predicate, true);

			return innerRoot.createRelativeStatement(statementXri.getInnerRootStatement());
		}

		// add the statement

		if (statementXri.isContextNodeStatement()) {

			ContextNode contextNode = root.getContextNode().createDeepContextNode(XDI3Util.expandXri((XDI3Segment) statementXri.getObject(), relativePart));

			return contextNode.getStatement();
		} else if (statementXri.isRelationStatement()) {

			Relation relation = root.getContextNode().createDeepRelation(relativePart, statementXri.getArcXri(), statementXri.getTargetContextNodeXri());

			return relation.getStatement();
		} else if (statementXri.isLiteralStatement()) {

			Literal literal = root.getContextNode().createDeepLiteral(relativePart, statementXri.getLiteralData());

			return literal.getStatement();
		} else {

			throw new Xdi2GraphException("Invalid statement XRI: " + statementXri);
		}
	}

	@Override
	public Statement setStatement(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("setStatement(" + statementXri + ")");

		// find the root and the base context node of this statement

		XdiRoot root = XdiLocalRoot.findLocalRoot(this).findRoot(statementXri.getSubject(), true);
		XDI3Segment relativePart = root.getRelativePart(statementXri.getSubject());

		// inner root short notation?

		if (statementXri.hasInnerRootStatement()) {

			XDI3Segment subject = relativePart;
			XDI3Segment predicate = statementXri.getPredicate();

			XdiInnerRoot innerRoot = root.findInnerRoot(subject, predicate, true);

			return innerRoot.setRelativeStatement(statementXri.getInnerRootStatement());
		}

		// set the statement

		if (statementXri.isContextNodeStatement()) {

			ContextNode contextNode = root.getContextNode().setDeepContextNode(XDI3Util.expandXri((XDI3Segment) statementXri.getObject(), relativePart));

			return contextNode.getStatement();
		} else if (statementXri.isRelationStatement()) {

			Relation relation = root.getContextNode().setDeepRelation(relativePart, statementXri.getArcXri(), statementXri.getTargetContextNodeXri());

			return relation.getStatement();
		} else if (statementXri.isLiteralStatement()) {

			Literal literal = root.getContextNode().setDeepLiteral(relativePart, statementXri.getLiteralData());

			return literal.getStatement();
		} else {

			throw new Xdi2GraphException("Invalid statement XRI: " + statementXri);
		}
	}

	@Override
	public Statement getStatement(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("getStatement(" + statementXri + ")");

		ContextNode baseContextNode = this.getDeepContextNode(statementXri.getSubject());
		if (baseContextNode == null) return null;

		if (statementXri.isContextNodeStatement()) {

			ContextNode contextNode = baseContextNode.getDeepContextNode((XDI3Segment) statementXri.getObject());

			return contextNode == null ? null : contextNode.getStatement();
		} else if (statementXri.isRelationStatement()) {

			Relation relation = baseContextNode.getRelation(statementXri.getArcXri(), statementXri.getTargetContextNodeXri());

			return relation == null ? null : relation.getStatement();
		} else if (statementXri.isLiteralStatement()) {

			Literal literal = baseContextNode.getLiteral(statementXri.getLiteralData());

			return literal == null ? null : literal.getStatement();
		}

		return null;
	}

	@Override
	public boolean containsStatement(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("containsStatement(" + statementXri + ")");

		return this.getStatement(statementXri) != null;
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
