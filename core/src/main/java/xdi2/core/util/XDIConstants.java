package xdi2.core.util;

import xdi2.core.xri3.impl.XRI3Segment;

public class XDIConstants {

	public static final XRI3Segment XRI_S_CONTEXT = new XRI3Segment("()");
	public static final XRI3Segment XRI_S_LITERAL = new XRI3Segment("!");

	public static final XRI3Segment XRI_S_TRUE = new XRI3Segment("$true");
	public static final XRI3Segment XRI_S_FALSE = new XRI3Segment("$false");

	private XDIConstants() { }
}
