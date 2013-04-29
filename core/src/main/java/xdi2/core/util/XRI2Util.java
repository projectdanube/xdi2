package xdi2.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Various utility methods for working with XRI 2.0 syntax.
 * 
 * @author markus
 */
public final class XRI2Util {

	private static final Logger log = LoggerFactory.getLogger(XRI2Util.class);

	private XRI2Util() { }

	/**
	 * Maps an XRI 2.0 Canonical ID to a Cloud Number.
	 */
	public static XDI3Segment canonicalIdToCloudNumber(String canonicalId) {

		if (log.isTraceEnabled()) log.trace("canonicalIdToCloudNumber(" + canonicalId + ")");

		char cs = canonicalId.charAt(0);

		canonicalId = canonicalId.substring(2).toLowerCase();

		String[] parts = canonicalId.split("\\.");
		if (parts.length != 4) return null;

		for (int i=0; i<parts.length; i++) {

			if (parts[i].length() > 4) return null;
			while (parts[i].length() < 4) parts[i] = "0" + parts[i];
		}

		StringBuilder builder = new StringBuilder();

		builder.append("[" + cs + "]" + XDIConstants.CS_BANG + ":uuid:");
		builder.append(parts[0]);
		builder.append(parts[1]);
		builder.append("-");
		builder.append(parts[2]);
		builder.append("-");
		builder.append(parts[3]);
		builder.append("-");
		builder.append(parts[0]);
		builder.append("-");
		builder.append(parts[1]);
		builder.append(parts[2]);
		builder.append(parts[3]);

		return XDI3Segment.create(builder.toString());
	}

	/**
	 * Maps a Cloud Number to an XRI 2.0 Canonical ID.
	 */
	public static String cloudNumberToCanonicalId(XDI3Segment cloudNumber) {

		if (log.isTraceEnabled()) log.trace("cloudNumberToCanonicalId(" + cloudNumber + ")");

		if (cloudNumber.getNumSubSegments() != 2) return null;

		if (! cloudNumber.getFirstSubSegment().isClassXs()) return null;
		if (cloudNumber.getFirstSubSegment().hasLiteral()) return null;
		if (cloudNumber.getFirstSubSegment().hasXRef()) return null;

		char cs = cloudNumber.getFirstSubSegment().getCs().charValue();

		if (! XDIConstants.CS_BANG.equals(cloudNumber.getLastSubSegment().getCs())) return null;
		if (! cloudNumber.getLastSubSegment().hasLiteral()) return null;
		if (cloudNumber.getLastSubSegment().hasXRef()) return null;
		if (! cloudNumber.getLastSubSegment().getLiteral().startsWith(":uuid")) return null;
		if (cloudNumber.getLastSubSegment().getLiteral().length() != 42) return null;

		String[] parts = new String[4];
		parts[0] = cloudNumber.getLastSubSegment().getLiteral().substring(6, 10);
		parts[1] = cloudNumber.getLastSubSegment().getLiteral().substring(10, 14);
		parts[2] = cloudNumber.getLastSubSegment().getLiteral().substring(15, 19);
		parts[3] = cloudNumber.getLastSubSegment().getLiteral().substring(20, 24);

		if (! parts[0].equals(cloudNumber.getLastSubSegment().getLiteral().substring(25, 29))) return null;
		if (! parts[1].equals(cloudNumber.getLastSubSegment().getLiteral().substring(30, 34))) return null;
		if (! parts[2].equals(cloudNumber.getLastSubSegment().getLiteral().substring(34, 38))) return null;
		if (! parts[3].equals(cloudNumber.getLastSubSegment().getLiteral().substring(38, 42))) return null;

		for (int i=0; i<parts.length; i++) {

			if (parts[i].length() > 4) return null;
			if (parts[i].indexOf('-') != -1) return null;
			parts[i] = parts[i].toUpperCase();
			while (parts[i].charAt(0) == '0') parts[i] = parts[i].substring(1);
		}

		StringBuilder builder = new StringBuilder();

		builder.append("" + cs + XDIConstants.CS_BANG);
		builder.append(parts[0]);
		builder.append(".");
		builder.append(parts[1]);
		builder.append(".");
		builder.append(parts[2]);
		builder.append(".");
		builder.append(parts[3]);

		return builder.toString();
	}

	/**
	 * Maps an XRI 2.0 service type to an XDI entity singleton arc XRI.
	 */
	public static XDI3SubSegment typeToXdiEntitySingletonArcXri(String type) {

		if (log.isTraceEnabled()) log.trace("typeToXdiEntitySingletonArcXri(" + type + ")");

		if (type.startsWith("xri://")) type = type.substring(6);

		try {

			return XDI3SubSegment.create(type);
		} catch (Exception ex) {

			try {

				return XDI3SubSegment.create("(" + type + ")");
			} catch (Exception ex2) {

				try {
					return XDI3SubSegment.create("(" + URLEncoder.encode(type, "UTF-8") + ")");
				} catch (UnsupportedEncodingException ex3) {

					return null;
				}
			}
		}
	}
}
