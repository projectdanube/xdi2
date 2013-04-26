package xdi2.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * Maps an XRI 2.0 canonicalID to a cloudnumber.
	 */
	public static XDI3Segment canonicalIdToCloudnumber(String canonicalId) {

		if (log.isTraceEnabled()) log.trace("canonicalIdToCloudnumber(" + canonicalId + ")");
		
		canonicalId = canonicalId.substring(2).toLowerCase();

		String[] parts = canonicalId.split("\\.");
		if (parts.length != 4) return null;

		for (int i=0; i<parts.length; i++) {

			if (parts[i].length() > 4) return null;
			while (parts[i].length() < 4) parts[i] = "0" + parts[i];
		}

		StringBuilder builder = new StringBuilder();

		builder.append("[=]!:uuid:");
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
