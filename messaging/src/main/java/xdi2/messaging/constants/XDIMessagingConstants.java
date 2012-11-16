package xdi2.messaging.constants;

import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3SubSegment;

/**
 * Constants for XDI messaging.
 * 
 * @author markus
 */
public final class XDIMessagingConstants {

	public static final XDI3Segment XRI_S_ANONYMOUS = new XDI3Segment("$");

	public static final XDI3Segment XRI_S_MSG = new XDI3Segment("$msg");
	public static final XDI3Segment XRI_S_GET = new XDI3Segment("$get");
	public static final XDI3Segment XRI_S_ADD = new XDI3Segment("$add");
	public static final XDI3Segment XRI_S_MOD = new XDI3Segment("$mod");
	public static final XDI3Segment XRI_S_DEL = new XDI3Segment("$del");
	public static final XDI3Segment XRI_S_DO = new XDI3Segment("$do");
	public static final XDI3Segment XRI_S_FROM_GRAPH = new XDI3Segment("$add");
	public static final XDI3Segment XRI_S_TO_GRAPH = new XDI3Segment("$is()");

	public static final XDI3Segment XRI_S_SECRET_TOKEN = new XDI3Segment("" + Multiplicity.entitySingletonArcXri(new XDI3SubSegment("$secret")) + Multiplicity.attributeSingletonArcXri(new XDI3SubSegment("$token")));
	public static final XDI3Segment XRI_S_OAUTH_TOKEN = new XDI3Segment("" + Multiplicity.entitySingletonArcXri(new XDI3SubSegment("$oauth")) + Multiplicity.attributeSingletonArcXri(new XDI3SubSegment("$token")));

	public static final XDI3SubSegment XRI_SS_MSG = new XDI3SubSegment("$msg");
	public static final XDI3SubSegment XRI_SS_GET = new XDI3SubSegment("$get");
	public static final XDI3SubSegment XRI_SS_ADD = new XDI3SubSegment("$add");
	public static final XDI3SubSegment XRI_SS_MOD = new XDI3SubSegment("$mod");
	public static final XDI3SubSegment XRI_SS_DEL = new XDI3SubSegment("$del");
	public static final XDI3SubSegment XRI_SS_DO = new XDI3SubSegment("$do");

	private XDIMessagingConstants() { }
}
