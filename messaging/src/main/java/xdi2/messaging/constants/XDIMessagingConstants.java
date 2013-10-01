package xdi2.messaging.constants;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI messaging.
 * 
 * @author markus
 */
public final class XDIMessagingConstants {

	public static final XDI3Segment XRI_S_ANONYMOUS = XDI3Segment.create("$anon");

	public static final XDI3Segment XRI_S_MSG = XDI3Segment.create("$msg");
	public static final XDI3Segment XRI_S_GET = XDI3Segment.create("$get");
	public static final XDI3Segment XRI_S_SET = XDI3Segment.create("$set");
	public static final XDI3Segment XRI_S_DEL = XDI3Segment.create("$del");
	public static final XDI3Segment XRI_S_DO = XDI3Segment.create("$do");
	public static final XDI3Segment XRI_S_FROM_ADDRESS = XDI3Segment.create("$set");
	public static final XDI3Segment XRI_S_TO_ADDRESS = XDI3Segment.create("$is()");

	public static final XDI3SubSegment XRI_SS_MSG = XDI3SubSegment.create("$msg");
	public static final XDI3SubSegment XRI_SS_GET = XDI3SubSegment.create("$get");
	public static final XDI3SubSegment XRI_SS_SET = XDI3SubSegment.create("$set");
	public static final XDI3SubSegment XRI_SS_DEL = XDI3SubSegment.create("$del");
	public static final XDI3SubSegment XRI_SS_DO = XDI3SubSegment.create("$do");

	private XDIMessagingConstants() { }
}
