package xdi2.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.xri3.CloudNumber;
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
	 * Maps an XRI 2.0 I-Number to a Cloud Number.
	 */
	public static CloudNumber iNumberToCloudNumber(String iNumber) {

		if (log.isTraceEnabled()) log.trace("iNumberToCloudNumber(" + iNumber + ")");

		if (iNumber.startsWith("xri://")) iNumber = iNumber.substring("xri://".length());

		Character cs = Character.valueOf(iNumber.charAt(0));
		if ((! XDIConstants.CS_EQUALS.equals(cs)) && (! XDIConstants.CS_AT.equals(cs))) return null;

		iNumber = iNumber.substring(2).toLowerCase();

		String[] parts = iNumber.split("\\.");
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

		return CloudNumber.create(builder.toString());
	}

	/**
	 * Maps a Cloud Number to an XRI 2.0 I-Number.
	 */
	public static String cloudNumberToINumber(CloudNumber cloudNumber) {

		if (log.isTraceEnabled()) log.trace("cloudNumberToINumber(" + cloudNumber + ")");

		XDI3Segment xri = cloudNumber.getXri();

		if (xri.getNumSubSegments() != 2) return null;

		if (! xri.getFirstSubSegment().isClassXs()) return null;
		if (xri.getFirstSubSegment().hasLiteral()) return null;
		if (xri.getFirstSubSegment().hasXRef()) return null;

		char cs = xri.getFirstSubSegment().getCs().charValue();

		if (! XDIConstants.CS_BANG.equals(xri.getLastSubSegment().getCs())) return null;
		if (! xri.getLastSubSegment().hasLiteral()) return null;
		if (xri.getLastSubSegment().hasXRef()) return null;
		if (! xri.getLastSubSegment().getLiteral().startsWith(":uuid")) return null;
		if (xri.getLastSubSegment().getLiteral().length() != 42) return null;

		String[] parts = new String[4];
		parts[0] = xri.getLastSubSegment().getLiteral().substring(6, 10);
		parts[1] = xri.getLastSubSegment().getLiteral().substring(10, 14);
		parts[2] = xri.getLastSubSegment().getLiteral().substring(15, 19);
		parts[3] = xri.getLastSubSegment().getLiteral().substring(20, 24);

		if (! parts[0].equals(xri.getLastSubSegment().getLiteral().substring(25, 29))) return null;
		if (! parts[1].equals(xri.getLastSubSegment().getLiteral().substring(30, 34))) return null;
		if (! parts[2].equals(xri.getLastSubSegment().getLiteral().substring(34, 38))) return null;
		if (! parts[3].equals(xri.getLastSubSegment().getLiteral().substring(38, 42))) return null;

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
