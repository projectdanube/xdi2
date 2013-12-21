package xdi2.client.constants;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI client and discovery.
 * 
 * @author markus
 */
public final class XDIClientConstants {

	public static final XDI3Segment XRI_S_AC_XDI = XDI3Segment.create("[<$xdi>]");
	public static final XDI3Segment XRI_S_AC_URI = XDI3Segment.create("[<$uri>]");

	public static final XDI3Segment XRI_S_AS_XDI = XDI3Segment.create("<$xdi>");
	public static final XDI3Segment XRI_S_AS_URI = XDI3Segment.create("<$uri>");

	public static final XDI3SubSegment XRI_SS_AC_XDI = XDI3SubSegment.create("[<$xdi>]");
	public static final XDI3SubSegment XRI_SS_AC_URI = XDI3SubSegment.create("[<$uri>]");

	public static final XDI3SubSegment XRI_SS_AS_XDI = XDI3SubSegment.create("<$xdi>");
	public static final XDI3SubSegment XRI_SS_AS_URI = XDI3SubSegment.create("<$uri>");

	private XDIClientConstants() { }
}
