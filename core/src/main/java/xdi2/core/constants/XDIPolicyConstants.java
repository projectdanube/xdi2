package xdi2.core.constants;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI policies.
 * 
 * @author markus
 */
public final class XDIPolicyConstants {

	public static final XDI3Segment XRI_S_IF = XDI3Segment.create("$if");

	public static final XDI3SubSegment XRI_SS_IF = XDI3SubSegment.create("$if");

	public static final XDI3SubSegment XRI_SS_AND = XDI3SubSegment.create("$and");
	public static final XDI3SubSegment XRI_SS_OR = XDI3SubSegment.create("$or");
	public static final XDI3SubSegment XRI_SS_NOT = XDI3SubSegment.create("$not");

	public static final XDI3Segment XRI_S_EQUALS = XDI3Segment.create("$equals");
	public static final XDI3Segment XRI_S_MATCHES = XDI3Segment.create("$matches");
	public static final XDI3Segment XRI_S_GREATER = XDI3Segment.create("$greater");
	public static final XDI3Segment XRI_S_LESSER = XDI3Segment.create("$lesser");
	public static final XDI3Segment XRI_S_IS = XDI3Segment.create("$is");

	private XDIPolicyConstants() { }
}
