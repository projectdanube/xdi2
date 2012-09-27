package xdi2.core.features.timestamps;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.constants.XDITimestampsConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;

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

		Literal timestampLiteral = contextNode.getLiteralInContextNode(XDITimestampsConstants.XRI_SS_T);
		if (timestampLiteral == null) return null;

		Date timestamp = stringToTimestamp(timestampLiteral.getLiteralData());
		return timestamp;
	}

	/**
	 * Set the timestamp associated with a context node.
	 */
	public static void setContextNodeTimestamp(ContextNode contextNode, Date timestamp) {

		String string = timestampToString(timestamp);

		Literal timestampLiteral = contextNode.getLiteralInContextNode(XDITimestampsConstants.XRI_SS_T);

		if (timestampLiteral == null) 
			timestampLiteral = contextNode.createLiteralInContextNode(XDITimestampsConstants.XRI_SS_T, string);
		else
			timestampLiteral.setLiteralData(string);
	}
}
