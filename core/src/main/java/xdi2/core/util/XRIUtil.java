package xdi2.core.util;

import java.util.Random;
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

	private static final Random random;

	static {

		random = new Random();
	}

	private XRIUtil() { }

	public static XRI3SubSegment randomUUIDSubSegment(char lcs) {

		return new XRI3SubSegment(lcs + UUID.randomUUID().toString().replace("-", "."));
	}

	public static XRI3SubSegment randomHEXSubSegment(char lcs) {

		final String hex = "0123456789abcdef";
		StringBuilder inumber = new StringBuilder();
		inumber.append(lcs);
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));
		inumber.append('.');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));
		inumber.append('.');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));
		inumber.append('.');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));

		return new XRI3SubSegment(inumber.toString());
	}

	public static XRI3SubSegment randomHEXXRefSubSegment(char lcs) {

		final String hex = "0123456789abcdef";
		StringBuilder inumber = new StringBuilder();
		inumber.append(lcs);
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));
		inumber.append('.');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));
		inumber.append('.');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));
		inumber.append('.');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));

		return new XRI3SubSegment("(" + inumber.toString() + ")");
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
