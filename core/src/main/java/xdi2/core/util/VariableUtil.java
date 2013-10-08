package xdi2.core.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

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
	public static boolean isVariable(XDI3SubSegment variable) {

		return variable.hasXRef() &&
				XDIConstants.XS_VARIABLE.equals(variable.getXRef().getXs()) &&
				( variable.getXRef().isEmpty() || variable.getXRef().hasSegment() || variable.getXRef().hasLiteral() );
	}

	public static boolean isVariable(XDI3Segment variable) {

		if (variable.getNumSubSegments() != 1) return false;

		return isVariable(variable.getFirstSubSegment());
	}

	public static List<XDI3SubSegment> getSubSegments(XDI3SubSegment variable) {

		if (! isVariable(variable)) return new ArrayList<XDI3SubSegment> ();
		if (! variable.getXRef().hasSegment()) return new ArrayList<XDI3SubSegment> ();

		XDI3Segment segment = variable.getXRef().getSegment();

		while (segment.getFirstSubSegment().hasXRef()) {

			segment = segment.getFirstSubSegment().getXRef().getSegment();
			if (segment == null) return new ArrayList<XDI3SubSegment> ();
		}

		return segment.getSubSegments();
	}

	public static String getXs(XDI3SubSegment variable) {

		if (! isVariable(variable)) return null;
		if (! variable.getXRef().hasSegment()) return null;

		XDI3XRef xref = variable.getXRef().getSegment().getFirstSubSegment().getXRef();
		if (xref == null) return null;

		if (XDIConstants.XS_VARIABLE.equals(xref.getXs())) {

			if (! xref.hasSegment()) return null;

			xref = xref.getSegment().getFirstSubSegment().getXRef();
			if (xref == null) return null;
		}

		return xref.getXs();
	}

	/**
	 * Checks if a variable matches multiple subsegments.
	 * @param variable The variable.
	 * @return True, if the variable matches multiple subsegments.
	 */
	public static boolean isMultiple(XDI3SubSegment variable) {

		if (! isVariable(variable)) return false;
		if (! variable.getXRef().hasSegment()) return false;

		if (! variable.getXRef().getSegment().getFirstSubSegment().hasXRef()) return false;
		if (! XDIConstants.XS_VARIABLE.equals(variable.getXRef().getSegment().getFirstSubSegment().getXRef().getXs())) return false;

		return true;
	}

	/**
	 * Checks if a variables matches a subsegment.
	 * @param variable The variable.
	 * @param subSegment The subsegment to match the variable against.
	 * @return True, if the variable matches the subsegment.
	 */
	public static boolean matches(XDI3SubSegment variable, XDI3SubSegment subSegment) {

		List<XDI3SubSegment> variableSubSegments = getSubSegments(variable);
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

			if (! subSegment.getXRef().hasSegment()) {

				if (log.isTraceEnabled()) log.trace("Subsegment has no inner subsegment. No match.");
				return false;
			}

			subSegment = subSegment.getXRef().getSegment().getFirstSubSegment();
		}

		if (variableSubSegments.size() == 0) {

			if (log.isTraceEnabled()) log.trace("Variable has no subsegments. Match.");
			return true;
		}

		if (! subSegment.hasCs()) {

			if (log.isTraceEnabled()) log.trace("Subsegment has no cs. No match.");
			return false;
		}

		for (XDI3SubSegment variableSubSegment : variableSubSegments) {

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
