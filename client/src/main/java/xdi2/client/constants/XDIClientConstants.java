package xdi2.client.constants;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI client and discovery.
 * 
 * @author markus
 */
public final class XDIClientConstants {

	public static final XDI3Segment XRI_S_XDI = XDI3Segment.create("<$xdi>");
	public static final XDI3Segment XRI_S_URI = XDI3Segment.create("<$uri>");

	public static final XDI3SubSegment XRI_SS_XDI = XDI3SubSegment.create("<$xdi>");
	public static final XDI3SubSegment XRI_SS_URI = XDI3SubSegment.create("<$uri>");

	private XDIClientConstants() { }
}
