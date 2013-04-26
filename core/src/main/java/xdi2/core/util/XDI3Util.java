package xdi2.core.util;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Various utility methods for working with XRI 3.0 syntax.
 * 
 * @author markus
 */
public final class XDI3Util {

	private static final Logger log = LoggerFactory.getLogger(XDI3Util.class);

	private XDI3Util() { }

	/**
	 * Checks if an XRI starts with a certain other XRI.
	 */
	public static boolean startsWith(XDI3Segment xri, XDI3Segment base, boolean variablesInXri, boolean variablesInBase) {

		if (log.isTraceEnabled()) log.trace("startsWith(" + xri + "," + base + "," + variablesInXri + "," + variablesInBase + ")");

		if (xri == null) return false;
		if (base == null) return true;

		int xriIndex = 0, baseIndex = 0;

		while (true) {

			if (baseIndex == base.getNumSubSegments()) return true;
			if (xriIndex == xri.getNumSubSegments()) return false;

			XDI3SubSegment xriSubSegment = xri.getSubSegment(xriIndex);
			XDI3SubSegment baseSubSegment = base.getSubSegment(baseIndex);

			// check variables

			if (variablesInXri && VariableUtil.isVariable(xriSubSegment)) {

				if (VariableUtil.matches(xriSubSegment, baseSubSegment)) {

					xriIndex++;
					baseIndex++;

					if (VariableUtil.isMultiple(xriSubSegment)) {

						while (baseIndex < base.getNumSubSegments() && VariableUtil.matches(xriSubSegment, baseSubSegment)) baseIndex++;
					}

					continue;
				} else {

					return false;
				}
			}

			if (variablesInBase && VariableUtil.isVariable(baseSubSegment)) {

				if (VariableUtil.matches(baseSubSegment, xriSubSegment)) {

					xriIndex++;
					baseIndex++;

					if (VariableUtil.isMultiple(baseSubSegment)) {

						while (xriIndex < xri.getNumSubSegments() && VariableUtil.matches(baseSubSegment, xriSubSegment)) xriIndex++;
					}

					continue;
				} else {

					return false;
				}
			}

			// no variables? just match the subsegment

			if (! (xriSubSegment.equals(baseSubSegment))) return false;

			xriIndex++;
			baseIndex++;
		}
	}

	/**
	 * Checks if an XRI starts with a certain other XRI.
	 */
	public static boolean startsWith(XDI3Segment xri, XDI3Segment base) {

		return startsWith(xri, base, false, false);
	}

	/**
	 * Checks if an XRI ends with a certain other XRI.
	 */
	public static boolean endsWith(XDI3Segment xri, XDI3Segment base, boolean variablesInXri, boolean variablesInBase) {

		if (log.isTraceEnabled()) log.trace("endsWith(" + xri + "," + base + "," + variablesInXri + "," + variablesInBase + ")");

		if (xri == null) return false;
		if (base == null) return true;

		int xriIndex = xri.getNumSubSegments() - 1, baseIndex = base.getNumSubSegments() - 1;

		while (true) {

			if (baseIndex == -1) return true;
			if (xriIndex == -1) return false;

			XDI3SubSegment xriSubSegment = xri.getSubSegment(xriIndex);
			XDI3SubSegment baseSubSegment = base.getSubSegment(baseIndex);

			// check variables

			if (variablesInXri && VariableUtil.isVariable(xriSubSegment)) {

				if (VariableUtil.matches(xriSubSegment, baseSubSegment)) {

					xriIndex--;
					baseIndex--;

					if (VariableUtil.isMultiple(xriSubSegment)) {

						while (baseIndex > -1 && VariableUtil.matches(xriSubSegment, baseSubSegment)) baseIndex--;
					}

					continue;
				} else {

					return false;
				}
			}

			if (variablesInBase && VariableUtil.isVariable(baseSubSegment)) {

				if (VariableUtil.matches(baseSubSegment, xriSubSegment)) {

					xriIndex--;
					baseIndex--;

					if (VariableUtil.isMultiple(baseSubSegment)) {

						while (xriIndex > -1 && VariableUtil.matches(baseSubSegment, xriSubSegment)) xriIndex--;
					}

					continue;
				} else {

					return false;
				}
			}

			// no variables? just match the subsegment

			if (! (xriSubSegment.equals(baseSubSegment))) return false;

			xriIndex--;
			baseIndex--;
		}
	}

	/**
	 * Checks if an XRI ends with a certain other XRI.
	 */
	public static boolean endsWith(XDI3Segment xri, XDI3Segment base) {

		return endsWith(xri, base, false, false);
	}

	/**
	 * Extracts an XRI's parent subsegment(s), counting either from the start or the end.
	 * For =a*b*c*d and 1, this returns =a
	 * For =a*b*c*d and -1, this returns =a*b*c
	 */
	public static XDI3Segment parentXri(XDI3Segment xri, int numSubSegments) {

		if (log.isTraceEnabled()) log.trace("parentXri(" + xri + "," + numSubSegments + ")");

		StringBuilder buffer = new StringBuilder();

		if (numSubSegments > 0) {

			for (int i = 0; i < numSubSegments; i++) buffer.append(xri.getSubSegment(i).toString());
		} else if (numSubSegments < 0) {

			for (int i = 0; i < xri.getNumSubSegments() - (- numSubSegments); i++) buffer.append(xri.getSubSegment(i).toString());
		} else {

			return null;
		}

		if (buffer.length() == 0) return null;

		return XDI3Segment.create(buffer.toString());
	}

