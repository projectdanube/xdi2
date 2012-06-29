package xdi2.core.features.linkcontracts.util;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * A class that provides various constants for XDI link contracts.
 * 
 * @author markus
 */
public final class XDILinkContractConstants {

	public static final XRI3Segment XRI_S_DO = new XRI3Segment("$do");
	public static final XRI3Segment XRI_S_IF = new XRI3Segment("$if");
	public static final XRI3Segment XRI_S_IS_DO = new XRI3Segment("$is$do");

	public static final XRI3SubSegment XRI_SS_DO = new XRI3SubSegment("$do");
	public static final XRI3SubSegment XRI_SS_IF = new XRI3SubSegment("$if");
	public static final XRI3Segment XRI_SS_IS_DO = new XRI3Segment("$is$do");
	
	public static final XRI3Segment XRI_S_GET = new XRI3Segment("$get");
	public static final XRI3Segment XRI_S_ALL = new XRI3Segment("$all");
	
	public static final XRI3Segment XRI_S_ADD = new XRI3Segment("$add");
	public static final XRI3Segment XRI_S_MOD = new XRI3Segment("$mod");
	
	public static final XRI3Segment XRI_S_DEL = new XRI3Segment("$del");
	

	public static final XRI3SubSegment XRI_SS_GET = new XRI3SubSegment("$get");
	public static final XRI3SubSegment XRI_SS_ALL = new XRI3SubSegment("$all");
	public static final XRI3SubSegment XRI_SS_ADD = new XRI3SubSegment("$add");
	public static final XRI3SubSegment XRI_SS_MOD = new XRI3SubSegment("$mod");
	public static final XRI3SubSegment XRI_SS_DEL = new XRI3SubSegment("$del");
	
	private XDILinkContractConstants() { }
}
