package xdi2.core.xri3;

public class XRI3Constants {

	public static final String XRI_SCHEME = "xri:";

	public static final String AUTHORITY_PREFIX = "";
	public static final String PATH_PREFIX = "/";
	public static final String QUERY_PREFIX = "?";
	public static final String FRAGMENT_PREFIX = "#";

	public static final String XREF_START = "(";
	public static final String XREF_END = ")";
	
	public static final Character GCS_EQUALS = new Character('='); 
	public static final Character GCS_AT = new Character('@'); 
	public static final Character GCS_PLUS = new Character('+'); 
	public static final Character GCS_DOLLAR = new Character('$'); 

	public static final Character LCS_STAR = new Character('*'); 
	public static final Character LCS_BANG = new Character('!'); 

	public static final Character[] GCS_ARRAY = new Character[] {
		GCS_EQUALS,
		GCS_AT,
		GCS_PLUS,
		GCS_DOLLAR
	};

	public static final Character[] LCS_ARRAY = new Character[] {
		LCS_STAR,
		LCS_BANG
	};
}
