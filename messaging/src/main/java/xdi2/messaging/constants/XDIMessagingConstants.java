package xdi2.messaging.constants;

import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * Constants for XDI messaging.
 * 
 * @author markus
 */
public final class XDIMessagingConstants {

	public static final XRI3Segment XRI_S_ANONYMOUS = new XRI3Segment("$");

	public static final XRI3Segment XRI_S_MSG = new XRI3Segment("$msg");
	public static final XRI3Segment XRI_S_GET = new XRI3Segment("$get");
	public static final XRI3Segment XRI_S_ADD = new XRI3Segment("$add");
	public static final XRI3Segment XRI_S_MOD = new XRI3Segment("$mod");
	public static final XRI3Segment XRI_S_DEL = new XRI3Segment("$del");
	public static final XRI3Segment XRI_S_DO = new XRI3Segment("$do");
	public static final XRI3Segment XRI_S_FROM_GRAPH = new XRI3Segment("$add");
	public static final XRI3Segment XRI_S_TO_GRAPH = new XRI3Segment("$is()");

	public static final XRI3Segment XRI_S_SECRET_TOKEN = new XRI3Segment("" + Multiplicity.entitySingletonArcXri(new XRI3SubSegment("$secret")) + Multiplicity.attributeSingletonArcXri(new XRI3SubSegment("$token")));
	public static final XRI3Segment XRI_S_OAUTH_TOKEN = new XRI3Segment("" + Multiplicity.entitySingletonArcXri(new XRI3SubSegment("$oauth")) + Multiplicity.attributeSingletonArcXri(new XRI3SubSegment("$token")));

	public static final XRI3SubSegment XRI_SS_MSG = new XRI3SubSegment("$msg");
	public static final XRI3SubSegment XRI_SS_GET = new XRI3SubSegment("$get");
	public static final XRI3SubSegment XRI_SS_ADD = new XRI3SubSegment("$add");
	public static final XRI3SubSegment XRI_SS_MOD = new XRI3SubSegment("$mod");
	public static final XRI3SubSegment XRI_SS_DEL = new XRI3SubSegment("$del");
	public static final XRI3SubSegment XRI_SS_DO = new XRI3SubSegment("$do");

	private XDIMessagingConstants() { }
}
