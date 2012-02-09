package xdi2.util;

import xdi2.xri3.impl.XRI3Segment;

public class XDIConstants {

	public static final XRI3Segment XRI_CONTEXT = new XRI3Segment("()");
	public static final XRI3Segment XRI_LITERAL = new XRI3Segment("!");

	public static final XRI3Segment XRI_TRUE = new XRI3Segment("$true");
	public static final XRI3Segment XRI_FALSE = new XRI3Segment("$false");

	private XDIConstants() { }
}
