package xdi2.core.xri3;



public class XDI3SubSegment extends XDI3SyntaxComponent {

	private static final long serialVersionUID = -645927779266394209L;

	private Character gcs;
	private Character lcs;
	private String literal;
	private XDI3XRef xref;

	XDI3SubSegment(String string, Character gcs, Character lcs, String literal, XDI3XRef xref) {

		super(string);

		this.gcs = gcs;
		this.lcs = lcs;
		this.literal = literal;
		this.xref = xref;
	}

	public static XDI3SubSegment create(XDI3Parser parser, String string) {

		return parser.parseXDI3SubSegment(string);
	}

	public static XDI3SubSegment create(String string) {

		return create(XDI3ParserRegistry.getInstance().getParser(), string);
	}

	public boolean hasGCS() {

		return this.gcs != null;
	}

	public boolean hasLCS() {

		return this.lcs != null;
	}

	public boolean hasLiteral() {

		return this.literal != null;
	}

	public boolean hasXRef() {

		return this.xref != null;
	}

	public Character getGCS() {

		return this.gcs;
	}

	public Character getLCS() {

		return this.lcs;
	}

	public String getLiteral() {

		return this.literal;
	}

	public XDI3XRef getXRef() {

		return this.xref;
	}

	public boolean isGlobal() {

		return this.hasGCS();
	}

	public boolean isLocal() {

		return this.hasLCS() && ! this.hasGCS();
	}

	public boolean isPersistent() {

		return this.hasLCS() && this.getLCS().equals(XRI3Constants.LCS_BANG);
	}

	public boolean isReassignable() {

		return (this.hasGCS() && ! this.hasLCS()) || (this.hasLCS() && this.getLCS().equals(XRI3Constants.LCS_STAR));
	}
}
