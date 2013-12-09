package xdi2.core.constants;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI link contracts.
 * 
 * @author markus
 */
public final class XDILinkContractConstants {

	public static final XDI3Segment XRI_S_DO = XDI3Segment.create("$do");

	public static final XDI3SubSegment XRI_SS_DO = XDI3SubSegment.create("$do");

	public static final XDI3Segment XRI_S_PUBLIC = XDI3Segment.create("$public");
	public static final XDI3Segment XRI_S_PUBLIC_DO = XDI3Segment.create("$public$do");

	public static final XDI3Segment XRI_S_TO = XDI3Segment.create("$to");
	public static final XDI3Segment XRI_S_TO_VARIABLE = XDI3Segment.create("{$to}");
	public static final XDI3Segment XRI_S_FROM = XDI3Segment.create("$from");
	public static final XDI3Segment XRI_S_FROM_VARIABLE = XDI3Segment.create("{$from}");

	public static final XDI3SubSegment XRI_SS_TO = XDI3SubSegment.create("$to");
	public static final XDI3SubSegment XRI_SS_TO_VARIABLE = XDI3SubSegment.create("{$to}");
	public static final XDI3SubSegment XRI_SS_FROM = XDI3SubSegment.create("$from");
	public static final XDI3SubSegment XRI_SS_FROM_VARIABLE = XDI3SubSegment.create("{$from}");

	public static final XDI3Segment XRI_S_ALL = XDI3Segment.create("$all");
	public static final XDI3Segment XRI_S_GET = XDI3Segment.create("$get");
	public static final XDI3Segment XRI_S_SET = XDI3Segment.create("$set");
	public static final XDI3Segment XRI_S_DEL = XDI3Segment.create("$del");
	public static final XDI3Segment XRI_S_NOT = XDI3Segment.create("$not");

	public static final XDI3SubSegment XRI_SS_ALL = XDI3SubSegment.create("$all");
	public static final XDI3SubSegment XRI_SS_GET = XDI3SubSegment.create("$get");
	public static final XDI3SubSegment XRI_SS_SET = XDI3SubSegment.create("$set");
	public static final XDI3SubSegment XRI_SS_DEL = XDI3SubSegment.create("$del");
	public static final XDI3SubSegment XRI_SS_NOT = XDI3SubSegment.create("$not");

	private XDILinkContractConstants() { }
}
