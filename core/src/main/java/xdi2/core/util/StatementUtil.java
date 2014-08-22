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
public final class StatementUtil {

	private static final Logger log = LoggerFactory.getLogger(StatementUtil.class);

	private StatementUtil() { }

	/**
	 * Removes a start address from a statement.
	 * E.g. for =a*b*c*d&/&/... and =a*b, this returns *c*d&/&/...
	 */
	public static XDIStatement removeStartAddressStatement(final XDIStatement statement, final XDIAddress start, final boolean variablesInAddress, boolean variablesInStart) {

		if (statement == null) throw new NullPointerException();
		if (start == null) throw new NullPointerException();

		XDIStatement result = null;

		try {

			XDIAddress subject;
			XDIAddress predicate;
			Object object;

			// subject

			subject = AddressUtil.removeStartAddress(statement.getSubject(), start, variablesInAddress, variablesInStart);
			if (subject == null) { result = null; return result; }

			// predicate

			predicate = statement.getPredicate();

			// object

			object = statement.getObject();

			{ result = XDIStatement.fromComponents(subject, predicate, object); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeStartAddressStatement(" + statement + "," + start + "," + variablesInAddress + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Removes a start address from a statement.
	 * E.g. for =a*b*c*d&/&/... and =a*b, this returns *c*d&/&/...
	 */
	public static XDIStatement removeStartAddressStatement(final XDIStatement statement, final XDIAddress start) {

		return removeStartAddressStatement(statement, start, false, false);
	}

	/**
	 * Concats an address and a statement into a new statement.
	 * E.g. for *c*d&/&/... and =a*b, this returns =a*b*c*d&/&/...
	 */
	public static XDIStatement concatAddressStatement(final XDIAddress address, final XDIStatement statement) {

		if (statement == null) throw new NullPointerException();

		XDIStatement result = null;

		try {

			XDIAddress subject;
			XDIAddress predicate;
			Object object;

			// subject

			subject = AddressUtil.concatAddresses(address, statement.getSubject());

			// predicate

			predicate = statement.getPredicate();

			// object

			object = statement.getObject();

			{ result = XDIStatement.fromComponents(subject, predicate, object); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("concatAddressStatement(" + address + "," + statement + ") --> " + result);
		}
	}
}
