package xdi2.core.constants;

import xdi2.core.syntax.XDIAddress;

/**
 * Constants for XDI dictionaries.
 * 
 * @author markus
 */
public final class XDIDictionaryConstants {

	public static final XDIAddress XDI_ADD_IS = XDIAddress.create("$is");

	public static final XDIAddress XDI_ADD_TYPE = XDIAddress.create("#");
	public static final XDIAddress XDI_ADD_IS_TYPE = XDIAddress.create("$is#");

	public static final XDIAddress XDI_ADD_REF = XDIAddress.create("$ref");
	public static final XDIAddress XDI_ADD_IS_REF = XDIAddress.create("$is$ref");

	public static final XDIAddress XDI_ADD_REP = XDIAddress.create("$rep");
	public static final XDIAddress XDI_ADD_IS_REP = XDIAddress.create("$is$rep");

	public static final XDIAddress XDI_ADD_HAS = XDIAddress.create("$has");
	public static final XDIAddress XDI_ADD_IS_HAS = XDIAddress.create("$is$has");

	public static final XDIAddress XDI_ADD_BOOLEAN = XDIAddress.create("$b");
	public static final XDIAddress XDI_ADD_NUMBER = XDIAddress.create("$n");

	private XDIDictionaryConstants() { }
}
