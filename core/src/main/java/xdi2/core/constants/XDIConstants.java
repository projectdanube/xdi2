package xdi2.core.constants;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * General XDI constants.
 * 
 * @author markus
 */
public class XDIConstants {

	public static final XRI3Segment XRI_S_ROOT = new XRI3Segment("()");
	public static final XRI3Segment XRI_S_CONTEXT = new XRI3Segment("()");
	public static final XRI3Segment XRI_S_LITERAL = new XRI3Segment("!");

	public static final XRI3Segment XRI_S_TRUE = new XRI3Segment("$true");
	public static final XRI3Segment XRI_S_FALSE = new XRI3Segment("$false");

	public static final XRI3SubSegment XRI_SS_CONTEXT = new XRI3SubSegment("()");
	public static final XRI3SubSegment XRI_SS_LITERAL = new XRI3SubSegment("!");

	private XDIConstants() { }
}
