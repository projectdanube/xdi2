package xdi2.core.constants;

import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI link contracts.
 * 
 * @author markus
 */
public final class XDILinkContractConstants {

	public static final XDI3Segment XRI_S_DO = XDI3Segment.create("$do");
	public static final XDI3Segment XRI_S_IF = XDI3Segment.create("$if");

	public static final XDI3SubSegment XRI_SS_DO = XDI3SubSegment.create("$do");
	public static final XDI3SubSegment XRI_SS_IF = XDI3SubSegment.create("$if");

	public static final XDI3Segment XRI_S_ALL = XDI3Segment.create("$all");
	public static final XDI3Segment XRI_S_GET = XDI3Segment.create("$get");
	public static final XDI3Segment XRI_S_ADD = XDI3Segment.create("$add");
	public static final XDI3Segment XRI_S_MOD = XDI3Segment.create("$mod");
	public static final XDI3Segment XRI_S_DEL = XDI3Segment.create("$del");

	public static final XDI3SubSegment XRI_SS_ALL = XDI3SubSegment.create("$all");
	public static final XDI3SubSegment XRI_SS_GET = XDI3SubSegment.create("$get");
	public static final XDI3SubSegment XRI_SS_ADD = XDI3SubSegment.create("$add");
	public static final XDI3SubSegment XRI_SS_MOD = XDI3SubSegment.create("$mod");
	public static final XDI3SubSegment XRI_SS_DEL = XDI3SubSegment.create("$del");

	public static final XDI3SubSegment XRI_SS_AND = Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("$and"));
	public static final XDI3SubSegment XRI_SS_OR = Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("$or"));
	public static final XDI3SubSegment XRI_SS_NOT = Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("$not"));

	public static final XDI3Segment XRI_S_EQUALS = XDI3Segment.create("$equals");
	public static final XDI3Segment XRI_S_GREATER = XDI3Segment.create("$greater");
	public static final XDI3Segment XRI_S_LESSER = XDI3Segment.create("$lesser");
	public static final XDI3Segment XRI_S_IS = XDI3Segment.create("$is");

	private XDILinkContractConstants() { }
}
