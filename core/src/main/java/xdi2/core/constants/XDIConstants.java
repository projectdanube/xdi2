package xdi2.core.constants;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

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
	public static final Character CS_UNORDERED = new Character('!'); 
	public static final Character CS_ORDERED = new Character('@');

	public static final String XS_ROOT = "()";
	public static final String XS_VARIABLE = "{}";
	public static final String XS_DEFINITION = "||";
	public static final String XS_CLASS = "[]";
	public static final String XS_ATTRIBUTE = "<>";

	public static final Character[] CS_ARRAY = new Character[] { CS_AUTHORITY_PERSONAL, CS_AUTHORITY_LEGAL, CS_AUTHORITY_GENERAL, CS_CLASS_UNRESERVED, CS_CLASS_RESERVED, CS_VALUE, CS_UNORDERED, CS_ORDERED };

	public static final XDI3Segment XRI_S_ROOT = XDI3Segment.create("");
	public static final XDI3Segment XRI_S_CONTEXT = XDI3Segment.create("");
	public static final XDI3Segment XRI_S_VALUE = XDI3Segment.create(XDIConstants.CS_VALUE.toString());
	public static final XDI3Segment XRI_S_LITERAL = XDI3Segment.create(XDIConstants.CS_VALUE.toString());

	public static final XDI3SubSegment XRI_SS_VALUE = XDI3SubSegment.create(XDIConstants.CS_VALUE.toString());
	public static final XDI3SubSegment XRI_SS_LITERAL = XDI3SubSegment.create(XDIConstants.CS_VALUE.toString());

	public static final XDI3Segment XRI_S_TRUE = XDI3Segment.create("$true");
	public static final XDI3Segment XRI_S_FALSE = XDI3Segment.create("$false");

	public static final XDI3SubSegment XRI_SS_TRUE = XDI3SubSegment.create("$true");
	public static final XDI3SubSegment XRI_SS_FALSE = XDI3SubSegment.create("$false");

	public static final XDI3Segment XRI_S_VARIABLE = XDI3Segment.create(XDIConstants.XS_VARIABLE);

	private XDIConstants() { }
}
