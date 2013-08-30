package xdi2.core.features.timestamps;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.constants.XDITimestampsConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiValue;

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

		if (string == null) return null;

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

		XdiAttribute xdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(XDITimestampsConstants.XRI_SS_T, false);
		if (xdiAttribute == null) return null;

		XdiValue xdiValue = xdiAttribute.getXdiValue(false);
		if (xdiValue == null) return null;

		Literal timestampLiteral = xdiValue.getContextNode().getLiteral();
		if (timestampLiteral == null) return null;

		Date timestamp = stringToTimestamp(timestampLiteral.getLiteralDataString());
		return timestamp;
	}

	/**
	 * Set the timestamp associated with a context node.
	 */
	public static void setContextNodeTimestamp(ContextNode contextNode, Date timestamp) {

		String literalData = timestampToString(timestamp);

		XdiAttribute xdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(XDITimestampsConstants.XRI_SS_T, true);
		XdiValue xdiValue = xdiAttribute.getXdiValue(true);
		xdiValue.getContextNode().setLiteralString(literalData);
	}
}
