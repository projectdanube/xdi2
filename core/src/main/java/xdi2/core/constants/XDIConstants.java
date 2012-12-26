package xdi2.core.constants;

import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3SubSegment;

/**
 * General XDI constants.
 * 
 * @author markus
 */
public class XDIConstants {

	public static final XDI3Segment XRI_S_ROOT = new XDI3Segment("()");
	public static final XDI3Segment XRI_S_CONTEXT = new XDI3Segment("()");
	public static final XDI3Segment XRI_S_LITERAL = new XDI3Segment("!");

	public static final XDI3Segment XRI_S_VARIABLE_SINGLE = new XDI3Segment("($)");
	public static final XDI3Segment XRI_S_VARIABLE_MULTIPLE_LOCAL = new XDI3Segment("($$!)");

	public static final XDI3Segment XRI_S_TRUE = new XDI3Segment("$true");
	public static final XDI3Segment XRI_S_FALSE = new XDI3Segment("$false");

	public static final XDI3SubSegment XRI_SS_TRUE = new XDI3SubSegment("$true");
	public static final XDI3SubSegment XRI_SS_FALSE = new XDI3SubSegment("$false");

	public static final XDI3Segment XRI_S_PUBLIC = new XDI3Segment("$public");

	public static final XDI3SubSegment XRI_SS_CONTEXT = new XDI3SubSegment("()");
	public static final XDI3SubSegment XRI_SS_LITERAL = new XDI3SubSegment("!");

	private XDIConstants() { }
}
