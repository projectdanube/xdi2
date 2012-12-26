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
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.XDIUtil;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3SubSegment;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = -5285276230236236923L;

	private static final Logger log = LoggerFactory.getLogger(AbstractGraph.class);

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
	public ContextNode findContextNode(XDI3Segment contextNodeXri, boolean create) {

		return this.getRootContextNode().findContextNode(contextNodeXri, create);
	}

	@Override
	public Relation findRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.getRootContextNode().findRelation(contextNodeXri, arcXri, targetContextNodeXri);
	}

	@Override
	public Relation findRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri) {

		return this.getRootContextNode().findRelation(contextNodeXri, arcXri);
	}

	@Override
	public ReadOnlyIterator<Relation> findRelations(XDI3Segment contextNodeXri, XDI3Segment arcXri) {

		return this.getRootContextNode().findRelations(contextNodeXri, arcXri);
	}

	@Override
	public Literal findLiteral(XDI3Segment contextNodeXri) {

		return this.getRootContextNode().findLiteral(contextNodeXri);
	}

	@Override
	public boolean containsContextNode(XDI3Segment contextNodeXri) {

		return this.findContextNode(contextNodeXri, false) != null;
	}

	@Override
	public boolean containsRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.findRelation(contextNodeXri, arcXri, targetContextNodeXri) != null;
	}

	@Override
	public boolean containsRelations(XDI3Segment contextNodeXri, XDI3Segment arcXri) {

		return this.findRelation(contextNodeXri, arcXri) != null;
	}

	@Override
	public boolean containsLiteral(XDI3Segment contextNodeXri) {

		return this.findLiteral(contextNodeXri) != null;
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
	public Statement createStatement(Statement statement) {

		XDI3Segment subject = statement.getSubject();
		XDI3Segment predicate = statement.getPredicate();
		XDI3Segment object = statement.getObject();

		ContextNode contextNode = this.findContextNode(subject, true);

		if (statement instanceof ContextNodeStatement) {

			ContextNode innerContextNode = contextNode.createContextNode(new XDI3SubSegment(object.toString()));
			if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created context node " + innerContextNode.getArcXri() + " --> " + innerContextNode.getXri());

			return innerContextNode.getStatement();
		} else if (statement instanceof LiteralStatement) {

			Literal literal = contextNode.createLiteral(XDIUtil.dataXriSegmentToString(object));
			if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created literal --> " + literal.getLiteralData());

			return literal.getStatement();
		} else if (statement instanceof RelationStatement) {

			Relation relation = contextNode.createRelation(predicate, object);
			if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created relation " + relation.getArcXri() + " --> " + relation.getTargetContextNodeXri());

			return relation.getStatement();
		} else {

			throw new Xdi2RuntimeException("Unknown statement type: " + statement.getClass().getCanonicalName());
		}
	}

	@Override
	public boolean containsStatement(Statement statement) {

		ContextNode contextNode = this.findContextNode(statement.getSubject(), false);
		if (contextNode == null) return false;

		if (statement instanceof ContextNodeStatement) {

			return new IteratorContains(new MappingContextNodeStatementIterator(contextNode.getContextNodes()), statement).contains();
		} else if (statement instanceof LiteralStatement) {

			return new IteratorContains(new MappingLiteralStatementIterator(contextNode.getLiteral()), statement).contains();
		} else if (statement instanceof RelationStatement) {

			return new IteratorContains(new MappingRelationStatementIterator(contextNode.getRelations()), statement).contains();
		} else {

			throw new Xdi2RuntimeException("Unknown statement type: " + statement.getClass().getCanonicalName());
		}
	}

	/*
	 * Methods related to transactions.
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

		return this.toString(new MimeType("text/xdi;contexts=1;ordered=1")).equals(other.toString(new MimeType("text/xdi;contexts=1;ordered=1")));
	}

	@Override
	public int hashCode() {

		// TODO: do this without serializing to string

		return this.toString(new MimeType("text/xdi;contexts=1;ordered=1")).hashCode();
	}

	@Override
	public int compareTo(Graph other) {

		if (other == null || other == this) return 0;

		// TODO: do this without serializing to string

		String string1 = this.toString(new MimeType("text/xdi;contexts=1;ordered=1"));
		String string2 = other.toString(new MimeType("text/xdi;contexts=1;ordered=1"));

		return string1.compareTo(string2);
	}
}
