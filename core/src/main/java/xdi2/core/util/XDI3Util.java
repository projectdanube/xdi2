package xdi2.core.util;

import java.util.Arrays;
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
	public static XDI3Segment startsWith(XDI3Segment xri, XDI3Segment startXri, boolean variablesInXri, boolean variablesInStart) {

		if (log.isTraceEnabled()) log.trace("startsWith(" + xri + "," + startXri + "," + variablesInXri + "," + variablesInStart + ")");

		if (xri == null) throw new NullPointerException();
		if (startXri == null) throw new NullPointerException();

		if (startXri.equals(XDIConstants.XRI_S_ROOT)) return XDIConstants.XRI_S_ROOT;
		if (xri.equals(XDIConstants.XRI_S_ROOT)) return null;

		int xriIndex = 0, startIndex = 0;

		while (true) {

			if (startIndex == startXri.getNumSubSegments()) return XDI3Util.parentXri(xri, xriIndex);
			if (xriIndex == xri.getNumSubSegments()) return null;

			// check variables

			if (variablesInXri && VariableUtil.isVariable(xri.getSubSegment(xriIndex))) {

				if (VariableUtil.matches(xri.getSubSegment(xriIndex), startXri.getSubSegment(startIndex))) {

					startIndex++;

					if (VariableUtil.isMultiple(xri.getSubSegment(xriIndex))) {

						while (startIndex < startXri.getNumSubSegments() && 
								VariableUtil.matches(xri.getSubSegment(xriIndex), startXri.getSubSegment(startIndex))) startIndex++;
					}

					xriIndex++;

					continue;
				} else {

					return null;
				}
			}

			if (variablesInStart && VariableUtil.isVariable(startXri.getSubSegment(startIndex))) {

				if (VariableUtil.matches(startXri.getSubSegment(startIndex), xri.getSubSegment(xriIndex))) {

					xriIndex++;

					if (VariableUtil.isMultiple(startXri.getSubSegment(startIndex))) {

						while (xriIndex < xri.getNumSubSegments() && 
								VariableUtil.matches(startXri.getSubSegment(startIndex), xri.getSubSegment(xriIndex))) xriIndex++;
					}

					startIndex++;

					continue;
				} else {

					return null;
				}
			}

			// no variables? just match the subsegment

			if (! (xri.getSubSegment(xriIndex).equals(startXri.getSubSegment(startIndex)))) return null;

			xriIndex++;
			startIndex++;
		}
	}

	/**
	 * Checks if an XRI starts with a certain other XRI.
	 */
	public static XDI3Segment startsWith(XDI3Segment xri, XDI3Segment startXri) {

		return startsWith(xri, startXri, false, false);
	}

	/**
	 * Checks if an XRI ends with a certain other XRI.
	 */
	public static XDI3Segment endsWith(XDI3Segment xri, XDI3Segment endXri, boolean variablesInXri, boolean variablesInEnd) {

		if (log.isTraceEnabled()) log.trace("endsWith(" + xri + "," + endXri + "," + variablesInXri + "," + variablesInEnd + ")");

		if (xri == null) throw new NullPointerException();
		if (endXri == null) throw new NullPointerException();

		if (endXri.equals(XDIConstants.XRI_S_ROOT)) return XDIConstants.XRI_S_ROOT;
		if (xri.equals(XDIConstants.XRI_S_ROOT)) return null;

		int xriIndex = xri.getNumSubSegments() - 1, endIndex = endXri.getNumSubSegments() - 1;

		while (true) {

			if (endIndex == -1) return XDI3Util.localXri(xri, - xriIndex - 1);
			if (xriIndex == -1) return null;

			// check variables

			if (variablesInXri && VariableUtil.isVariable(xri.getSubSegment(xriIndex))) {

				if (VariableUtil.matches(xri.getSubSegment(xriIndex), endXri.getSubSegment(endIndex))) {

					endIndex--;

					if (VariableUtil.isMultiple(xri.getSubSegment(xriIndex))) {

						while (endIndex > -1 && 
								VariableUtil.matches(xri.getSubSegment(xriIndex), endXri.getSubSegment(endIndex))) endIndex--;
					}

					xriIndex--;

					continue;
				} else {

					return null;
				}
			}

			if (variablesInEnd && VariableUtil.isVariable(endXri.getSubSegment(endIndex))) {

				if (VariableUtil.matches(endXri.getSubSegment(endIndex), xri.getSubSegment(xriIndex))) {

					xriIndex--;

					if (VariableUtil.isMultiple(endXri.getSubSegment(endIndex))) {

						while (xriIndex > -1 && 
								VariableUtil.matches(endXri.getSubSegment(endIndex), xri.getSubSegment(xriIndex))) xriIndex--;
					}

					endIndex--;

					continue;
				} else {

					return null;
				}
			}

			// no variables? just match the subsegment

			if (! (xri.getSubSegment(xriIndex).equals(endXri.getSubSegment(endIndex)))) return null;

			xriIndex--;
			endIndex--;
		}
	}

	/**
	 * Checks if an XRI ends with a certain other XRI.
	 */
	public static XDI3Segment endsWith(XDI3Segment xri, XDI3Segment endXri) {

		return endsWith(xri, endXri, false, false);
	}

	/**
	 * Extracts an XRI's parent subsegment(s), counting either from the start or the end.
	 * For =a*b*c*d and 1, this returns =a
	 * For =a*b*c*d and -1, this returns =a*b*c
	 */
	public static XDI3Segment parentXri(XDI3Segment xri, int numSubSegments) {

		if (log.isTraceEnabled()) log.trace("parentXri(" + xri + "," + numSubSegments + ")");

		if (xri == null) throw new NullPointerException();

		StringBuilder buffer = new StringBuilder();

		if (numSubSegments > 0) {

			for (int i = 0; i < numSubSegments; i++) buffer.append(xri.getSubSegment(i).toString());
		} else if (numSubSegments < 0) {

			for (int i = 0; i < xri.getNumSubSegments() - (- numSubSegments); i++) buffer.append(xri.getSubSegment(i).toString());
		} else {

			return xri;
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

		if (xri == null) throw new NullPointerException();

		StringBuilder buffer = new StringBuilder();

		if (numSubSegments > 0) {

			for (int i = xri.getNumSubSegments() - numSubSegments; i < xri.getNumSubSegments(); i++) buffer.append(xri.getSubSegment(i).toString());
		} else if (numSubSegments < 0) {

			for (int i = (- numSubSegments); i < xri.getNumSubSegments(); i++) buffer.append(xri.getSubSegment(i).toString());
		} else {

			return xri;
		}

		if (buffer.length() == 0) return null;

		return XDI3Segment.create(buffer.toString());
	}

	/**
	 * Removes a start XRI from an XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and =x, this returns null
	 */
	public static XDI3Segment removeStartXri(XDI3Segment xri, XDI3Segment start, boolean variablesInXri, boolean variablesInStart) {

		if (log.isTraceEnabled()) log.trace("removeStartXri(" + xri + "," + start + "," + variablesInXri + "," + variablesInStart + ")");

		if (xri == null) throw new NullPointerException();
		if (start == null) throw new NullPointerException();

		if (start.equals(XDIConstants.XRI_S_ROOT)) return xri;
		if (xri.equals(XDIConstants.XRI_S_ROOT)) return null;

		XDI3Segment startXri = XDI3Util.startsWith(xri, start, variablesInXri, variablesInStart);
		if (startXri == null) return null;

		if (xri.equals(startXri)) return XDIConstants.XRI_S_ROOT;

		return XDI3Util.localXri(xri, - startXri.getNumSubSegments());
	}

	/**
	 * Removes a start XRI from an XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and =x, this returns null
	 */
	public static XDI3Segment removeStartXri(XDI3Segment xri, XDI3Segment start) {

		return removeStartXri(xri, start, false, false);
	}

	/**
	 * Removes an end XRI from an XRI.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDI3Segment removeEndXri(XDI3Segment xri, XDI3Segment end, boolean variablesInXri, boolean variablesInEnd) {

		if (log.isTraceEnabled()) log.trace("removeEndXri(" + xri + "," + end + "," + variablesInXri + "," + variablesInEnd + ")");

		if (xri == null) throw new NullPointerException();
		if (end == null) throw new NullPointerException();

		if (end.equals(XDIConstants.XRI_S_ROOT)) return xri;
		if (xri.equals(XDIConstants.XRI_S_ROOT)) return null;

		XDI3Segment endXri = XDI3Util.endsWith(xri, end, variablesInXri, variablesInEnd);
		if (endXri == null) return null;

		if (xri.equals(endXri)) return XDIConstants.XRI_S_ROOT;

		return XDI3Util.parentXri(xri, - endXri.getNumSubSegments());
	}

	/**
	 * Removes an end XRI from an XRI.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDI3Segment removeEndXri(XDI3Segment xri, XDI3Segment end) {

		return removeEndXri(xri, end, false, false);
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

	/**
	 * Concats all XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(XDI3Segment[] xris) {

		if (log.isTraceEnabled()) log.trace("concatXris(" + Arrays.asList(xris) + ")");

		StringBuffer buffer = new StringBuffer();

		for (XDI3Segment xri : xris) {

			if (xri != null && ! XDIConstants.XRI_S_ROOT.equals(xri)) buffer.append(xri.toString());
		}

		if (buffer.length() == 0) buffer.append("()");

		return XDI3Segment.create(buffer.toString());
	}

	/**
	 * Concats two XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(XDI3Segment xri1, XDI3Segment xri2) {

		if (log.isTraceEnabled()) log.trace("concatXris(" + xri1 + "," + xri2 + ")");

		StringBuffer buffer = new StringBuffer();
		if (xri1 != null && ! XDIConstants.XRI_S_ROOT.equals(xri1)) buffer.append(xri1.toString()); 
		if (xri2 != null && ! XDIConstants.XRI_S_ROOT.equals(xri2)) buffer.append(xri2.toString()); 

		if (buffer.length() == 0) buffer.append("()");

		return XDI3Segment.create(buffer.toString());
	}

	/**
	 * Concats two XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(XDI3Segment xri1, XDI3SubSegment xri2) {

		return concatXris(xri1, xri2 == null ? null : XDI3Segment.create(xri2));
	}

	/**
	 * Concats two XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(XDI3SubSegment xri1, XDI3Segment xri2) {

		return concatXris(xri1 == null ? null : XDI3Segment.create(xri1), xri2);
	}

	/**
	 * Concats two XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(XDI3SubSegment xri1, XDI3SubSegment xri2) {

		return concatXris(xri1 == null ? null : XDI3Segment.create(xri1), xri2 == null ? null : XDI3Segment.create(xri2));
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
