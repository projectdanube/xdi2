package xdi2.core.xri3;

public class XDI3Constants {

	public static final String XRI_SCHEME = "xri:";

	public static final String AUTHORITY_PREFIX = "";
	public static final String PATH_PREFIX = "/";
	public static final String QUERY_PREFIX = "?";
	public static final String FRAGMENT_PREFIX = "#";

	public static final Character CS_EQUALS = new Character('='); 
	public static final Character CS_AT = new Character('@'); 
	public static final Character CS_PLUS = new Character('+'); 
	public static final Character CS_DOLLAR = new Character('$'); 
	public static final Character CS_STAR = new Character('*'); 
	public static final Character CS_BANG = new Character('!'); 

	public static final String CF_ROOT = "()";
	public static final String CF_ELEMENT = "[]";
	public static final String CF_VALUE = "<>";
	public static final String CF_VARIABLE = "{}";

	public static final Character[] CS_ARRAY = new Character[] {
		CS_EQUALS,
		CS_AT,
		CS_PLUS,
		CS_DOLLAR,
		CS_STAR,
		CS_BANG
	};

	public static final String[] CF_ARRAY = new String[] {
		CF_ROOT,
		CF_ELEMENT,
		CF_VALUE,
		CF_VARIABLE
	};
}
