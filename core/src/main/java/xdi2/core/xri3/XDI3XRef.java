package xdi2.core.xri3;


public class XDI3XRef extends XDI3SyntaxComponent {

	private static final long serialVersionUID = 4875921569202236777L;

	private String xs;
	private XDI3Segment segment;
	private XDI3Statement statement;
	private XDI3Segment partialSubject;
	private XDI3Segment partialPredicate;
	private String iri;
	private String literal;

	XDI3XRef(String string, String xs, XDI3Segment segment, XDI3Statement statement, XDI3Segment partialSubject, XDI3Segment partialPredicate, String iri, String literal) {

		super(string);

		this.xs = xs;
		this.segment = segment;
		this.statement = statement;
		this.partialSubject = partialSubject;
		this.partialPredicate = partialPredicate;
		this.iri = iri;
		this.literal = literal;
	}

	public static XDI3XRef create(String string) {

		return XDI3ParserRegistry.getInstance().getParser().parseXDI3XRef(string);
	}

	public static XDI3XRef fromComponents(String xs, XDI3Segment segment, XDI3Statement statement, XDI3Segment partialSubject, XDI3Segment partialPredicate, String iri, String literal) {

		if (xs == null) throw new IllegalArgumentException();
		if (segment == null && statement == null && partialSubject == null && partialPredicate == null && iri == null && literal == null) throw new IllegalArgumentException();

		StringBuffer buffer = new StringBuffer();
		buffer.append(xs.charAt(0));
		if (segment != null) buffer.append(segment.toString());
		if (statement != null) buffer.append(statement.toString());
		if (partialSubject != null && partialPredicate != null) buffer.append(partialSubject.toString() + "/" + partialPredicate.toString());
		if (iri != null) buffer.append(iri);
		if (literal != null) buffer.append(literal);
		buffer.append(xs.charAt(1));

		return new XDI3XRef(buffer.toString(), xs, segment, statement, partialSubject, partialPredicate, iri, literal);
	}

	public boolean isEmpty() {

		return this.toString().length() == 2;
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

	public boolean hasIri() {

		return this.iri != null;
	}

	public boolean hasLiteral() {

		return this.literal != null;
	}

	public String getXs() {

		return this.xs;
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

	public String getIri() {

		return this.iri;
	}

	public String getLiteral() {

		return this.literal;
	}

	public String getValue() {

		if (this.segment != null) return this.segment.toString();
		if (this.statement != null) return this.statement.toString();
		if (this.partialSubject != null && this.partialPredicate != null) return this.partialSubject.toString() + "/" + this.partialPredicate.toString();
		if (this.iri != null) return this.iri;
		if (this.literal != null) return this.literal;

		return null;
	}
}
