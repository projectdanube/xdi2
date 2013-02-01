package xdi2.core.util;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.variables.Variables;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Various utility methods for working with XRIs.
 * 
 * @author markus
 */
public final class XRIUtil {

	private static final Logger log = LoggerFactory.getLogger(XRIUtil.class);

	private XRIUtil() { }

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

			if (variablesInXri && Variables.isVariableSingle(xriSubSegment)) {

				if (Variables.isVariableSingle(xriSubSegment)) {

					xriIndex++;
					baseIndex++;

					continue; 
				}

				if (Variables.isVariableMultipleLocal(xriSubSegment)) {

					xriIndex++;
					baseIndex++;

					while (baseIndex < base.getNumSubSegments() && (! base.getSubSegment(baseIndex).hasGCS())) baseIndex++;

					continue;
				}
			}

			if (variablesInBase) {

				if (Variables.isVariableSingle(baseSubSegment)) {

					xriIndex++;
					baseIndex++;

					continue; 
				}

				if (Variables.isVariableMultipleLocal(baseSubSegment)) {

					xriIndex++;
					baseIndex++;

					while (xriIndex < xri.getNumSubSegments() && (! xri.getSubSegment(xriIndex).hasGCS())) xriIndex++;

					continue;
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
	 * Extracts a relative XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XDI3Segment relativeXri(XDI3Segment xri, XDI3Segment base, boolean variablesInXri, boolean variablesInBase) {

		if (log.isTraceEnabled()) log.trace("relativeXri(" + xri + "," + base + "," + variablesInXri + "," + variablesInBase + ")");

		if (xri == null) return null;
		if (base == null) return xri;

		int xriIndex = 0, baseIndex = 0;

		while (true) {

			if (baseIndex == base.getNumSubSegments()) break;
			if (xriIndex == xri.getNumSubSegments()) return null;

			XDI3SubSegment xriSubSegment = xri.getSubSegment(xriIndex);
			XDI3SubSegment baseSubSegment = base.getSubSegment(baseIndex);

			// check variables

			if (variablesInXri && Variables.isVariableSingle(xriSubSegment)) {

				if (Variables.isVariableSingle(xriSubSegment)) {

					xriIndex++;
					baseIndex++;

					continue; 
				}

				if (Variables.isVariableMultipleLocal(xriSubSegment)) {

					xriIndex++;
					baseIndex++;

					while (baseIndex < base.getNumSubSegments() && (! base.getSubSegment(baseIndex).hasGCS())) baseIndex++;

					continue;
				}
			}

			if (variablesInBase) {

				if (Variables.isVariableSingle(baseSubSegment)) {

					xriIndex++;
					baseIndex++;

					continue; 
				}

				if (Variables.isVariableMultipleLocal(baseSubSegment)) {

					xriIndex++;
					baseIndex++;

					while (xriIndex < xri.getNumSubSegments() && (! xri.getSubSegment(xriIndex).hasGCS())) xriIndex++;

					continue;
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
	 * Extracts a relative XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XDI3Segment relativeXri(XDI3Segment xri, XDI3Segment base) {

		return relativeXri(xri, base, false, false);
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
	 * Checks if a subsegment is illegal as an arc XRI for a context node.
	 */
	public static boolean isIllegalContextNodeArcXri(XDI3SubSegment arcXri) {

		if (XDIConstants.XRI_SS_CONTEXT.equals(arcXri)) return true;

		return false;
	}

	/**
	 * Checks if a subsegment is illegal as an arc XRI for a relation.
	 */
	public static boolean isIllegalRelationArcXri(XDI3Segment arcXri, XDI3Segment targetXri) {

		if (XDIConstants.XRI_SS_CONTEXT.equals(arcXri)) return true;
		if (XDIConstants.XRI_SS_LITERAL.equals(arcXri) && (! Variables.isVariable(targetXri))) return true;

		return false;
	}

	/**
	 * Replaces all occurences of a subsegment with a segment.
	 */
	@SuppressWarnings("unchecked")
	public static XDI3Segment replaceXri(XDI3Segment xri, XDI3SubSegment oldXri, XDI3Segment newXri) {

		StringBuffer buffer = new StringBuffer();

		for (XDI3SubSegment subSegment : ((List<XDI3SubSegment>) xri.getSubSegments())) {

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
