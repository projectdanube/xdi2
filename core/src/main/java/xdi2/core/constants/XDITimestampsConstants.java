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

	public static final XDIArc XDI_ARC_AS_CREATION = XDIArc.create("<#creation>");
	public static final XDIArc XDI_ARC_AS_EXPIRATION = XDIArc.create("<#expiration>");
	public static final XDIArc XDI_ARC_AS_T = XDIArc.create("<$t>");

	public static final XDIAddress XDI_ADD_AS_CREATION = XDIAddress.fromComponent(XDI_ARC_AS_CREATION);
	public static final XDIAddress XDI_ADD_AS_EXPIRATION = XDIAddress.fromComponent(XDI_ARC_AS_EXPIRATION);
	public static final XDIAddress XDI_ADD_AS_T = XDIAddress.fromComponent(XDI_ARC_AS_T);

	public static final DateFormat[] FORMATS_TIMESTAMP = new SimpleDateFormat[] {

		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),    // <-- default format
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	};

	private XDITimestampsConstants() { }
}
