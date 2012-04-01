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
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.XDIConstants;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.parser.ParserException;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = -5285276230236236923L;

	private static final Logger log = LoggerFactory.getLogger(AbstractGraph.class);

	/*
	 * General methods
	 */

	public ContextNode findContextNode(XRI3Segment xri, boolean create) {

		ContextNode contextNode = this.getRootContextNode();
		if (XDIConstants.XRI_S_CONTEXT.equals(xri)) return contextNode;

		for (Iterator<?> arcXris = xri.getSubSegments().iterator(); arcXris.hasNext(); ) {

			XRI3SubSegment arcXri = (XRI3SubSegment) arcXris.next();

			ContextNode innerContextNode = contextNode.getContextNode(arcXri);
			if (innerContextNode == null) {

				if (create) {

					innerContextNode = contextNode.createContextNode(arcXri);
				} else {

					return null;
				}
			}

			contextNode = innerContextNode;
		}

		return contextNode;
	}

	public Relation findRelation(XRI3Segment xri, XRI3Segment arcXri) {

		ContextNode contextNode = this.findContextNode(xri, false);
		if (contextNode == null) return null;

		return contextNode.getRelation(arcXri);
	}

	public Iterator<Relation> findRelations(XRI3Segment xri, XRI3Segment arcXri) {

		ContextNode contextNode = this.findContextNode(xri, false);
		if (contextNode == null) return null;

		return contextNode.getRelations(arcXri);
	}

	public Literal findLiteral(XRI3Segment xri) {

		ContextNode contextNode = this.findContextNode(xri, false);
		if (contextNode == null) return null;

		return contextNode.getLiteral();
	}

	public boolean containsContextNode(XRI3Segment xri) {

		return this.findContextNode(xri, false) != null;
	}

	public boolean containsRelations(XRI3Segment xri, XRI3Segment arcXri) {

		return this.findRelation(xri, arcXri) != null;
	}

	public boolean containsLiteral(XRI3Segment xri) {

		return this.findLiteral(xri) != null;
	}

	public String toString(String format) {

		return this.toString(format, null);
	}

	public String toString(String format, Properties parameters) {

		if (format == null) format = XDIWriterRegistry.getDefault().getFormat();

		XDIWriter writer = XDIWriterRegistry.forFormat(format);
		StringWriter buffer = new StringWriter();

		try {

			writer.write(this, buffer, parameters);
		} catch (IOException ex) {

			return "[Exception: " + ex.getMessage() + "]";
		}

		return buffer.toString();
	}

	/*
	 * Methods related to messages
	 */

	public Graph applyOperation(Graph operationGraph, XRI3Segment operationXri) throws Xdi2MessagingException {

		// TODO

		return null;
	}

	public Graph applyOperation(XRI3 address, XRI3Segment operationXri) throws Xdi2MessagingException {

		// TODO

		return null;
	}

	/*
	 * Methods related to statements
	 */

	public Statement addStatement(String statement) throws Xdi2ParseException {

		String[] segments = statement.split("/");
		if (segments.length != 3) throw new Xdi2ParseException("Invalid statement.");

		XRI3Segment subject, predicate, object;

		try {

			subject = new XRI3Segment(segments[0]);
			predicate = new XRI3Segment(segments[1]);
			object = new XRI3Segment(segments[2]);
		} catch (ParserException ex) {

			throw new Xdi2ParseException("Cannot parse XRI: " + ex.getMessage(), ex);
		}

		ContextNode contextNode = this.findContextNode(subject, true);

		if (XDIConstants.XRI_S_CONTEXT.equals(predicate)) {

			ContextNode innerContextNode = contextNode.createContextNode(new XRI3SubSegment(object.toString()));
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created context node " + innerContextNode.getArcXri() + " --> " + innerContextNode.getXri());

			return innerContextNode.getStatement();
		} else if (XDIConstants.XRI_S_LITERAL.equals(predicate)) {

			Literal literal = contextNode.createLiteral(XDIUtil.dataXriSegmentToString(object));
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created literal --> " + literal.getLiteralData());

			return literal.getStatement();
		} else {

			Relation relation = contextNode.createRelation(predicate, object);
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created relation " + relation.getArcXri() + " --> " + relation.getRelationXri());

			return relation.getStatement();
		}
	}


	/*
	 * Methods related to transactions.
	 */

	public void beginTransaction() {

	}

	public void commitTransaction() {

	}

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

	public int compareTo(Graph other) {

		if (other == null || other == this) return 0;

		// TODO

		return 0;
	}
}
