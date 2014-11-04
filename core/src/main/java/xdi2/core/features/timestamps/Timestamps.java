package xdi2.core.features.timestamps;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.constants.XDITimestampsConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.XDIAddressUtil;

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
	public static Date getContextNodeTimestamp(ContextNode contextNode, XDIAddress modifier) {

		XDIAddress timestampAddress = modifier == null ? XDITimestampsConstants.XDI_ADD_AS_T : XDIAddressUtil.concatXDIAddresses(modifier, XDITimestampsConstants.XDI_ARC_AS_T);

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(timestampAddress, false);
		if (xdiAttributeSingleton == null) return null;

		LiteralNode timestampLiteral = xdiAttributeSingleton.getLiteralNode();
		if (timestampLiteral == null) return null;

		Date timestamp = stringToTimestamp(timestampLiteral.getLiteralDataString());
		return timestamp;
	}

	public static Date getContextNodeTimestamp(ContextNode contextNode) {

		return getContextNodeTimestamp(contextNode, null);
	}

	/**
	 * Set the timestamp associated with a context node.
	 */
	public static void setContextNodeTimestamp(ContextNode contextNode, XDIAddress modifierAddress, Date timestamp) {

		XDIAddress timestampAddress = modifierAddress == null ? XDITimestampsConstants.XDI_ADD_AS_T : XDIAddressUtil.concatXDIAddresses(modifierAddress, XDITimestampsConstants.XDI_ARC_AS_T);

		String literalData = timestampToString(timestamp);

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(timestampAddress, true);
		xdiAttributeSingleton.setLiteralString(literalData);
	}

	public static void setContextNodeTimestamp(ContextNode contextNode, Date timestamp) {

		setContextNodeTimestamp(contextNode, null, timestamp);
	}
}
