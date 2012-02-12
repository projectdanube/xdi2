package xdi2.util;

import xdi2.xri3.impl.XRI3Authority;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public class XDIConstants {

	public static final XRI3Authority XRI_A_CONTEXT = new XRI3Authority("()");

	public static final XRI3Segment XRI_S_CONTEXT = new XRI3Segment("()");
	public static final XRI3Segment XRI_S_LITERAL = new XRI3Segment("!");

	public static final XRI3Segment XRI_S_TRUE = new XRI3Segment("$true");
	public static final XRI3Segment XRI_S_FALSE = new XRI3Segment("$false");

	public static final XRI3Segment XRI_S_MSG = new XRI3Segment("$msg");
	public static final XRI3Segment XRI_S_GET = new XRI3Segment("$get");
	public static final XRI3Segment XRI_S_ADD = new XRI3Segment("$add");
	public static final XRI3Segment XRI_S_MOD = new XRI3Segment("$mod");
	public static final XRI3Segment XRI_S_DEL = new XRI3Segment("$del");
	public static final XRI3Segment XRI_S_DO = new XRI3Segment("$do");

	public static final XRI3SubSegment XRI_SS_MSG = new XRI3SubSegment("$msg");
	public static final XRI3SubSegment XRI_SS_GET = new XRI3SubSegment("$get");
	public static final XRI3SubSegment XRI_SS_ADD = new XRI3SubSegment("$add");
	public static final XRI3SubSegment XRI_SS_MOD = new XRI3SubSegment("$mod");
	public static final XRI3SubSegment XRI_SS_DEL = new XRI3SubSegment("$del");
	public static final XRI3SubSegment XRI_SS_DO = new XRI3SubSegment("$do");

	private XDIConstants() { }
}
