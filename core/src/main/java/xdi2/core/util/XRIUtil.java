package xdi2.core.util;

import java.util.UUID;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * Various utility methods for working with XRIs.
 * 
 * @author markus
 */
public final class XRIUtil {

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
	public static boolean startsWith(XRI3Segment xri, XRI3Segment part) {

		if (xri.getNumSubSegments() < part.getNumSubSegments()) return false;

		for (int i=0; i<part.getNumSubSegments(); i++) {

			if (! (xri.getSubSegment(i).equals(part.getSubSegment(i)))) return false;
		}

		return true;
	}

	/**
	 * Extracts a relative XRI.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 */
	public static XRI3Segment relativeXRI(XRI3Segment xri, XRI3Segment base) {

		if (xri.getNumSubSegments() <= base.getNumSubSegments()) return null;

		int i = 0;

		for (; i<base.getNumSubSegments(); i++) {

			if (! (xri.getSubSegment(i).equals(base.getSubSegment(i)))) return null;
		}

		StringBuilder buffer = new StringBuilder();

		for (; i<xri.getNumSubSegments(); i++) {

			buffer.append(xri.getSubSegment(i).toString());
		}

		return new XRI3Segment(buffer.toString());
	}

	/**
	 * Extracts an XRI's parent XRI.
	 * E.g. for =a*b*c*d, this returns =a*b*c
	 */
	public static XRI3Segment parentXri(XRI3Segment xri) {

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
	public static XRI3Segment localXri(XRI3Segment xri) {

		if (xri.getNumSubSegments() > 0) {

			return new XRI3Segment("" + xri.getLastSubSegment());
		} else {

			return null;
		}
	}
}
