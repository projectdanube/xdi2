package xdi2.core.util;

import java.util.UUID;

import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * Various utility methods for cloning graph components.
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

	public static XRI3 extractParentXri(XRI3 xri) {

		StringBuffer buffer = new StringBuffer();

		if (xri.hasPath()) {

			buffer.append(xri.getAuthority());

			for (int i=0; i<xri.getPath().getNumSegments() - 1; i++) {

				buffer.append("/");
				buffer.append(xri.getPath().getSegment(i).toString());
			}
		} else {

			return null;
		}

		return new XRI3(buffer.toString());
	}

	public static XRI3Segment extractParentXriSegment(XRI3Segment xri) {

		StringBuffer buffer = new StringBuffer();

		if (xri.getNumSubSegments() > 1) {

			for (int i=0; i<xri.getNumSubSegments() - 1; i++) {

				buffer.append(xri.getSubSegment(i).toString());
			}
		} else {

			return null;
		}

		return new XRI3Segment(buffer.toString());
	}

	public static XRI3Segment extractLocalXriSegment(XRI3Segment xri) {

		if (xri.getNumSubSegments() > 0) {

			return new XRI3Segment("" + xri.getLastSubSegment());
		} else {

			return null;
		}
	}
}
