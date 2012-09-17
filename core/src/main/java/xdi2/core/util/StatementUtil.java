package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.AbstractStatement.AbstractContextNodeStatement;
import xdi2.core.impl.AbstractStatement.AbstractLiteralStatement;
import xdi2.core.impl.AbstractStatement.AbstractRelationStatement;
import xdi2.core.xri3.impl.XRI3Reference;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.XRI3XRef;

/**
 * Various utility methods for working with statements.
 * 
 * @author markus
 */
public final class StatementUtil {

	private static final Logger log = LoggerFactory.getLogger(StatementUtil.class);

	private StatementUtil() { }

	/**
	 * Creates an XDI statement from its three components
	 * @param subject The statement's subject
	 * @param predicate The statement's predicate
	 * @param object The statement's object
	 * @return An XDI statement
	 */
	public static Statement fromComponents(final XRI3Segment subject, final XRI3Segment predicate, final XRI3Segment object) {

		if (XDIConstants.XRI_S_CONTEXT.equals(predicate)) {

			return new AbstractContextNodeStatement() {

				private static final long serialVersionUID = -8541962994048481222L;

				@Override
				public XRI3Segment getSubject() {

					return subject;
				}

				@Override
				public XRI3Segment getPredicate() {

					return predicate;
				}

				@Override
				public XRI3Segment getObject() {

					return object;
				}
			};
		} else if (XDIConstants.XRI_S_LITERAL.equals(predicate) && XDIUtil.isDataXriSegment(object)) {

			return new AbstractLiteralStatement() {

				private static final long serialVersionUID = -7808697310492171096L;

				@Override
				public XRI3Segment getSubject() {

					return subject;
				}

				@Override
				public XRI3Segment getPredicate() {

					return predicate;
				}

				@Override
				public XRI3Segment getObject() {

					return object;
				}
			};
		} else {

			return new AbstractRelationStatement() {

				private static final long serialVersionUID = 7444790938914661808L;

				@Override
				public XRI3Segment getSubject() {

					return subject;
				}

				@Override
				public XRI3Segment getPredicate() {

					return predicate;
				}

				@Override
				public XRI3Segment getObject() {

					return object;
				}
			};
		}
	}

	/**
	 * Creates an XDI statement from an XRI reference in the form subject/predicate/object
	 * @param reference The XRI reference
	 * @return An XDI statement
	 */
	public static Statement fromXriReference(XRI3Reference reference) throws Xdi2ParseException {

		XRI3Segment subject, predicate, object;

		try {

			if (reference.hasAuthority()) {

				if (reference.getPath().getNumSegments() != 2) throw new Xdi2ParseException("Invalid number of segments.");

				subject = new XRI3Segment(reference.getAuthority().toString());
				predicate = new XRI3Segment(reference.getPath().getSegment(0).toString());
				object = new XRI3Segment(reference.getPath().getSegment(1).toString());
			} else {

				if (reference.getPath().getNumSegments() != 3) throw new Xdi2ParseException("Invalid number of segments.");

				subject = new XRI3Segment(reference.getPath().getSegment(0).toString());
				predicate = new XRI3Segment(reference.getPath().getSegment(1).toString());
				object = new XRI3Segment(reference.getPath().getSegment(2).toString());
			}
		} catch (Exception ex) {

			throw new Xdi2ParseException("Cannot parse statement: " + ex.getMessage(), ex);
		}

		return fromComponents(subject, predicate, object);
	}

	/**
	 * Creates an XDI statement from an XRI segment in the form (subject/predicate/object)
	 * @param segment The XRI segment
	 * @return An XDI statement
	 */
	public static Statement fromXriSegment(XRI3Segment segment) throws Xdi2ParseException {

		XRI3SubSegment subSegment = (XRI3SubSegment) segment.getFirstSubSegment();
		if (subSegment == null) throw new Xdi2ParseException("No subsegment found: " + segment.toString());

		XRI3XRef xref = (XRI3XRef) subSegment.getXRef();
		if (xref == null) throw new Xdi2ParseException("No cross-reference found: " + segment.toString());

		XRI3Reference reference = (XRI3Reference) xref.getXRIReference();
		if (reference == null) throw new Xdi2ParseException("No XRI cross-reference found: " + segment.toString());

		return fromXriReference(reference);
	}

	/**
	 * Creates an XDI statement from a string in the form subject/predicate/object
	 * @param string The string
	 * @return An XDI statement
	 */
	public static Statement fromString(String string) throws Xdi2ParseException {

		return fromXriReference(new XRI3Reference(string));
	}

	/**
	 * Extracts a statement with a relative subject.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static Statement relativeStatement(Statement statement, XRI3Segment base, boolean variablesInXri, boolean variablesInBase) {

		if (log.isTraceEnabled()) log.trace("relativeStatement(" + statement + "," + base + "," + variablesInXri + "," + variablesInBase + ")");

		XRI3Segment subject = XRIUtil.relativeXri(statement.getSubject(), base, variablesInXri, variablesInBase);
		XRI3Segment predicate = statement.getPredicate();
		XRI3Segment object = statement.getObject();

		return fromComponents(subject, predicate, object);
	}

	/**
	 * Extracts a statement with a relative subject.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static Statement relativeStatement(Statement statement, XRI3Segment base) {

		return relativeStatement(statement, base, false, false);
	}

	/**
	 * Checks if a statement is implied by other statements in the graph.
	 * @param statement A statement.
	 * @return True, if the statement is implied by other statements in the graph.
	 */
	public static boolean isImplied(Statement statement) {

		if (! (statement instanceof ContextNodeStatement)) return false;

		ContextNode contextNode = ((ContextNodeStatement) statement).getContextNode();
		if (contextNode == null) return false;

		if (! contextNode.isEmpty()) return true;
		if (contextNode.getIncomingRelations().hasNext()) return true;

		return false;
	}
}
