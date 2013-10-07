package xdi2.core.constants;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * General XDI syntax and graph constants.
 * 
 * @author markus
 */
public class XDIConstants {

	public static final Character CS_EQUALS = new Character('='); 
	public static final Character CS_AT = new Character('@'); 
	public static final Character CS_PLUS = new Character('+'); 
	public static final Character CS_DOLLAR = new Character('$'); 
	public static final Character CS_STAR = new Character('*'); 
	public static final Character CS_BANG = new Character('!'); 
	public static final Character CS_ORDER = new Character('#');
	public static final Character CS_VALUE = new Character('&');

	public static final String XS_ROOT = "()";
	public static final String XS_VARIABLE = "{}";
	public static final String XS_CLASS = "[]";
	public static final String XS_ATTRIBUTE = "<>";

	public static final Character[] CS_ARRAY = new Character[] { CS_EQUALS, CS_AT, CS_PLUS, CS_DOLLAR, CS_STAR, CS_BANG, CS_ORDER, CS_VALUE };

	public static final XDI3Segment XRI_S_ROOT = XDI3Segment.create(XDIConstants.XS_ROOT);
	public static final XDI3Segment XRI_S_CONTEXT = XDI3Segment.create(XDIConstants.XS_ROOT);
	public static final XDI3Segment XRI_S_VALUE = XDI3Segment.create(XDIConstants.CS_VALUE.toString());
	public static final XDI3Segment XRI_S_LITERAL = XDI3Segment.create(XDIConstants.CS_VALUE.toString());
	public static final XDI3Segment XRI_S_VARIABLE = XDI3Segment.create(XDIConstants.XS_VARIABLE);

	public static final XDI3Segment XRI_S_TRUE = XDI3Segment.create("$true");
	public static final XDI3Segment XRI_S_FALSE = XDI3Segment.create("$false");

	public static final XDI3SubSegment XRI_SS_TRUE = XDI3SubSegment.create("$true");
	public static final XDI3SubSegment XRI_SS_FALSE = XDI3SubSegment.create("$false");

	public static final XDI3SubSegment XRI_SS_CONTEXT = XDI3SubSegment.create(XDIConstants.XS_ROOT);
	public static final XDI3SubSegment XRI_SS_LITERAL = XDI3SubSegment.create(XDIConstants.CS_VALUE.toString());

	public static final XDI3Segment XRI_S_XDI = XDI3Segment.create("$xdi");

	private XDIConstants() { }
}
