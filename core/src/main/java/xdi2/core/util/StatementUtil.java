package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

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
	public static XDI3Statement fromComponents(final XDI3Segment subject, final XDI3Segment predicate, final XDI3Segment object) {

		return XDI3Statement.create("" + subject + "/" + predicate + "/" + object);
	}

	/**
	 * Creates an XDI statement from a context node XRI and an arc XRI.
	 * @param contextNodeXri The context node XRI
	 * @param arcXri The arc XRI
	 * @return An XDI statement
	 */
	public static XDI3Statement fromContextNodeComponents(final XDI3Segment contextNodeXri, final XDI3Segment arcXri) {

		return fromComponents(contextNodeXri, XDIConstants.XRI_S_CONTEXT, arcXri);
	}

	/**
	 * Creates an XDI statement from a context node XRI, arc XRI, and target context node XRI.
	 * @param contextNodeXri The context node XRI
	 * @param arcXri The arc XRI
	 * @return An XDI statement
	 */
	public static XDI3Statement fromRelationComponents(final XDI3Segment contextNodeXri, final XDI3Segment arcXri, final XDI3Segment targetContextNodeXri) {

		return fromComponents(contextNodeXri, arcXri, targetContextNodeXri);
	}

	/**
	 * Creates an XDI statement from a context node XRI and literal data.
	 * @param contextNodeXri The context node XRI
	 * @param literalData The literal data
	 * @return An XDI statement
	 */
	public static XDI3Statement fromLiteralComponents(final XDI3Segment contextNodeXri, final String literalData) {

		return fromComponents(contextNodeXri, XDIConstants.XRI_S_LITERAL, XDIUtil.stringToDataXriSegment(literalData));
	}

	/**
	 * Creates an XDI statement from an XRI segment in the form (subject/predicate/object)
	 * @param segment The XRI segment
	 * @return An XDI statement
	 */
	public static XDI3Statement fromXriSegment(XDI3Segment segment) throws Xdi2ParseException {

		XDI3SubSegment subSegment = segment.getFirstSubSegment();
		if (subSegment == null) throw new Xdi2ParseException("No subsegment found: " + segment.toString());

		XDI3XRef xref = subSegment.getXRef();
		if (xref == null) throw new Xdi2ParseException("No cross-reference found: " + segment.toString());

		XDI3Statement statement = xref.getStatement();
		if (statement == null) throw new Xdi2ParseException("No statement found: " + segment.toString());

		return statement;
	}

	/**
	 * Extracts a statement with a relative subject.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XDI3Statement relativeStatement(XDI3Statement statement, XDI3Segment base, boolean variablesInXri, boolean variablesInBase) {

		if (log.isTraceEnabled()) log.trace("relativeStatement(" + statement + "," + base + "," + variablesInXri + "," + variablesInBase + ")");

		XDI3Segment subject = XRIUtil.relativeXri(statement.getSubject(), base, variablesInXri, variablesInBase);
		XDI3Segment predicate = statement.getPredicate();
		XDI3Segment object = statement.getObject();

		if (subject == null) return null;

		return fromComponents(subject, predicate, object);
	}

	/**
	 * Extracts a statement with a relative subject.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XDI3Statement relativeStatement(XDI3Statement statement, XDI3Segment base) {

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
