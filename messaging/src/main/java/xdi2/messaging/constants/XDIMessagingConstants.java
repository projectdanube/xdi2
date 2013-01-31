package xdi2.messaging.constants;

import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI messaging.
 * 
 * @author markus
 */
public final class XDIMessagingConstants {

	public static final XDI3Segment XRI_S_ANONYMOUS = XDI3Segment.create("$");

	public static final XDI3Segment XRI_S_MSG = XDI3Segment.create("$msg");
	public static final XDI3Segment XRI_S_ADD = XDI3Segment.create("$add");
	public static final XDI3Segment XRI_S_GET = XDI3Segment.create("$get");
	public static final XDI3Segment XRI_S_MOD = XDI3Segment.create("$mod");
	public static final XDI3Segment XRI_S_DEL = XDI3Segment.create("$del");
	public static final XDI3Segment XRI_S_DO = XDI3Segment.create("$do");
	public static final XDI3Segment XRI_S_FROM_GRAPH = XDI3Segment.create("$add");
	public static final XDI3Segment XRI_S_TO_GRAPH = XDI3Segment.create("$is()");

	public static final XDI3Segment XRI_S_SECRET_TOKEN = XDI3Segment.create("" + Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("$secret")) + Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("$token")));
	public static final XDI3Segment XRI_S_OAUTH_TOKEN = XDI3Segment.create("" + Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("$oauth")) + Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("$token")));

	public static final XDI3SubSegment XRI_SS_MSG = XDI3SubSegment.create("$msg");
	public static final XDI3SubSegment XRI_SS_GET = XDI3SubSegment.create("$get");
	public static final XDI3SubSegment XRI_SS_ADD = XDI3SubSegment.create("$add");
	public static final XDI3SubSegment XRI_SS_MOD = XDI3SubSegment.create("$mod");
	public static final XDI3SubSegment XRI_SS_DEL = XDI3SubSegment.create("$del");
	public static final XDI3SubSegment XRI_SS_DO = XDI3SubSegment.create("$do");

	private XDIMessagingConstants() { }
}