	/**
	 * Extracts an XRI's local subsegment(s).
	 * For =a*b*c*d and 1, this returns *d
	 * For =a*b*c*d and -1, this returns *b*c*d
	 */
	public static XDI3Segment localXri(XDI3Segment xri, int numSubSegments) {

		if (log.isTraceEnabled()) log.trace("localXri(" + xri + "," + numSubSegments + ")");

		StringBuilder buffer = new StringBuilder();

		if (numSubSegments > 0) {

			for (int i = xri.getNumSubSegments() - numSubSegments; i < xri.getNumSubSegments(); i++) buffer.append(xri.getSubSegment(i).toString());
		} else if (numSubSegments < 0) {

			for (int i = (- numSubSegments); i < xri.getNumSubSegments(); i++) buffer.append(xri.getSubSegment(i).toString());
		} else {

			return null;
		}

		if (buffer.length() == 0) return null;

		return XDI3Segment.create(buffer.toString());
	}

	/**
	 * Creates an expanded XRI from a base XRI.
	 * E.g. for *c*d and =a*b, this returns =a*b*c*d
	 */
	public static XDI3Segment expandXri(XDI3Segment xri, XDI3Segment base) {

		if (log.isTraceEnabled()) log.trace("expandXri(" + xri + "," + base + ")");

		StringBuffer buffer = new StringBuffer();
		if (base != null && ! XDIConstants.XRI_S_ROOT.equals(base)) buffer.append(base.toString()); 
		if (xri != null && ! XDIConstants.XRI_S_ROOT.equals(xri)) buffer.append(xri.toString()); 

		if (buffer.length() == 0) buffer.append("()");

		return XDI3Segment.create(buffer.toString());
	}

	/**
	 * Creates a reduced XRI from a base XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XDI3Segment reduceXri(XDI3Segment xri, XDI3Segment base, boolean variablesInXri, boolean variablesInBase) {

		if (log.isTraceEnabled()) log.trace("reduceXri(" + xri + "," + base + "," + variablesInXri + "," + variablesInBase + ")");

		if (xri == null) return null;
		if (base == null || XDIConstants.XRI_S_ROOT.equals(base)) return xri;

		int xriIndex = 0, baseIndex = 0;

		while (true) {

			if (baseIndex == base.getNumSubSegments()) break;
			if (xriIndex == xri.getNumSubSegments()) return null;

			XDI3SubSegment xriSubSegment = xri.getSubSegment(xriIndex);
			XDI3SubSegment baseSubSegment = base.getSubSegment(baseIndex);

			// check variables

			if (variablesInXri && VariableUtil.isVariable(xriSubSegment)) {

				if (VariableUtil.matches(xriSubSegment, baseSubSegment)) {

					xriIndex++;
					baseIndex++;

					if (VariableUtil.isMultiple(xriSubSegment) && baseIndex < base.getNumSubSegments()) {

						baseSubSegment = base.getSubSegment(baseIndex);

						while (VariableUtil.matches(xriSubSegment, baseSubSegment)) {

							if (++baseIndex == base.getNumSubSegments()) break;
							baseSubSegment = base.getSubSegment(baseIndex);
						}
					}

					continue;
				} else {

					return null;
				}
			}

			if (variablesInBase && VariableUtil.isVariable(baseSubSegment)) {

				if (VariableUtil.matches(baseSubSegment, xriSubSegment)) {

					xriIndex++;
					baseIndex++;

					if (VariableUtil.isMultiple(baseSubSegment) && xriIndex < xri.getNumSubSegments()) {

						xriSubSegment = xri.getSubSegment(xriIndex);

						while (VariableUtil.matches(baseSubSegment, xriSubSegment)) {

							if (++xriIndex == xri.getNumSubSegments()) break;
							xriSubSegment = xri.getSubSegment(xriIndex);
						}
					}

					continue;
				} else {

					return null;
				}
			}

			// no variables? just match the subsegment

			if (! (xriSubSegment.equals(baseSubSegment))) return null;

			xriIndex++;
			baseIndex++;
		}

		StringBuilder buffer = new StringBuilder();

		for (; xriIndex<xri.getNumSubSegments(); xriIndex++) {

			buffer.append(xri.getSubSegment(xriIndex).toString());
		}

		if (buffer.length() == 0) return null;

		return XDI3Segment.create(buffer.toString());
	}

	/**
	 * Creates a reduced XRI from a base XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XDI3Segment reduceXri(XDI3Segment xri, XDI3Segment base) {

		return reduceXri(xri, base, false, false);
	}

	/**
	 * Replaces all occurences of a subsegment with a segment.
	 */
	public static XDI3Segment replaceXri(XDI3Segment xri, XDI3SubSegment oldXri, XDI3Segment newXri) {

		StringBuffer buffer = new StringBuffer();

		for (XDI3SubSegment subSegment : xri.getSubSegments()) {

			if (subSegment.equals(oldXri)) 
				buffer.append(newXri.toString());
			else
				buffer.append(subSegment.toString());
		}

		return XDI3Segment.create(buffer.toString());
	}

	/*
	 * Helper classes
	 */

	public static final Comparator<? super XDI3Segment> XDI3Segment_ASCENDING_COMPARATOR = new Comparator<XDI3Segment>() {

		@Override
		public int compare(XDI3Segment o1, XDI3Segment o2) {

			if (o1.getNumSubSegments() < o2.getNumSubSegments()) return -1;
			if (o1.getNumSubSegments() > o2.getNumSubSegments()) return 1;

			return o1.compareTo(o2);
		}
	};

	public static final Comparator<? super XDI3Segment> XDI3Segment_DESCENDING_COMPARATOR = new Comparator<XDI3Segment>() {

		@Override
		public int compare(XDI3Segment o1, XDI3Segment o2) {

			if (o1.getNumSubSegments() > o2.getNumSubSegments()) return -1;
			if (o1.getNumSubSegments() < o2.getNumSubSegments()) return 1;

			return o1.compareTo(o2);
		}
	};
}
