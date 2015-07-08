package xdi2.core.constants;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Constants for XDI link contracts.
 */
public final class XDILinkContractConstants {

	public static final XDIAddress XDI_ADD_DO = XDIAddress.create("$do");
	public static final XDIAddress XDI_ADD_EC_DO = XDIAddress.create("[$do]");
	public static final XDIAddress XDI_ADD_V_DO = XDIAddress.create("{$do}");
	public static final XDIAddress XDI_ADD_VC_DO = XDIAddress.create("{[$do]}");

	public static final XDIArc XDI_ARC_DO = XDIArc.create("$do");
	public static final XDIArc XDI_ARC_EC_DO = XDIArc.create("[$do]");
	public static final XDIArc XDI_ARC_V_DO = XDIArc.create("{$do}");
	public static final XDIArc XDI_ARC_VC_DO = XDIArc.create("{[$do]}");

	public static final XDIAddress XDI_ADD_PUBLIC = XDIAddress.create("$public");

	public static final XDIAddress XDI_ADD_V_TO = XDIAddress.create("{$to}");
	public static final XDIAddress XDI_ADD_V_FROM = XDIAddress.create("{$from}");

	public static final XDIArc XDI_ARC_V_TO = XDIArc.create("{$to}");
	public static final XDIArc XDI_ARC_V_FROM = XDIArc.create("{$from}");

	public static final XDIAddress XDI_ADD_ALL = XDIAddress.create("$all");
	public static final XDIAddress XDI_ADD_GET = XDIAddress.create("$get");
	public static final XDIAddress XDI_ADD_SET = XDIAddress.create("$set");
	public static final XDIAddress XDI_ADD_SET_DO = XDIAddress.create("$set{$do}");
	public static final XDIAddress XDI_ADD_SET_REF = XDIAddress.create("$set{$ref}");
	public static final XDIAddress XDI_ADD_DEL = XDIAddress.create("$del");
	public static final XDIAddress XDI_ADD_PUSH = XDIAddress.create("$push");
	public static final XDIAddress XDI_ADD_IS_PUSH = XDIAddress.create("$is$push");

	public static final XDIAddress XDI_ADD_NOT = XDIAddress.create("$not");

	public static final XDIAddress XDI_ADD_TO_PEER_ROOT_ARC = XDIAddress.create("$is()");

	private XDILinkContractConstants() { }
}
