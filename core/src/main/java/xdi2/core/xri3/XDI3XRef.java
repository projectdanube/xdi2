package xdi2.core.xri3;


public class XDI3XRef extends XDI3SyntaxComponent {

	private static final long serialVersionUID = 4875921569202236777L;

	private XDI3Segment segment;
	private XDI3Statement statement;
	private XDI3Segment partialSubject;
	private XDI3Segment partialPredicate;
	private String IRI;
	private String literal;

	XDI3XRef(String string, XDI3Segment segment, XDI3Statement statement, XDI3Segment partialSubject, XDI3Segment partialPredicate, String IRI, String literal) {

		super(string);

		this.segment = segment;
		this.statement = statement;
		this.partialSubject = partialSubject;
		this.partialPredicate = partialPredicate;
		this.IRI = IRI;
		this.literal = literal;
	}

	public static XDI3XRef create(XDI3Parser parser, String string) {

		return parser.parseXDI3XRef(string);
	}

	public static XDI3XRef create(String string) {

		return create(XDI3ParserRegistry.getInstance().getParser(), string);
	}

	public boolean hasSegment() {

		return this.segment != null;
	}

	public boolean hasStatement() {

		return this.statement != null;
	}

	public boolean hasPartialSubjectAndPredicate() {

		return this.partialSubject != null && this.partialPredicate != null;
	}

	public boolean hasIRI() {

		return this.IRI != null;
	}

	public boolean hasLiteral() {

		return this.literal != null;
	}

	public XDI3Segment getSegment() {

		return this.segment;
	}

	public XDI3Statement getStatement() {

		return this.statement;
	}

	public XDI3Segment getPartialSubject() {

		return this.partialSubject;
	}

	public XDI3Segment getPartialPredicate() {

		return this.partialPredicate;
	}

	public String getIRI() {

		return this.IRI;
	}

	public String getLiteral() {

		return this.literal;
	}

	public String getValue() {

		if (this.segment != null) return this.segment.toString();
		if (this.statement != null) return this.statement.toString();
		if (this.partialSubject != null && this.partialPredicate != null) return this.partialSubject.toString() + "/" + this.partialPredicate.toString();
		if (this.IRI != null) return this.IRI;
		if (this.literal != null) return this.literal;

		return null;
	}
}
