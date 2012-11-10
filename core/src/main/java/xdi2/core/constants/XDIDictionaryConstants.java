package xdi2.core.constants;

import xdi2.core.xri3.impl.XRI3Segment;

/**
 * Constants for XDI dictionaries.
 * 
 * @author markus
 */
public final class XDIDictionaryConstants {

	public static final XRI3Segment XRI_S_IS = new XRI3Segment("$is");
	public static final XRI3Segment XRI_S_IS_BANG = new XRI3Segment("$is!");
	public static final XRI3Segment XRI_S_IS_IS = new XRI3Segment("$is$is");
	public static final XRI3Segment XRI_S_IS_TYPE = new XRI3Segment("$is+");

	private XDIDictionaryConstants() { }
}
