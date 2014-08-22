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
	 * Checks if a subsegment is a valid XDI variable.
	 * @param variable The subsegment to check.
	 * @return True if the subsegment is a valid XDI variable.
	 */
	public static boolean isVariable(XDIArc variable) {

		return variable.hasXRef() &&
				XDIConstants.XS_VARIABLE.equals(variable.getXRef().getXs()) &&
				( variable.getXRef().isEmpty() || variable.getXRef().hasAddress() || variable.getXRef().hasLiteral() );
	}

	public static boolean isVariable(XDIAddress variable) {

		if (variable.getNumArcs() != 1) return false;

		return isVariable(variable.getFirstArc());
	}

	public static List<XDIArc> getSubSegments(XDIArc variable) {

		if (! isVariable(variable)) return new ArrayList<XDIArc> ();
		if (! variable.getXRef().hasAddress()) return new ArrayList<XDIArc> ();

		XDIAddress segment = variable.getXRef().getAddress();

		while (segment.getFirstArc().hasXRef()) {

			segment = segment.getFirstArc().getXRef().getAddress();
			if (segment == null) return new ArrayList<XDIArc> ();
		}

		return segment.getArcs();
	}

	public static String getXs(XDIArc variable) {

		if (! isVariable(variable)) return null;
		if (! variable.getXRef().hasAddress()) return null;

		XDIXRef xref = variable.getXRef().getAddress().getFirstArc().getXRef();
		if (xref == null) return null;

		if (XDIConstants.XS_VARIABLE.equals(xref.getXs())) {

			if (! xref.hasAddress()) return null;

			xref = xref.getAddress().getFirstArc().getXRef();
			if (xref == null) return null;
		}

		return xref.getXs();
	}

	/**
	 * Checks if a variable matches multiple subsegments.
	 * @param variable The variable.
	 * @return True, if the variable matches multiple subsegments.
	 */
	public static boolean isMultiple(XDIArc variable) {

		if (! isVariable(variable)) return false;
		if (! variable.getXRef().hasAddress()) return false;

		if (! variable.getXRef().getAddress().getFirstArc().hasXRef()) return false;
		if (! XDIConstants.XS_VARIABLE.equals(variable.getXRef().getAddress().getFirstArc().getXRef().getXs())) return false;

		return true;
	}

	/**
	 * Checks if a variables matches a subsegment.
	 * @param variable The variable.
	 * @param subSegment The subsegment to match the variable against.
	 * @return True, if the variable matches the subsegment.
	 */
	public static boolean matches(XDIArc variable, XDIArc subSegment) {

		List<XDIArc> variableSubSegments = getSubSegments(variable);
		String variableXs = getXs(variable);

		if (log.isTraceEnabled()) log.trace("Matching variable " + variable + " against subsegment " + subSegment + " (variableSubSegments=" + variableSubSegments + ", variableXs=" + variableXs + ")");

		if (variableXs != null) {

			if (! subSegment.hasXRef()) {

				if (log.isTraceEnabled()) log.trace("Variable requires xs " + variableXs + ", but subsegment has no xs. No match.");
				return false;
			}

			if (! variableXs.equals(subSegment.getXRef().getXs())) {

				if (log.isTraceEnabled()) log.trace("Variable xs " + variableXs + " does not match subsegment xs " + subSegment.getXRef().getXs() + ". No match.");
				return false;
			}
		}

		if (subSegment.hasXRef() && ! subSegment.hasCs()) {

			if (variableXs == null) {

				if (log.isTraceEnabled()) log.trace("Variable requires no xs, but subsegment has xs " + subSegment.getXRef().getXs() + ". No match.");
				return false;
			}

			if (! subSegment.getXRef().hasAddress()) {

				if (log.isTraceEnabled()) log.trace("Subsegment has no inner subsegment. No match.");
				return false;
			}

			subSegment = subSegment.getXRef().getAddress().getFirstArc();
		}

		if (variableSubSegments.size() == 0) {

			if (log.isTraceEnabled()) log.trace("Variable has no subsegments. Match.");
			return true;
		}

		if (! subSegment.hasCs()) {

			if (log.isTraceEnabled()) log.trace("Subsegment has no cs. No match.");
			return false;
		}

		for (XDIArc variableSubSegment : variableSubSegments) {

			if (log.isTraceEnabled()) log.trace("Trying to match variable subsegment " + variableSubSegment + ".");

			if (! variableSubSegment.hasCs()) {

				if (log.isTraceEnabled()) log.trace("Variable subsegment has no cs. Continuing.");
				continue;
			}

			if (variableSubSegment.isClassXs() && ! subSegment.isClassXs()) continue;
			if (variableSubSegment.isAttributeXs() && ! subSegment.isAttributeXs()) continue;

			if (variableSubSegment.getCs().equals(subSegment.getCs())) {

				if (log.isTraceEnabled()) log.trace("Variable cs " + variableSubSegment.getCs() + " is equal to subsegment cs. Match.");
				return true;
			}
		}

		if (log.isTraceEnabled()) log.trace("No match with any subsegment. No Match.");
		return false;
	}
}
