package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

/**
 * Various utility methods for working with statements.
 * 
 * @author markus
 */
public final class XDIStatementUtil {

	private static final Logger log = LoggerFactory.getLogger(XDIStatementUtil.class);

	private XDIStatementUtil() { }

	/**
	 * Removes a start address from a statement.
	 * E.g. for =a*b*c*d/&/... and =a*b, this returns *c*d/&/...
	 */
	public static XDIStatement removeStartXDIStatement(final XDIStatement statement, final XDIAddress start, final boolean variablesInAddress, boolean variablesInStart) {

		if (statement == null) throw new NullPointerException();
		if (start == null) throw new NullPointerException();

		XDIStatement result = null;

		try {

			XDIAddress subject;
			Object predicate;
			Object object;

			// subject

			subject = XDIAddressUtil.removeStartXDIAddress(statement.getSubject(), start, variablesInAddress, variablesInStart);
			if (subject == null) { result = null; return result; }

			// predicate

			predicate = statement.getPredicate();

			// object

			object = statement.getObject();

			{ result = XDIStatement.fromComponents(subject, predicate, object); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeStartXDIStatement(" + statement + "," + start + "," + variablesInAddress + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Removes a start address from a statement.
	 * E.g. for =a*b*c*d/&/... and =a*b, this returns *c*d/&/...
	 */
	public static XDIStatement removeStartXDIStatement(final XDIStatement statement, final XDIAddress start) {

		return removeStartXDIStatement(statement, start, false, false);
	}

	/**
	 * Concats an address and a statement into a new statement.
	 * E.g. for *c*d/&/... and =a*b, this returns =a*b*c*d/&/...
	 */
	public static XDIStatement concatXDIStatement(final XDIAddress XDIaddress, final XDIStatement statement) {

		if (statement == null) throw new NullPointerException();

		XDIStatement result = null;

		try {

			XDIAddress subject;
			Object predicate;
			Object object;

			// subject

			subject = XDIAddressUtil.concatXDIAddresses(XDIaddress, statement.getSubject());

			// predicate

			predicate = statement.getPredicate();

			// object

			object = statement.getObject();

			{ result = XDIStatement.fromComponents(subject, predicate, object); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("concatXDIStatement(" + XDIaddress + "," + statement + ") --> " + result);
		}
	}
}
