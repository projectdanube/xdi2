package xdi2.messaging.constants;

import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI messaging.
 * 
 * @author markus
 */
public final class XDIMessagingConstants {

	public static final XDI3SubSegment XRI_SS_MSG = XDI3SubSegment.create("$msg");
	public static final XDI3SubSegment XRI_SS_GET = XDI3SubSegment.create("$get");
	public static final XDI3SubSegment XRI_SS_SET = XDI3SubSegment.create("$set");
	public static final XDI3SubSegment XRI_SS_DEL = XDI3SubSegment.create("$del");
	public static final XDI3SubSegment XRI_SS_DO = XDI3SubSegment.create("$do");

	public static final XDI3Segment XRI_S_MSG = XDI3Segment.fromComponent(XRI_SS_MSG);
	public static final XDI3Segment XRI_S_GET = XDI3Segment.fromComponent(XRI_SS_GET);
	public static final XDI3Segment XRI_S_GET_BOOLEAN = XDI3Util.concatXris(XRI_S_GET, XDIDictionaryConstants.XRI_S_BOOLEAN);
	public static final XDI3Segment XRI_S_GET_NUMBER = XDI3Util.concatXris(XRI_S_GET, XDIDictionaryConstants.XRI_S_NUMBER);
	public static final XDI3Segment XRI_S_SET = XDI3Segment.fromComponent(XRI_SS_SET);
	public static final XDI3Segment XRI_S_DEL = XDI3Segment.fromComponent(XRI_SS_DEL);
	public static final XDI3Segment XRI_S_DO = XDI3Segment.fromComponent(XRI_SS_DO);

	public static final XDI3Segment XRI_S_FROM_PEER_ROOT_XRI = XDI3Segment.create("$set");
	public static final XDI3Segment XRI_S_TO_PEER_ROOT_XRI = XDI3Segment.create("$is()");

	private XDIMessagingConstants() { }
}
