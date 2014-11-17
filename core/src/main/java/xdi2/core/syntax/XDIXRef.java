package xdi2.core.syntax;



public class XDIXRef extends XDIIdentifier {

	private static final long serialVersionUID = 4875921569202236777L;

	private String xs;
	private XDIAddress XDIaddress;
	private XDIAddress partialSubject;
	private XDIAddress partialPredicate;
	private String iri;
	private String literal;

	XDIXRef(String string, String xs, XDIAddress XDIaddress, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		super(string);

		this.xs = xs;
		this.XDIaddress = XDIaddress;
		this.partialSubject = partialSubject;
		this.partialPredicate = partialPredicate;
		this.iri = iri;
		this.literal = literal;
	}

	public static XDIXRef create(String string) {

		return ParserRegistry.getInstance().getParser().parseXDIXRef(string);
	}

	public static XDIXRef fromComponents(String xs, XDIAddress XDIaddress, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		if (xs == null) throw new IllegalArgumentException();
		if (XDIaddress == null && partialSubject == null && partialPredicate == null && iri == null && literal == null) throw new IllegalArgumentException();
		if (XDIaddress != null && (partialSubject != null || partialPredicate != null || iri != null || literal != null)) throw new IllegalArgumentException();
		if (partialSubject != null && (XDIaddress != null || partialPredicate == null || iri != null || literal != null)) throw new IllegalArgumentException();
		if (partialPredicate != null && (XDIaddress != null || partialSubject == null || iri != null || literal != null)) throw new IllegalArgumentException();
		if (iri != null && (XDIaddress != null || partialSubject != null || partialPredicate != null || literal != null)) throw new IllegalArgumentException();
		if (literal != null && (XDIaddress != null || partialSubject != null || partialPredicate != null || iri != null)) throw new IllegalArgumentException();

		StringBuffer buffer = new StringBuffer();
		buffer.append(xs.charAt(0));
		if (XDIaddress != null) buffer.append(XDIaddress.toString());
		if (partialSubject != null && partialPredicate != null) buffer.append(partialSubject.toString() + "/" + partialPredicate.toString());
		if (iri != null) buffer.append(iri);
		if (literal != null) buffer.append(literal);
		buffer.append(xs.charAt(1));

		return new XDIXRef(buffer.toString(), xs, XDIaddress, partialSubject, partialPredicate, iri, literal);
	}

	public boolean isEmpty() {

		return this.toString().length() == 2;
	}

	public boolean hasXDIAddress() {

		return this.XDIaddress != null;
	}

	public boolean hasPartialSubjectAndPredicate() {

		return this.partialSubject != null && this.partialPredicate != null;
	}

	public boolean hasIri() {

		return this.iri != null;
	}

	public boolean hasLiteralNode() {

		return this.literal != null;
	}

	public String getXs() {

		return this.xs;
	}

	public XDIAddress getXDIAddress() {

		return this.XDIaddress;
	}

	public XDIAddress getPartialSubject() {

		return this.partialSubject;
	}

	public XDIAddress getPartialPredicate() {

		return this.partialPredicate;
	}

	public String getIri() {

		return this.iri;
	}

	public String getLiteralNode() {

		return this.literal;
	}

	public String getValue() {

		if (this.XDIaddress != null) return this.XDIaddress.toString();
		if (this.partialSubject != null && this.partialPredicate != null) return this.partialSubject.toString() + "/" + this.partialPredicate.toString();
		if (this.iri != null) return this.iri;
		if (this.literal != null) return this.literal;

		return null;
	}
}
