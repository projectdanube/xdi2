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
				XDI3Constants.XS_VARIABLE.equals(variable.getXRef().getXs()) &&
				( variable.getXRef().isEmpty() || variable.getXRef().hasSegment() || variable.getXRef().hasLiteral() );
	}

	public static boolean isVariable(XDI3Segment variable) {

		if (variable.getNumSubSegments() != 1) return false;

		return isVariable(variable.getFirstSubSegment());
	}

	public static String getCss(XDI3SubSegment variable) {

		if (! isVariable(variable)) return null;

		String css = "" ;

		if (variable.getXRef().hasSegment()) {

			XDI3Segment innerSegment = variable.getXRef().getSegment();
			XDI3SubSegment innerSubSegment = innerSegment.getFirstSubSegment();

			if (innerSubSegment.hasCs()) {

				for (int i=0; i<innerSegment.getNumSubSegments(); i++) {

					css += innerSegment.getSubSegment(i).getCs();
				}
			} else if (innerSubSegment.hasXRef()) {

				if (innerSubSegment.getXRef().hasSegment()) {

					XDI3Segment innerInnerSegment = innerSubSegment.getXRef().getSegment();

					for (int i=0; i<innerInnerSegment.getNumSubSegments(); i++) {

						css += innerInnerSegment.getSubSegment(i).getCs();
					}
				}
			}
		}

		return css;
	}

	public static boolean getSingleton(XDI3SubSegment variable) {

		if (! isVariable(variable)) return false;
		if (! variable.getXRef().hasSegment()) return false;

		XDI3Segment innerSegment = variable.getXRef().getSegment();
		XDI3SubSegment innerSubSegment = innerSegment.getFirstSubSegment();

		return innerSubSegment.isSingleton();
	}

	public static boolean getAttribute(XDI3SubSegment variable) {

		if (! isVariable(variable)) return false;
		if (! variable.getXRef().hasSegment()) return false;

		XDI3Segment innerSegment = variable.getXRef().getSegment();
		XDI3SubSegment innerSubSegment = innerSegment.getFirstSubSegment();

		return innerSubSegment.isAttribute();
	}

	public static String getXs(XDI3SubSegment variable) {

		if (! isVariable(variable)) return null;
		if (! variable.getXRef().hasSegment()) return null;

		XDI3Segment innerSegment = variable.getXRef().getSegment();
		XDI3SubSegment innerSubSegment = innerSegment.getFirstSubSegment();
		if (innerSubSegment.hasCs()) return null;
		if (! innerSubSegment.hasXRef()) return null;

		return innerSubSegment.getXRef().getXs();
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

		return innerSegment.toString().length() > 2 && innerSegment.toString().endsWith("{}");
	}

	/**
	 * Checks if a variables matches a subsegment.
	 * @param variable The variable.
	 * @param subSegment The subsegment to match the variable against.
	 * @return True, if the variable matches the subsegment.
	 */
	public static boolean matches(XDI3SubSegment variable, XDI3SubSegment subSegment) {

		String css = getCss(variable);
		boolean singleton = getSingleton(variable);
		boolean attribute = getAttribute(variable);
		String xs = getXs(variable);

		if (log.isDebugEnabled()) log.debug("Matching variable " + variable + " against subsegment " + subSegment + " (css=" + css + ", singleton=" + singleton + ", attribute=" + attribute + ", xs=" + xs + ")");

		if (xs != null) {

			if (! subSegment.hasXRef()) return false;

			if (! xs.equals(subSegment.getXRef().getXs())) return false;
		}

		if (singleton) {
			
			if (! subSegment.isSingleton()) return false;
		}

		if (attribute) {
			
			if (! subSegment.isAttribute()) return false;
		}
		
		if (css.length() > 0) {

			if (! subSegment.hasCs()) return false;

			if (css.indexOf(subSegment.getCs().charValue()) == -1) return false;
		}

		return true;
	}
}
