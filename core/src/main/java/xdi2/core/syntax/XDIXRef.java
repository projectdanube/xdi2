package xdi2.core.syntax;

import xdi2.core.syntax.parser.ParserRegistry;


public class XDIXRef extends XDIIdentifier {

	private static final long serialVersionUID = 4875921569202236777L;

	private String xs;
	private XDIAddress address;
	private XDIAddress partialSubject;
	private XDIAddress partialPredicate;
	private String iri;
	private String literal;

	XDIXRef(String string, String xs, XDIAddress address, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		super(string);

		this.xs = xs;
		this.address = address;
		this.partialSubject = partialSubject;
		this.partialPredicate = partialPredicate;
		this.iri = iri;
		this.literal = literal;
	}

	public static XDIXRef create(String string) {

		return ParserRegistry.getInstance().getParser().parseXDIXRef(string);
	}

	public static XDIXRef fromComponents(String xs, XDIAddress address, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		if (xs == null) throw new IllegalArgumentException();
		if (address == null && partialSubject == null && partialPredicate == null && iri == null && literal == null) throw new IllegalArgumentException();
		if (address != null && (partialSubject != null || partialPredicate != null || iri != null || literal != null)) throw new IllegalArgumentException();
		if (partialSubject != null && (address != null || partialPredicate == null || iri != null || literal != null)) throw new IllegalArgumentException();
		if (partialPredicate != null && (address != null || partialSubject == null || iri != null || literal != null)) throw new IllegalArgumentException();
		if (iri != null && (address != null || partialSubject != null || partialPredicate != null || literal != null)) throw new IllegalArgumentException();
		if (literal != null && (address != null || partialSubject != null || partialPredicate != null || iri != null)) throw new IllegalArgumentException();

		StringBuffer buffer = new StringBuffer();
		buffer.append(xs.charAt(0));
		if (address != null) buffer.append(address.toString());
		if (partialSubject != null && partialPredicate != null) buffer.append(partialSubject.toString() + "/" + partialPredicate.toString());
		if (iri != null) buffer.append(iri);
		if (literal != null) buffer.append(literal);
		buffer.append(xs.charAt(1));

		return new XDIXRef(buffer.toString(), xs, address, partialSubject, partialPredicate, iri, literal);
	}

	public boolean isEmpty() {

		return this.toString().length() == 2;
	}

	public boolean hasAddress() {

		return this.address != null;
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

	public XDIAddress getAddress() {

		return this.address;
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

	public String getLiteral() {

		return this.literal;
	}

	public String getValue() {

		if (this.address != null) return this.address.toString();
		if (this.partialSubject != null && this.partialPredicate != null) return this.partialSubject.toString() + "/" + this.partialPredicate.toString();
		if (this.iri != null) return this.iri;
		if (this.literal != null) return this.literal;

		return null;
	}
}
