package xdi2.core.constants;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Constants for XDI policies.
 * 
 * @author markus
 */
public final class XDIPolicyConstants {

	public static final XDIAddress XDI_ADD_IF = XDIAddress.create("$if");

	public static final XDIArc XDI_ARC_IF = XDIArc.create("$if");

	public static final XDIAddress XDI_ADD_HOLD = XDIAddress.create("$hold");
	public static final XDIAddress XDI_ADD_HOLD_PUSH = XDIAddress.create("$hold$push");

	public static final XDIArc XDI_ARC_AND = XDIArc.create("$and");
	public static final XDIArc XDI_ARC_OR = XDIArc.create("$or");
	public static final XDIArc XDI_ARC_NOT = XDIArc.create("$not");

	public static final XDIAddress XDI_ADD_EQUALS = XDIAddress.create("$equals");
	public static final XDIAddress XDI_ADD_MATCHES = XDIAddress.create("$matches");
	public static final XDIAddress XDI_ADD_GREATER = XDIAddress.create("$greater");
	public static final XDIAddress XDI_ADD_LESSER = XDIAddress.create("$lesser");
	public static final XDIAddress XDI_ADD_IS = XDIAddress.create("$is");

	private XDIPolicyConstants() { }
}
