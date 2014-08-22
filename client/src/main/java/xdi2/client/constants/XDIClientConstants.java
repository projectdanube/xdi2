package xdi2.client.constants;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Constants for XDI client and discovery.
 * 
 * @author markus
 */
public final class XDIClientConstants {

	public static final XDIAddress XDI_ADD_AC_XDI = XDIAddress.create("[<$xdi>]");
	public static final XDIAddress XDI_ADD_AC_URI = XDIAddress.create("[<$uri>]");

	public static final XDIAddress XDI_ADD_AS_XDI = XDIAddress.create("<$xdi>");
	public static final XDIAddress XDI_ADD_AS_URI = XDIAddress.create("<$uri>");

	public static final XDIArc XDI_ARC_AC_XDI = XDIArc.create("[<$xdi>]");
	public static final XDIArc XDI_ARC_AC_URI = XDIArc.create("[<$uri>]");

	public static final XDIArc XDI_ARC_AS_XDI = XDIArc.create("<$xdi>");
	public static final XDIArc XDI_ARC_AS_URI = XDIArc.create("<$uri>");

	private XDIClientConstants() { }
}
