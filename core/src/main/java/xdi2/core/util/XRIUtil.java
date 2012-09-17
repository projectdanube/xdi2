package xdi2.core.util;

import java.util.Comparator;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.variables.Variables;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * Various utility methods for working with XRIs.
 * 
 * @author markus
 */
public final class XRIUtil {

	private static final Logger log = LoggerFactory.getLogger(XRIUtil.class);

	private XRIUtil() { }

	public static XRI3SubSegment randomSubSegment(String prefix) {

		return new XRI3SubSegment(prefix + UUID.randomUUID().toString());
	}

	public static XRI3SubSegment randomXRefSubSegment(String outerPrefix, String innerPrefix) {

		return new XRI3SubSegment(outerPrefix + "(" + innerPrefix + UUID.randomUUID().toString() + ")");
	}

	/**
	 * Checks if an XRI starts with a certain other XRI.
	 */
	public static boolean startsWith(XRI3Segment xri, XRI3Segment base, boolean variablesInXri, boolean variablesInBase) {

		if (log.isTraceEnabled()) log.trace("startsWith(" + xri + "," + base + "," + variablesInXri + "," + variablesInBase + ")");

		if (xri.getNumSubSegments() < base.getNumSubSegments()) return false;

		for (int i=0; i<base.getNumSubSegments(); i++) {

			if (variablesInXri && Variables.isVariable((XRI3SubSegment) xri.getSubSegment(i))) continue;
			if (variablesInBase && Variables.isVariable((XRI3SubSegment) base.getSubSegment(i))) continue;

			if (! (xri.getSubSegment(i).equals(base.getSubSegment(i)))) return false;
		}

		return true;
	}

	/**
	 * Checks if an XRI starts with a certain other XRI.
	 */
	public static boolean startsWith(XRI3Segment xri, XRI3Segment base) {

		return startsWith(xri, base, false, false);
	}

	/**
	 * Extracts a relative XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XRI3Segment relativeXri(XRI3Segment xri, XRI3Segment base, boolean variablesInXri, boolean variablesInBase) {

		if (log.isTraceEnabled()) log.trace("relativeXri(" + xri + "," + base + "," + variablesInXri + "," + variablesInBase + ")");

		if (xri.getNumSubSegments() < base.getNumSubSegments()) return null;

		int i = 0;

		for (; i<base.getNumSubSegments(); i++) {

			if (variablesInXri && Variables.isVariable((XRI3SubSegment) xri.getSubSegment(i))) continue;
			if (variablesInBase && Variables.isVariable((XRI3SubSegment) base.getSubSegment(i))) continue;

			if (! (xri.getSubSegment(i).equals(base.getSubSegment(i)))) return null;
		}

		StringBuilder buffer = new StringBuilder();

		for (; i<xri.getNumSubSegments(); i++) {

			buffer.append(xri.getSubSegment(i).toString());
		}

		if (buffer.length() == 0) return XDIConstants.XRI_S_CONTEXT;

		return new XRI3Segment(buffer.toString());
	}

	/**
	 * Extracts a relative XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XRI3Segment relativeXri(XRI3Segment xri, XRI3Segment base) {

		return relativeXri(xri, base, false, false);
	}

	/**
	 * Extracts an XRI's parent XRI.
	 * E.g. for =a*b*c*d, this returns =a*b*c
	 */
	public static XRI3Segment parentXri(XRI3Segment xri) {

		if (log.isTraceEnabled()) log.trace("parentXri(" + xri + ")");

		if (xri.getNumSubSegments() <= 1)  return null;

		StringBuilder buffer = new StringBuilder();

		for (int i=0; i<xri.getNumSubSegments() - 1; i++) {

			buffer.append(xri.getSubSegment(i).toString());
		}

		return new XRI3Segment(buffer.toString());
	}

	/**
	 * Extracts an XRI's local part.
	 * E.g. for =a*b*c*d, this returns *d
	 */
	public static XRI3SubSegment localXri(XRI3Segment xri) {

		if (log.isTraceEnabled()) log.trace("localXri(" + xri  + ")");

		if (xri.getNumSubSegments() > 0) {

			return (XRI3SubSegment) xri.getLastSubSegment();
		} else {

			return null;
		}
	}

	/**
	 * Checks if a subsegment is illegal as an arc XRI for a context node.
	 */
	public static boolean isIllegalContextNodeArcXri(XRI3SubSegment arcXri) {

		if (XDIConstants.XRI_SS_CONTEXT.equals(arcXri)) return true;

		return false;
	}

	/**
	 * Checks if a subsegment is illegal as an arc XRI for a relation.
	 */
	public static boolean isIllegalRelationArcXri(XRI3Segment arcXri) {

		if (XDIConstants.XRI_SS_CONTEXT.equals(arcXri)) return true;
		if (XDIConstants.XRI_SS_LITERAL.equals(arcXri)) return true;

		return false;
	}

	/*
	 * Helper classes
	 */

	public static final Comparator<? super XRI3Segment> XRI3SEGMENT_ASCENDING_COMPARATOR = new Comparator<XRI3Segment>() {

		@Override
		public int compare(XRI3Segment o1, XRI3Segment o2) {

			if (o1.getNumSubSegments() < o2.getNumSubSegments()) return -1;
			if (o1.getNumSubSegments() > o2.getNumSubSegments()) return 1;

			return o1.compareTo(o2);
		}
	};

	public static final Comparator<? super XRI3Segment> XRI3SEGMENT_DESCENDING_COMPARATOR = new Comparator<XRI3Segment>() {

		@Override
		public int compare(XRI3Segment o1, XRI3Segment o2) {

			if (o1.getNumSubSegments() > o2.getNumSubSegments()) return -1;
			if (o1.getNumSubSegments() < o2.getNumSubSegments()) return 1;

			return o1.compareTo(o2);
		}
	};
}
