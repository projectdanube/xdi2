package xdi2.core.features.timestamps;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import xdi2.core.Literal;
import xdi2.core.constants.XDITimestampsConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiValue;
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
	public static Date getTimestamp(XdiContext<?> xdiContext, XDIAddress modifier) {

		XDIAddress timestampXDIAddress = modifier == null ? XDITimestampsConstants.XDI_ADD_AS_T : XDIAddressUtil.concatXDIAddresses(modifier, XDITimestampsConstants.XDI_ARC_AS_T);

		XdiAttributeSingleton xdiAttributeSingleton = xdiContext.getXdiAttributeSingleton(timestampXDIAddress, false);
		if (xdiAttributeSingleton == null) return null;

		XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(false);
		if (xdiValue == null) return null;

		Literal timestampLiteral = xdiValue.getContextNode().getLiteral();
		if (timestampLiteral == null) return null;

		Date timestamp = stringToTimestamp(timestampLiteral.getLiteralDataString());
		return timestamp;
	}

	public static Date getTimestamp(XdiContext<?> xdiContext) {

		return getTimestamp(xdiContext, null);
	}

	/**
	 * Set the timestamp associated with a context node.
	 */
	public static void setTimestamp(XdiContext<?> xdiContext, XDIAddress modifierAddress, Date timestamp) {

		XDIAddress timestampXDIAddress = modifierAddress == null ? XDITimestampsConstants.XDI_ADD_AS_T : XDIAddressUtil.concatXDIAddresses(modifierAddress, XDITimestampsConstants.XDI_ARC_AS_T);

		String literalData = timestampToString(timestamp);

		XdiAttributeSingleton xdiAttributeSingleton = xdiContext.getXdiAttributeSingleton(timestampXDIAddress, true);
		XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(true);
		xdiValue.getContextNode().setLiteralString(literalData);
	}

	public static void setTimestamp(XdiContext<?> xdiContext, Date timestamp) {

		setTimestamp(xdiContext, null, timestamp);
	}
}
