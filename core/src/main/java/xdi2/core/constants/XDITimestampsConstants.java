package xdi2.core.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Constants for XDI timestamps.
 * 
 * @author markus
 */
public final class XDITimestampsConstants {

	public static final XDIAddress XDI_ADD_AS_CREATED = XDIAddress.create("<#created>");
	public static final XDIAddress XDI_ADD_AS_EXPIRES = XDIAddress.create("<#expires>");
	public static final XDIAddress XDI_ADD_AS_T = XDIAddress.create("<$t>");

	public static final XDIArc XDI_ARC_AS_CREATED = XDIArc.create("<#created>");
	public static final XDIArc XDI_ARC_AS_EXPIRES = XDIArc.create("<#expires>");
	public static final XDIArc XDI_ARC_AS_T = XDIArc.create("<$t>");

	public static final DateFormat[] FORMATS_TIMESTAMP = new SimpleDateFormat[] {

		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),    // <-- default format
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	};

	private XDITimestampsConstants() { }
}
