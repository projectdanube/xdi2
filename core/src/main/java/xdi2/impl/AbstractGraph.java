package xdi2.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.io.XDIWriter;
import xdi2.io.XDIWriterRegistry;
import xdi2.util.XDIConstants;
import xdi2.xri3.impl.XRI3;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = -5285276230236236923L;

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

	public Literal findLiteral(XRI3Segment xri) {

		ContextNode contextNode = this.findContextNode(xri, false);
		if (contextNode == null) return null;

		return contextNode.getLiteral();
	}

	public boolean containsContextNode(XRI3Segment xri) {

		return this.findContextNode(xri, false) != null;
	}

	public boolean containsRelation(XRI3Segment xri, XRI3Segment arcXri) {

		return this.findRelation(xri, arcXri) != null;
	}

	public boolean containsLiteral(XRI3Segment xri) {

		return this.findLiteral(xri) != null;
	}

	public String toString(String format) {

		return this.toString(format, null);
	}

	public String toString(String format, Properties parameters) {

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

		return this.toString(XDIWriterRegistry.getDefault().getFormat(), null);
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
