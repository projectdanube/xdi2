package xdi2.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

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
	public static XDI3Segment startsWith(final XDI3Segment xri, final XDI3Segment startXri, final boolean variablesInXri, final boolean variablesInStart) {

		if (xri == null) throw new NullPointerException();
		if (startXri == null) throw new NullPointerException();

		XDI3Segment result = null;

		try {

			if (startXri.equals(XDIConstants.XRI_S_ROOT)) { result = XDIConstants.XRI_S_ROOT; return result; }
			if (xri.equals(XDIConstants.XRI_S_ROOT)) { result = null; return result; }

			int xriIndex = 0, startIndex = 0;

			while (true) {

				if (startIndex == startXri.getNumSubSegments()) { result = XDI3Util.parentXri(xri, xriIndex); return result; }
				if (xriIndex == xri.getNumSubSegments()) { result = null; return result; }

				// check variables

				if (variablesInXri && VariableUtil.isVariable(xri.getSubSegment(xriIndex))) {

					if (VariableUtil.matches(xri.getSubSegment(xriIndex), startXri.getSubSegment(startIndex))) {

						startIndex++;

						if (VariableUtil.isMultiple(xri.getSubSegment(xriIndex))) {

							while (true) {

								if (startIndex == startXri.getNumSubSegments()) break;
								if (! VariableUtil.matches(xri.getSubSegment(xriIndex), startXri.getSubSegment(startIndex))) break;
								if (xriIndex + 1 < xri.getNumSubSegments() && xri.getSubSegment(xriIndex + 1).equals(startXri.getSubSegment(startIndex))) break;

								startIndex++;
							}
						}

						xriIndex++;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				if (variablesInStart && VariableUtil.isVariable(startXri.getSubSegment(startIndex))) {

					if (VariableUtil.matches(startXri.getSubSegment(startIndex), xri.getSubSegment(xriIndex))) {

						xriIndex++;

						if (VariableUtil.isMultiple(startXri.getSubSegment(startIndex))) {

							while (true) {

								if (xriIndex == xri.getNumSubSegments()) break;
								if (! VariableUtil.matches(startXri.getSubSegment(startIndex), xri.getSubSegment(xriIndex))) break;
								if (startIndex + 1 < startXri.getNumSubSegments() && xri.getSubSegment(xriIndex).equals(startXri.getSubSegment(startIndex + 1))) break;

								xriIndex++;
							}
						}

						startIndex++;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				// no variables? just match the subsegment

				if (! (xri.getSubSegment(xriIndex).equals(startXri.getSubSegment(startIndex)))) { result = null; return result; }

				xriIndex++;
				startIndex++;
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("startsWith(" + xri + "," + startXri + "," + variablesInXri + "," + variablesInStart + ") --> " + result);
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
	public static XDI3Segment endsWith(final XDI3Segment xri, final XDI3Segment endXri, final boolean variablesInXri, final boolean variablesInEnd) {

		if (xri == null) throw new NullPointerException();
		if (endXri == null) throw new NullPointerException();

		XDI3Segment result = null;

		try {

			if (endXri.equals(XDIConstants.XRI_S_ROOT)) { result = XDIConstants.XRI_S_ROOT; return result; }
			if (xri.equals(XDIConstants.XRI_S_ROOT)) { result = null; return result; }

			int xriIndex = xri.getNumSubSegments() - 1, endIndex = endXri.getNumSubSegments() - 1;

			while (true) {

				if (endIndex == -1) { result = XDI3Util.localXri(xri, - xriIndex - 1); return result; }
				if (xriIndex == -1) { result = null; return result; }

				// check variables

				if (variablesInXri && VariableUtil.isVariable(xri.getSubSegment(xriIndex))) {

					if (VariableUtil.matches(xri.getSubSegment(xriIndex), endXri.getSubSegment(endIndex))) {

						endIndex--;

						if (VariableUtil.isMultiple(xri.getSubSegment(xriIndex))) {

							while (true) {

								if (endIndex == -1) break;
								if (! VariableUtil.matches(xri.getSubSegment(xriIndex), endXri.getSubSegment(endIndex))) break;
								if (xriIndex - 1 > -1 && xri.getSubSegment(xriIndex - 1).equals(endXri.getSubSegment(endIndex))) break;

								endIndex--;
							}
						}

						xriIndex--;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				if (variablesInEnd && VariableUtil.isVariable(endXri.getSubSegment(endIndex))) {

					if (VariableUtil.matches(endXri.getSubSegment(endIndex), xri.getSubSegment(xriIndex))) {

						xriIndex--;

						if (VariableUtil.isMultiple(endXri.getSubSegment(endIndex))) {

							while (true) {

								if (xriIndex == -1) break;
								if (! VariableUtil.matches(endXri.getSubSegment(endIndex), xri.getSubSegment(xriIndex))) break;
								if (endIndex - 1 > -1 && xri.getSubSegment(xriIndex).equals(endXri.getSubSegment(endIndex - 1))) break;

								xriIndex--;
							}
						}

						endIndex--;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				// no variables? just match the subsegment

				if (! (xri.getSubSegment(xriIndex).equals(endXri.getSubSegment(endIndex)))) { result = null; return result; }

				xriIndex--;
				endIndex--;
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("endsWith(" + xri + "," + endXri + "," + variablesInXri + "," + variablesInEnd + ") --> " + result);
		}
	}

	/**
	 * Checks if an XRI ends with a certain other XRI.
	 */
	public static XDI3Segment endsWith(final XDI3Segment xri, final XDI3Segment endXri) {

		return endsWith(xri, endXri, false, false);
	}

	/**
	 * Extracts an XRI's parent subsegment(s), counting either from the start or the end.
	 * For =a*b*c*d and 1, this returns =a
	 * For =a*b*c*d and -1, this returns =a*b*c
	 */
	public static XDI3Segment parentXri(final XDI3Segment xri, final int numSubSegments) {

		if (xri == null) throw new NullPointerException();

		XDI3Segment result = null;

		try {

			StringBuilder buffer = new StringBuilder();

			if (numSubSegments > 0) {

				for (int i = 0; i < numSubSegments; i++) buffer.append(xri.getSubSegment(i).toString());
			} else if (numSubSegments < 0) {

				for (int i = 0; i < xri.getNumSubSegments() - (- numSubSegments); i++) buffer.append(xri.getSubSegment(i).toString());
			} else {

				{ result = xri; return result; }
			}

			if (buffer.length() == 0) { result = null; return result; }

			{ result = XDI3Segment.create(buffer.toString()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("parentXri(" + xri + "," + numSubSegments + ") --> " + result);
		}
	}

	/**
	 * Extracts an XRI's local subsegment(s).
	 * For =a*b*c*d and 1, this returns *d
	 * For =a*b*c*d and -1, this returns *b*c*d
	 */
	public static XDI3Segment localXri(final XDI3Segment xri, final int numSubSegments) {

		if (xri == null) throw new NullPointerException();

		XDI3Segment result = null;

		try {

			StringBuilder buffer = new StringBuilder();

			if (numSubSegments > 0) {

				for (int i = xri.getNumSubSegments() - numSubSegments; i < xri.getNumSubSegments(); i++) buffer.append(xri.getSubSegment(i).toString());
			} else if (numSubSegments < 0) {

				for (int i = (- numSubSegments); i < xri.getNumSubSegments(); i++) buffer.append(xri.getSubSegment(i).toString());
			} else {

				{ result = xri; return xri; }
			}

			if (buffer.length() == 0) { result = null; return result; }

			{ result = XDI3Segment.create(buffer.toString()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("localXri(" + xri + "," + numSubSegments + ") --> " + result);
		}
	}

	/**
	 * Removes a start XRI from an XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and =x, this returns null
	 */
	public static XDI3Segment removeStartXri(final XDI3Segment xri, final XDI3Segment start, final boolean variablesInXri, final boolean variablesInStart) {

		if (xri == null) throw new NullPointerException();
		if (start == null) throw new NullPointerException();

		XDI3Segment result = null;

		try {

			if (start.equals(XDIConstants.XRI_S_ROOT)) { result = xri; return result; }
			if (xri.equals(XDIConstants.XRI_S_ROOT)) { result = null; return result; }

			XDI3Segment startXri = XDI3Util.startsWith(xri, start, variablesInXri, variablesInStart);
			if (startXri == null) { result = null; return result; }

			if (xri.equals(startXri)) { result = XDIConstants.XRI_S_ROOT; return result; }

			{ result = XDI3Util.localXri(xri, - startXri.getNumSubSegments()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeStartXri(" + xri + "," + start + "," + variablesInXri + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Removes a start XRI from an XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and =x, this returns null
	 */
	public static XDI3Segment removeStartXri(final XDI3Segment xri, final XDI3Segment start) {

		return removeStartXri(xri, start, false, false);
	}

	/**
	 * Removes an end XRI from an XRI.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDI3Segment removeEndXri(final XDI3Segment xri, final XDI3Segment end, final boolean variablesInXri, final boolean variablesInEnd) {

		if (xri == null) throw new NullPointerException();
		if (end == null) throw new NullPointerException();

		XDI3Segment result = null;

		try {

			if (end.equals(XDIConstants.XRI_S_ROOT)) { result = xri; return result; }
			if (xri.equals(XDIConstants.XRI_S_ROOT)) { result = null; return result; }

			XDI3Segment endXri = XDI3Util.endsWith(xri, end, variablesInXri, variablesInEnd);
			if (endXri == null) { result = null; return result; }

			if (xri.equals(endXri)) { result = XDIConstants.XRI_S_ROOT; return result; }

			{ result = XDI3Util.parentXri(xri, - endXri.getNumSubSegments()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeEndXri(" + xri + "," + end + "," + variablesInXri + "," + variablesInEnd + ") --> " + result);
		}
	}

	/**
	 * Removes an end XRI from an XRI.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDI3Segment removeEndXri(final XDI3Segment xri, final XDI3Segment end) {

		return removeEndXri(xri, end, false, false);
	}

	/**
	 * Replaces all occurences of a subsegment with a segment.
	 */
	public static XDI3Segment replaceXri(final XDI3Segment xri, final XDI3SubSegment oldXri, final XDI3Segment newXri, final boolean replaceInXRefSegment, final boolean replaceInXRefStatement, final boolean replaceInXRefPartialSubjectAndPredicate) {

		if (xri == null) throw new NullPointerException();
		if (oldXri == null) throw new NullPointerException();
		if (newXri == null) throw new NullPointerException();

		XDI3Segment result = null;

		try {

			List<XDI3SubSegment> subSegments = new ArrayList<XDI3SubSegment> ();

			for (XDI3SubSegment subSegment : xri.getSubSegments()) {

				if (subSegment.equals(oldXri)) {

					subSegments.addAll(newXri.getSubSegments());

					continue;
				}

				if (replaceInXRefSegment && subSegment.hasXRef() && subSegment.getXRef().hasSegment()) {

					XDI3Segment xRefSegment = subSegment.getXRef().getSegment();

					xRefSegment = replaceXri(xRefSegment, oldXri, newXri, replaceInXRefSegment, replaceInXRefStatement, replaceInXRefPartialSubjectAndPredicate);

					subSegments.add(XDI3SubSegment.fromComponents(subSegment.getCs(), subSegment.isClassXs(), subSegment.isAttributeXs(), null, XDI3XRef.fromComponents(subSegment.getXRef().getXs(), xRefSegment, null, null, null, null, null)));

					continue;
				}

				if (replaceInXRefStatement && subSegment.hasXRef() && subSegment.getXRef().hasStatement()) {

					XDI3Statement xRefStatement = subSegment.getXRef().getStatement();

					//			TODO		xRefSegment = replaceXri(xRefSegment, oldXri, newXri, replaceInXRefSegment, replaceInXRefStatement, replaceInXRefPartialSubjectAndPredicate);

					subSegments.add(XDI3SubSegment.fromComponents(subSegment.getCs(), subSegment.isClassXs(), subSegment.isAttributeXs(), null, XDI3XRef.fromComponents(subSegment.getXRef().getXs(), null, xRefStatement, null, null, null, null)));

					continue;
				}

				if (replaceInXRefPartialSubjectAndPredicate && subSegment.hasXRef() && subSegment.getXRef().hasPartialSubjectAndPredicate()) {

					XDI3Segment xRefPartialSubject = subSegment.getXRef().getPartialSubject();
					XDI3Segment xRefPartialPredicate = subSegment.getXRef().getPartialPredicate();

					xRefPartialSubject = replaceXri(xRefPartialSubject, oldXri, newXri, replaceInXRefSegment, replaceInXRefStatement, replaceInXRefPartialSubjectAndPredicate);
					xRefPartialPredicate = replaceXri(xRefPartialPredicate, oldXri, newXri, replaceInXRefSegment, replaceInXRefStatement, replaceInXRefPartialSubjectAndPredicate);

					subSegments.add(XDI3SubSegment.fromComponents(subSegment.getCs(), subSegment.isClassXs(), subSegment.isAttributeXs(), null, XDI3XRef.fromComponents(subSegment.getXRef().getXs(), null, null, xRefPartialSubject, xRefPartialPredicate, null, null)));

					continue;
				}

				subSegments.add(subSegment);
			}

			{ result = XDI3Segment.fromComponents(subSegments); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("replaceXri(" + xri + "," + oldXri + "," + newXri + "," + replaceInXRefPartialSubjectAndPredicate + ") --> " + result);
		}
	}

	/**
	 * Concats all XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(final XDI3Segment[] xris) {

		XDI3Segment result = null;

		try {

			StringBuffer buffer = new StringBuffer();

			if (xris != null) {

				for (XDI3Segment xri : xris) {

					if (xri != null && ! XDIConstants.XRI_S_ROOT.equals(xri)) buffer.append(xri.toString());
				}
			}

			if (buffer.length() == 0) buffer.append("()");

			{ result = XDI3Segment.create(buffer.toString()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("concatXris(" + Arrays.asList(xris) + ") --> " + result);
		}
	}

	/**
	 * Concats two XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(XDI3Segment xri1, XDI3Segment xri2) {

		XDI3Segment result = null;

		try {

			StringBuffer buffer = new StringBuffer();
			if (xri1 != null && ! XDIConstants.XRI_S_ROOT.equals(xri1)) buffer.append(xri1.toString()); 
			if (xri2 != null && ! XDIConstants.XRI_S_ROOT.equals(xri2)) buffer.append(xri2.toString()); 

			if (buffer.length() == 0) buffer.append("()");

			{ result = XDI3Segment.create(buffer.toString()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("concatXris(" + xri1 + "," + xri2 + ") --> " + result);
		}
	}

	/**
	 * Concats two XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(final XDI3Segment xri1, final XDI3SubSegment xri2) {

		return concatXris(xri1, xri2 == null ? null : XDI3Segment.fromComponent(xri2));
	}

	/**
	 * Concats two XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(final XDI3SubSegment xri1, final XDI3Segment xri2) {

		return concatXris(xri1 == null ? null : XDI3Segment.fromComponent(xri1), xri2);
	}

	/**
	 * Concats two XRIs into a new XRI.
	 */
	public static XDI3Segment concatXris(final XDI3SubSegment xri1, final XDI3SubSegment xri2) {

		return concatXris(xri1 == null ? null : XDI3Segment.fromComponent(xri1), xri2 == null ? null : XDI3Segment.fromComponent(xri2));
	}

	/**
	 * Checks if an XRI is a valid Cloud Number.
	 */
	public static boolean isCloudNumber(final XDI3Segment xri) {

		if (xri == null) throw new NullPointerException();

		Boolean result = null;

		try {

			if (xri.getNumSubSegments() < 2) { result = Boolean.FALSE; return result.booleanValue(); }

			for (int i=0; i< xri.getNumSubSegments(); i+=2) {

				if (! xri.getSubSegment(i).isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_EQUALS.equals(xri.getSubSegment(i).getCs()) && ! XDIConstants.CS_AT.equals(xri.getSubSegment(i).getCs())) { result = Boolean.FALSE; return result.booleanValue(); }

				if (! XDIConstants.CS_BANG.equals(xri.getSubSegment(i + 1).getCs())) { result = Boolean.FALSE; return result.booleanValue(); }
			}

			{ result = Boolean.TRUE; return result.booleanValue(); }
		} finally {

			if (log.isTraceEnabled()) log.trace("isCloudNumber(" + xri + ") --> " + result);
		}
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
