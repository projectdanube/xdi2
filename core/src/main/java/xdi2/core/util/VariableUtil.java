package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

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
				XDI3Constants.CF_VARIABLE.equals(variable.getXRef().getCf()) &&
				( variable.getXRef().isEmpty() || variable.getXRef().hasSegment() || variable.getXRef().hasLiteral() );
	}

	public static boolean isVariable(XDI3Segment variable) {

		if (variable.getNumSubSegments() != 1) return false;

		return isVariable(variable.getFirstSubSegment());
	}

	/**
	 * Get a variable's context function.
	 * @param variable The variable.
	 * @return The variable's context function.
	 */
	public static String getCf(XDI3SubSegment variable) {

		if (! isVariable(variable)) return null;
		if (! variable.getXRef().hasSegment()) return null;

		XDI3Segment innerSegment = variable.getXRef().getSegment();
		XDI3SubSegment innerSubSegment = innerSegment.getFirstSubSegment();
		if (! innerSubSegment.hasXRef()) return null;

		return innerSubSegment.getXRef().getCf();
	}

	/**
	 * Get a variable's context symbol(s).
	 * @param variable The variable.
	 * @return The variable's context symbol(s).
	 */
	public static String getCss(XDI3SubSegment variable) {

		if (! isVariable(variable)) return null;

		String css = "" ;

		if (variable.getXRef().hasSegment()) {

			XDI3Segment innerSegment = variable.getXRef().getSegment();
			XDI3SubSegment innerSubSegment = innerSegment.getFirstSubSegment();

			if (innerSubSegment.hasXRef()) {

				if (innerSubSegment.getXRef().hasSegment()) {

					XDI3Segment innerInnerSegment = innerSubSegment.getXRef().getSegment();

					for (int i=0; i<innerInnerSegment.getNumSubSegments(); i++) {

						css += innerInnerSegment.getSubSegment(i).getCs();
					}
				}
			} else {

				for (int i=0; i<innerSegment.getNumSubSegments(); i++) {

					css += innerSegment.getSubSegment(i).getCs();
				}
			}
		}

		return css;
	}

	/**
	 * Checks if a variable matches multiple subsegments.
	 * @param variable The variable.
	 * @return True, if the variable matches multiple subsegments.
	 */
	public static boolean isMultiple(XDI3SubSegment variable) {

		if (! isVariable(variable)) return false;
		if (! variable.getXRef().hasSegment()) return false;

		XDI3Segment innerSegment = variable.getXRef().getSegment();
		if (innerSegment.getNumSubSegments() < 2) return false;

		return "{}".equals(innerSegment.getLastSubSegment().toString());
	}

	/**
	 * Checks if a variables matches a subsegment.
	 * @param variable The variable.
	 * @param subSegment The subsegment to match the variable against.
	 * @return True, if the variable matches the subsegment.
	 */
	public static boolean matches(XDI3SubSegment variable, XDI3SubSegment subSegment) {

		String cf = getCf(variable);
		String css = getCss(variable);

		if (log.isDebugEnabled()) log.debug("Matching variable " + variable + " against subsegment " + subSegment + " (cf=" + cf + ", css=" + css + ")");

		if (cf != null) {

			boolean validCf = false;

			/*			if (XDI3Constants.CF_ROOT.equals(cf) && XdiRoot.isRootArcXri(subSegment)) validCf = true;
			if (XDI3Constants.CF_ENTITY_SINGLETON.equals(cf) && XdiEntity.isValidArcXri(subSegment)) validCf = true;
			if (XDI3Constants.CF_ROOT.equals(cf) && XdiRoot.isRootArcXri(subSegment)) validCf = true;
			if (XDI3Constants.CF_ROOT.equals(cf) && XdiRoot.isRootArcXri(subSegment)) validCf = true;
			if (XDI3Constants.CF_ROOT.equals(cf) && XdiRoot.isRootArcXri(subSegment)) validCf = true;*/

			if (! validCf) throw new RuntimeException("fix me");
		}

		if (css.length() > 0) {

			Character cs = null;

			if (subSegment.hasCs()) {

				cs = subSegment.getCs();
			} else {

				if (subSegment.hasXRef() && subSegment.getXRef().hasSegment()) {

					cs = subSegment.getXRef().getSegment().getFirstSubSegment().getCs();
				}
			}

			if (cs == null) return false;
			if (css.indexOf(cs.charValue()) == -1) return false;
		}

		return true;
	}
}
