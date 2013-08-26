package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.impl.AbstractLiteral;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * Various utility methods for working with statements.
 * 
 * @author markus
 */
public final class StatementUtil {

	private static final Logger log = LoggerFactory.getLogger(StatementUtil.class);

	private StatementUtil() { }

	/**
	 * Concats an XRI and a statement into a new statement.
	 * E.g. for *c*d&/&/... and =a*b, this returns =a*b*c*d&/&/...
	 */
	public static XDI3Statement concatXriStatement(XDI3Segment xri, XDI3Statement statement) {

		if (log.isTraceEnabled()) log.trace("concatXriStatement(" + xri + "," + statement + ")");

		XDI3Segment subject = XDI3Util.concatXris(xri, statement.getSubject());
		XDI3Segment predicate = statement.getPredicate();
		Object object = (statement.isRelationStatement() && ! statement.hasInnerRootStatement()) ? XDI3Util.concatXris(xri, (XDI3Segment) statement.getObject()) : statement.getObject();

		return XDI3Statement.fromComponents(subject, predicate, object);
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

		return XDI3Statement.fromComponents(subject, predicate, object);
	}

	/**
	 * Removes a start XRI from a statement.
	 * E.g. for =a*b*c*d&/&/... and =a*b, this returns *c*d&/&/...
	 */
	public static XDI3Statement removeStartXriStatement(XDI3Statement statement, XDI3Segment start, boolean removeFromTargetContextNodeXri) {

		return removeStartXriStatement(statement, start, removeFromTargetContextNodeXri, false, false);
	}

	public static String statementObjectToStringg(XDI3Statement statement) {

		if (statement.isLiteralStatement()) {

			return AbstractLiteral.literalDataToString(statement.getObject());
		} else {

			return ((XDI3Segment) statement.getObject()).toString();
		}
	}
}
