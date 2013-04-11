package xdi2.core.xri3;

public class XDI3Constants {

	public static final String XRI_SCHEME = "xri:";

/*	public static final String AUTHORITY_PREFIX = "";
	public static final String PATH_PREFIX = "/";
	public static final String QUERY_PREFIX = "?";
	public static final String FRAGMENT_PREFIX = "#";*/

	public static final Character CS_EQUALS = new Character('='); 
	public static final Character CS_AT = new Character('@'); 
	public static final Character CS_PLUS = new Character('+'); 
	public static final Character CS_DOLLAR = new Character('$'); 
	public static final Character CS_STAR = new Character('*'); 
	public static final Character CS_BANG = new Character('!'); 
	public static final Character CS_VALUE = new Character('#');

	public static final Character C_SINGLETON = new Character('|');

	public static final Character C_ATTRIBUTE = new Character('&');

	public static final String XS_ROOT = "()";
	public static final String XS_ELEMENT = "[]";
	public static final String XS_VARIABLE = "{}";

	public static final Character[] CS_ARRAY = new Character[] {
		CS_EQUALS,
		CS_AT,
		CS_PLUS,
		CS_DOLLAR,
		CS_STAR,
		CS_BANG,
		CS_VALUE
	};

	public static final String[] XS_ARRAY = new String[] {
		XS_ROOT,
		XS_ELEMENT,
		XS_VARIABLE
	};
}
