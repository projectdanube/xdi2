package xdi2.core.constants;

import xdi2.core.xri3.XDI3Segment;

/**
 * Constants for XDI dictionaries.
 * 
 * @author markus
 */
public final class XDIDictionaryConstants {

	public static final XDI3Segment XRI_S_IS = XDI3Segment.create("$is");

	public static final XDI3Segment XRI_S_IS_TYPE = XDI3Segment.create("$is+");

	public static final XDI3Segment XRI_S_REF = XDI3Segment.create("$ref");
	public static final XDI3Segment XRI_S_IS_REF = XDI3Segment.create("$is$ref");

	public static final XDI3Segment XRI_S_REP = XDI3Segment.create("$rep");
	public static final XDI3Segment XRI_S_IS_REP = XDI3Segment.create("$is$rep");

	private XDIDictionaryConstants() { }
}
