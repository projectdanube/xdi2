package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * Removes a start XRI from a statement.
	 * E.g. for =a*b*c*d&/&/... and =a*b, this returns *c*d&/&/...
	 */
	public static XDI3Statement removeStartXriStatement(final XDI3Statement statementXri, final XDI3Segment start, final boolean removeFromTargetContextNodeXri, final boolean variablesInXri, boolean variablesInStart) {

		if (statementXri == null) throw new NullPointerException();
		if (start == null) throw new NullPointerException();

		XDI3Statement result = null;

		try {

			XDI3Segment subject;
			XDI3Segment predicate;
			Object object;

			// subject

			subject = XDI3Util.removeStartXri(statementXri.getSubject(), start, variablesInXri, variablesInStart);
			if (subject == null) { result = null; return result; }

			// predicate

			predicate = statementXri.getPredicate();

			// object

			if (statementXri.isRelationStatement() && removeFromTargetContextNodeXri) {

				object = XDI3Util.removeStartXri((XDI3Segment) statementXri.getObject(), start, variablesInXri, variablesInStart);
				if (object == null) { result = null; return result; }
			} else {

				object = statementXri.getObject();
			}

			{ result = XDI3Statement.fromComponents(subject, predicate, object); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeStartXriStatement(" + statementXri + "," + start + "," + removeFromTargetContextNodeXri + "," + variablesInXri + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Removes a start XRI from a statement.
	 * E.g. for =a*b*c*d&/&/... and =a*b, this returns *c*d&/&/...
	 */
	public static XDI3Statement removeStartXriStatement(final XDI3Statement statementXri, final XDI3Segment start, final boolean removeFromTargetContextNodeXri) {

		return removeStartXriStatement(statementXri, start, removeFromTargetContextNodeXri, false, false);
	}

	/**
	 * Concats an XRI and a statement into a new statement.
	 * E.g. for *c*d&/&/... and =a*b, this returns =a*b*c*d&/&/...
	 */
	public static XDI3Statement concatXriStatement(final XDI3Segment xri, final XDI3Statement statementXri, final boolean concatTargetContextNodeXri) {

		if (statementXri == null) throw new NullPointerException();

		XDI3Statement result = null;

		try {

			XDI3Segment subject = XDI3Util.concatXris(xri, statementXri.getSubject());
			XDI3Segment predicate = statementXri.getPredicate();
			Object object = (statementXri.isRelationStatement() && ! statementXri.hasInnerRootStatement() && concatTargetContextNodeXri) ? XDI3Util.concatXris(xri, (XDI3Segment) statementXri.getObject()) : statementXri.getObject();

			{ result = XDI3Statement.fromComponents(subject, predicate, object); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("concatXriStatement(" + xri + "," + statementXri + "," + concatTargetContextNodeXri + ") --> " + result);
		}
	}
}
