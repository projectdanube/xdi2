package xdi2.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.exceptions.MessagingException;
import xdi2.io.XDIWriter;
import xdi2.io.XDIWriterRegistry;
import xdi2.xri3.impl.XRI3;
import xdi2.xri3.impl.XRI3Segment;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = -5285276230236236923L;

	/*
	 * General methods
	 */

	public ContextNode findContextNode(XRI3Segment xri) {

		return null;
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

			return("[Exception: " + ex.getMessage() + "]");
		}

		return(buffer.toString());
	}

	/*
	 * Methods related to messages
	 */

	public Graph applyOperation(Graph operationGraph, XRI3Segment operationXri) throws MessagingException {

		// TODO

		return null;
	}

	public Graph applyOperation(XRI3 address, XRI3Segment operationXri) throws MessagingException {

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

		if (other == null || other == this) return(0);

		// TODO
		
		return 0;
	}
}
