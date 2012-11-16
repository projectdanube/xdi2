package xdi2.core.constants;

import xdi2.core.xri3.impl.XDI3Segment;

/**
 * Constants for XDI dictionaries.
 * 
 * @author markus
 */
public final class XDIDictionaryConstants {

	public static final XDI3Segment XRI_S_IS = new XDI3Segment("$is");
	public static final XDI3Segment XRI_S_IS_BANG = new XDI3Segment("$is!");
	public static final XDI3Segment XRI_S_IS_IS = new XDI3Segment("$is$is");
	public static final XDI3Segment XRI_S_IS_TYPE = new XDI3Segment("$is+");

	private XDIDictionaryConstants() { }
}
