package xdi2.core.constants;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Constants for XDI link contracts.
 * 
 * @author markus
 */
public final class XDILinkContractConstants {

	public static final XDIAddress XDI_ADD_DO = XDIAddress.create("$do");
	public static final XDIAddress XDI_ADD_EC_DO = XDIAddress.create("[$do]");
	public static final XDIAddress XDI_ADD_DO_VARIABLE = XDIAddress.create("{$do}");

	public static final XDIArc XDI_ARC_DO = XDIArc.create("$do");
	public static final XDIArc XDI_ARC_EC_DO = XDIArc.create("[$do]");
	public static final XDIArc XDI_ARC_DO_VARIABLE = XDIArc.create("{$do}");

	public static final XDIAddress XDI_ADD_PUBLIC = XDIAddress.create("$public");

	public static final XDIAddress XDI_ADD_TO_VARIABLE = XDIAddress.create("{$to}");
	public static final XDIAddress XDI_ADD_FROM_VARIABLE = XDIAddress.create("{$from}");

	public static final XDIArc XDI_ARC_TO_VARIABLE = XDIArc.create("{$to}");
	public static final XDIArc XDI_ARC_FROM_VARIABLE = XDIArc.create("{$from}");

	public static final XDIAddress XDI_ADD_ALL = XDIAddress.create("$all");
	public static final XDIAddress XDI_ADD_GET = XDIAddress.create("$get");
	public static final XDIAddress XDI_ADD_SET = XDIAddress.create("$set");
	public static final XDIAddress XDI_ADD_SET_DO = XDIAddress.create("$set{$do}");
	public static final XDIAddress XDI_ADD_SET_REF = XDIAddress.create("$set{$ref}");
	public static final XDIAddress XDI_ADD_DEL = XDIAddress.create("$del");
	public static final XDIAddress XDI_ADD_NOT = XDIAddress.create("$not");

	private XDILinkContractConstants() { }
}
