package xdi2.core.features.timestamps;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.constants.XDITimestampsConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class Timestamps {

	/*
	 * Methods for converting between Date and strings used as literal values.
	 */

	public static String timestampToString(Date timestamp) {

		DateFormat dateFormat = XDITimestampsConstants.FORMATS_TIMESTAMP[0];
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		String string = dateFormat.format(timestamp);
		string = string.replace("+0000", "Z");

		return string;
	}

	public static Date stringToTimestamp(String string) {

		if (string.charAt(string.length() - 1) == 'Z') string = string.substring(0, string.length() - 1) + "UTC";

		for (DateFormat dateFormat : XDITimestampsConstants.FORMATS_TIMESTAMP) {

			try {

				return dateFormat.parse(string);
			} catch (ParseException ex) {

				continue;
			}
		}

		throw new Xdi2RuntimeException("Cannot parse timestamp: " + string);
	}

	/*
	 * Methods for reading and writing timestamps from a context node.
	 */

	/**
	 * Get the timestamp associated with a context node.
	 */
	public static Date getContextNodeTimestamp(ContextNode contextNode) {

		XRI3SubSegment arcXri = Multiplicity.attributeSingletonArcXri(XDITimestampsConstants.XRI_SS_T);

		Literal timestampLiteral = contextNode.getLiteralInContextNode(arcXri);
		if (timestampLiteral == null) return null;

		Date timestamp = stringToTimestamp(timestampLiteral.getLiteralData());
		return timestamp;
	}

	/**
	 * Set the timestamp associated with a context node.
	 */
	public static void setContextNodeTimestamp(ContextNode contextNode, Date timestamp) {

		XRI3SubSegment arcXri = Multiplicity.attributeSingletonArcXri(XDITimestampsConstants.XRI_SS_T);

		String string = timestampToString(timestamp);

		Literal timestampLiteral = contextNode.getLiteralInContextNode(arcXri);

		if (timestampLiteral == null) 
			timestampLiteral = contextNode.createLiteralInContextNode(arcXri, string);
		else
			timestampLiteral.setLiteralData(string);
	}
}
