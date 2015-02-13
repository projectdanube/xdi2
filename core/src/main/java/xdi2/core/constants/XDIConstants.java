package xdi2.core.constants;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * General XDI syntax and graph constants.
 * 
 * @author markus
 */
public class XDIConstants {

	public static final Character CS_AUTHORITY_PERSONAL = new Character('='); 
	public static final Character CS_AUTHORITY_LEGAL = new Character('+'); 
	public static final Character CS_AUTHORITY_GENERAL = new Character('*'); 
	public static final Character CS_CLASS_UNRESERVED = new Character('#'); 
	public static final Character CS_CLASS_RESERVED = new Character('$'); 
	public static final Character CS_VALUE = new Character('&');
	public static final Character CS_MEMBER_UNORDERED = new Character('!'); 
	public static final Character CS_MEMBER_ORDERED = new Character('@');

	public static final String XS_ROOT = "()";
	public static final String XS_VARIABLE = "{}";
	public static final String XS_DEFINITION = "||";
	public static final String XS_COLLECTION = "[]";
	public static final String XS_ATTRIBUTE = "<>";

	public static final Character[] CS_ARRAY = new Character[] { CS_AUTHORITY_PERSONAL, CS_AUTHORITY_LEGAL, CS_AUTHORITY_GENERAL, CS_CLASS_UNRESERVED, CS_CLASS_RESERVED, CS_VALUE, CS_MEMBER_UNORDERED, CS_MEMBER_ORDERED };

	public static final XDIAddress XDI_ADD_ROOT = XDIAddress.create("");
	public static final XDIAddress XDI_ADD_CONTEXT = XDIAddress.create("");

	public static final XDIArc XDI_ARC_VALUE = XDIArc.create(XDIConstants.CS_VALUE.toString());
	public static final XDIArc XDI_ARC_LITERAL = XDIArc.create(XDIConstants.CS_VALUE.toString());
	public static final XDIAddress XDI_ADD_VALUE = XDIAddress.fromComponent(XDI_ARC_VALUE);
	public static final XDIAddress XDI_ADD_LITERAL = XDIAddress.fromComponent(XDI_ARC_LITERAL);

	public static final XDIArc XDI_ARC_COMMON_VARIABLE = XDIArc.create(XDIConstants.XS_VARIABLE);
	public static final XDIArc XDI_ARC_COMMON_DEFINITION = XDIArc.create(XDIConstants.XS_DEFINITION);
	public static final XDIAddress XDI_ADD_COMMON_VARIABLE = XDIAddress.fromComponent(XDI_ARC_COMMON_VARIABLE);
	public static final XDIAddress XDI_ADD_COMMON_DEFINITION = XDIAddress.fromComponent(XDI_ARC_COMMON_DEFINITION);

	public static final XDIArc XDI_ARC_TRUE = XDIArc.create("$true");
	public static final XDIArc XDI_ARC_FALSE = XDIArc.create("$false");
	public static final XDIAddress XDI_ADD_TRUE = XDIAddress.fromComponent(XDI_ARC_TRUE);
	public static final XDIAddress XDI_ADD_FALSE = XDIAddress.fromComponent(XDI_ARC_FALSE);

	private XDIConstants() { }
}
