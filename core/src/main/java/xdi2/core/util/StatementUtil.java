package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static XDI3Statement fromComponents(final XDI3Segment subject, final XDI3Segment predicate, final Object object) {

		return XDI3Statement.create("" + subject + "/" + predicate + "/" + StatementUtil.statementObjectToString(object));
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

		return fromComponents(contextNodeXri, XDIConstants.XRI_S_LITERAL, literalData);
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
	 * Concats an XRI and a statement into a new statement.
	 * E.g. for *c*d&/&/... and =a*b, this returns =a*b*c*d&/&/...
	 */
	public static XDI3Statement concatXriStatement(XDI3Segment xri, XDI3Statement statement) {

		if (log.isTraceEnabled()) log.trace("concatXriStatement(" + xri + "," + statement + ")");

		XDI3Segment subject = XDI3Util.concatXris(xri, statement.getSubject());
		XDI3Segment predicate = statement.getPredicate();
		Object object = (statement.isRelationStatement() && ! statement.hasInnerRootStatement()) ? XDI3Util.concatXris(xri, (XDI3Segment) statement.getObject()) : statement.getObject();

		return fromComponents(subject, predicate, object);
	}

	/**
	 * Removes a start XRI from a statement.
	 * E.g. for =a*b*c*d&/&/... and =a*b, this returns *c*d&/&/...
	 */
	public static XDI3Statement removeStartXriStatement(XDI3Statement statement, XDI3Segment start, boolean removeFromTargetContextNodeXri, boolean variablesInXri, boolean variablesInStart) {

		if (log.isTraceEnabled()) log.trace("removeStartXriStatement(" + statement + "," + start + "," + removeFromTargetContextNodeXri + "," + variablesInXri + "," + variablesInStart + ")");

		XDI3Segment subject;
		XDI3Segment predicate;
		Object object;

		// subject

		subject = XDI3Util.removeStartXri(statement.getSubject(), start, variablesInXri, variablesInStart);
		if (subject == null) return null;

		// predicate

		predicate = statement.getPredicate();

		// object

		if (statement.isRelationStatement() && removeFromTargetContextNodeXri) {

			object = XDI3Util.removeStartXri((XDI3Segment) statement.getObject(), start, variablesInXri, variablesInStart);
			if (object == null) return null;
		} else {

			object = statement.getObject();
		}

		return fromComponents(subject, predicate, object);
	}

	/**
	 * Removes a start XRI from a statement.
	 * E.g. for =a*b*c*d&/&/... and =a*b, this returns *c*d&/&/...
	 */
	public static XDI3Statement removeStartXriStatement(XDI3Statement statement, XDI3Segment start, boolean removeFromTargetContextNodeXri) {

		return removeStartXriStatement(statement, start, removeFromTargetContextNodeXri, false, false);
	}

	public static String statementObjectToString(Object object) {

		if (object instanceof String) {

			return "\"" + ((String) object).replace("\"", "\\\"") + "\"";
		} else {

			return object.toString();
		}
	}
}
