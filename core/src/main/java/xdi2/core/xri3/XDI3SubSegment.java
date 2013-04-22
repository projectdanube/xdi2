package xdi2.core.xri3;

public class XDI3SubSegment extends XDI3SyntaxComponent {

	private static final long serialVersionUID = -645927779266394209L;

	private Character cs;
	private boolean singleton;
	private boolean attribute;
	private String literal;
	private XDI3XRef xref;

	XDI3SubSegment(String string, Character cs, boolean singleton, boolean attribute, String literal, XDI3XRef xref) {

		super(string);

		this.cs = cs;
		this.singleton = singleton;
		this.attribute = attribute;
		this.literal = literal;
		this.xref = xref;
	}

	public static XDI3SubSegment create(String string) {

		return XDI3ParserRegistry.getInstance().getParser().parseXDI3SubSegment(string);
	}

	public boolean hasCs() {

		return this.cs != null;
	}

	public boolean isSingleton() {

		return this.singleton;
	}

	public boolean isAttribute() {

		return this.attribute;
	}

	public boolean hasLiteral() {

		return this.literal != null;
	}

	public boolean hasXRef() {

		return this.xref != null;
	}

	public Character getCs() {

		return this.cs;
	}

	public String getLiteral() {

		return this.literal;
	}

	public XDI3XRef getXRef() {

		return this.xref;
	}
}
