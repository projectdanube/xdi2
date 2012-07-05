package xdi2.core.constants;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * Constants for XDI link contracts.
 * 
 * @author markus
 */
public final class XDILinkContractConstants {

	public static final XRI3Segment XRI_S_DO = new XRI3Segment("$do");
	public static final XRI3Segment XRI_S_IF = new XRI3Segment("$if");

	public static final XRI3SubSegment XRI_SS_DO = new XRI3SubSegment("$do");
	public static final XRI3SubSegment XRI_SS_IF = new XRI3SubSegment("$if");

	private XDILinkContractConstants() { }
}
