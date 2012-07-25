package xdi2.core.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = -5285276230236236923L;

	private static final Logger log = LoggerFactory.getLogger(AbstractGraph.class);

	/*
	 * General methods
	 */

	@Override
	public ContextNode findContextNode(XRI3Segment xri, boolean create) {

		return this.getRootContextNode().findContextNode(xri, create);
	}

	@Override
	public Relation findRelation(XRI3Segment xri, XRI3Segment arcXri) {

		return this.getRootContextNode().findRelation(xri, arcXri);
	}

	@Override
	public Iterator<Relation> findRelations(XRI3Segment xri, XRI3Segment arcXri) {

		return this.getRootContextNode().findRelations(xri, arcXri);
	}

	@Override
	public Literal findLiteral(XRI3Segment xri) {

		return this.getRootContextNode().findLiteral(xri);
	}

	@Override
	public boolean containsContextNode(XRI3Segment xri) {

		return this.findContextNode(xri, false) != null;
	}

	@Override
	public boolean containsRelations(XRI3Segment xri, XRI3Segment arcXri) {

		return this.findRelation(xri, arcXri) != null;
	}

	@Override
	public boolean containsLiteral(XRI3Segment xri) {

		return this.findLiteral(xri) != null;
	}

	@Override
	public String toString(String format) {

		return this.toString(format, null);
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

	/*
	 * Methods related to statements
	 */

	@Override
	public Statement addStatement(Statement statement) {

		XRI3Segment subject = statement.getSubject();
		XRI3Segment predicate = statement.getPredicate();
		XRI3Segment object = statement.getObject();

		ContextNode contextNode = this.findContextNode(subject, true);

		if (statement instanceof ContextNodeStatement) {

			ContextNode innerContextNode = contextNode.createContextNode(new XRI3SubSegment(object.toString()));
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created context node " + innerContextNode.getArcXri() + " --> " + innerContextNode.getXri());

			return innerContextNode.getStatement();
		} else if (statement instanceof LiteralStatement) {

			Literal literal = contextNode.createLiteral(XDIUtil.dataXriSegmentToString(object));
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created literal --> " + literal.getLiteralData());

			return literal.getStatement();
		} else if (statement instanceof RelationStatement) {

			Relation relation = contextNode.createRelation(predicate, object);
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created relation " + relation.getArcXri() + " --> " + relation.getRelationXri());

			return relation.getStatement();
		} else {

			throw new Xdi2RuntimeException("Unknown statement type: " + statement.getClass().getCanonicalName());
		}
	}

	/*
	 * Methods related to transactions.
	 */

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

		// two graphs are equal if all statements in one graph also exist in the other graph

		// TODO

		return other == this;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		// TODO

		return hashCode;
	}

	@Override
	public int compareTo(Graph other) {

		if (other == null || other == this) return 0;

		// TODO

		return 0;
	}
}
