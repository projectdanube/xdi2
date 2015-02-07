package xdi2.core.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIXRef;

/**
 * Utility methods for working with variables.
 * 
 * @author markus
 */
public final class VariableUtil {

	private static final Logger log = LoggerFactory.getLogger(VariableUtil.class);

	private VariableUtil() { }

	/**
	 * Checks if an arc is a valid XDI variable.
	 * @param variable The arc to check.
	 * @return True if the arc is a valid XDI variable.
	 */
	public static boolean isVariable(XDIArc variable) {

		return variable.hasXRef() &&
				XDIConstants.XS_VARIABLE.equals(variable.getXRef().getXs()) &&
				( variable.getXRef().isEmpty() || variable.getXRef().hasXDIAddress() || variable.getXRef().hasLiteral() );
	}

	public static boolean isVariable(XDIAddress variable) {

		if (variable.getNumXDIArcs() != 1) return false;

		return isVariable(variable.getFirstXDIArc());
	}

	public static List<XDIArc> getArcs(XDIArc variable) {

		if (! isVariable(variable)) return new ArrayList<XDIArc> ();
		if (! variable.getXRef().hasXDIAddress()) return new ArrayList<XDIArc> ();

		XDIAddress XDIaddress = variable.getXRef().getXDIAddress();

		while (XDIaddress.getFirstXDIArc().hasXRef()) {

			XDIaddress = XDIaddress.getFirstXDIArc().getXRef().getXDIAddress();
			if (XDIaddress == null) return new ArrayList<XDIArc> ();
		}

		return XDIaddress.getXDIArcs();
	}

	public static String getXs(XDIArc variable) {

		if (! isVariable(variable)) return null;
		if (! variable.getXRef().hasXDIAddress()) return null;

		XDIXRef xref = variable.getXRef().getXDIAddress().getFirstXDIArc().getXRef();
		if (xref == null) return null;

		if (XDIConstants.XS_VARIABLE.equals(xref.getXs())) {

			if (! xref.hasXDIAddress()) return null;

			xref = xref.getXDIAddress().getFirstXDIArc().getXRef();
			if (xref == null) return null;
		}

		return xref.getXs();
	}

	/**
	 * Checks if a variable matches multiple arcs.
	 * @param variable The variable.
	 * @return True, if the variable matches multiple arcs.
	 */
	public static boolean isMultiple(XDIArc variable) {

		if (! isVariable(variable)) return false;
		if (! variable.getXRef().hasXDIAddress()) return false;

		if (! variable.getXRef().getXDIAddress().getFirstXDIArc().hasXRef()) return false;
		if (! XDIConstants.XS_VARIABLE.equals(variable.getXRef().getXDIAddress().getFirstXDIArc().getXRef().getXs())) return false;

		return true;
	}

	/**
	 * Checks if a variables matches an arc.
	 * @param variable The variable.
	 * @param arc The arc to match the variable against.
	 * @return True, if the variable matches the arc.
	 */
	public static boolean matches(XDIArc variable, XDIArc XDIarc) {

		List<XDIArc> variableArcs = getArcs(variable);
		String variableXs = getXs(variable);

		if (log.isTraceEnabled()) log.trace("Matching variable " + variable + " against arc " + XDIarc + " (variableArcs=" + variableArcs + ", variableXs=" + variableXs + ")");

		if (variableXs != null) {

			if (! XDIarc.hasXRef()) {

				if (log.isTraceEnabled()) log.trace("Variable requires xs " + variableXs + ", but arc has no xs. No match.");
				return false;
			}

			if (! variableXs.equals(XDIarc.getXRef().getXs())) {

				if (log.isTraceEnabled()) log.trace("Variable xs " + variableXs + " does not match arc xs " + XDIarc.getXRef().getXs() + ". No match.");
				return false;
			}
		}

		if (XDIarc.hasXRef() && ! XDIarc.hasCs()) {

			if (variableXs == null) {

				if (log.isTraceEnabled()) log.trace("Variable requires no xs, but arc has xs " + XDIarc.getXRef().getXs() + ". No match.");
				return false;
			}

			if (! XDIarc.getXRef().hasXDIAddress()) {

				if (log.isTraceEnabled()) log.trace("Arc has no inner address. No match.");
				return false;
			}

			XDIarc = XDIarc.getXRef().getXDIAddress().getFirstXDIArc();
		}

		if (variableArcs.size() == 0) {

			if (log.isTraceEnabled()) log.trace("Variable has no arcs. Match.");
			return true;
		}

		if (! XDIarc.hasCs()) {

			if (log.isTraceEnabled()) log.trace("Arc has no cs. No match.");
			return false;
		}

		for (XDIArc variableArc : variableArcs) {

			if (log.isTraceEnabled()) log.trace("Trying to match variable arc " + variableArc + ".");

			if (! variableArc.hasCs()) {

				if (log.isTraceEnabled()) log.trace("Variable arc has no cs. Continuing.");
				continue;
			}

			if (variableArc.isCollection() && ! XDIarc.isCollection()) continue;
			if (variableArc.isAttribute() && ! XDIarc.isAttribute()) continue;

			if (variableArc.getCs().equals(XDIarc.getCs())) {

				if (log.isTraceEnabled()) log.trace("Variable cs " + variableArc.getCs() + " is equal to arc cs. Match.");
				return true;
			}
		}

		if (log.isTraceEnabled()) log.trace("No match with any arc. No Match.");
		return false;
	}
}
