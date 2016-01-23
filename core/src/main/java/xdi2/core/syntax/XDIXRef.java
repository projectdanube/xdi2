package xdi2.core.syntax;

import xdi2.core.syntax.parser.ParserException;
import xdi2.core.syntax.parser.ParserRegistry;

public class XDIXRef extends XDIIdentifier {

	private static final long serialVersionUID = 4875921569202236777L;

	private String xs;
	private XDIArc XDIarc;
	private XDIAddress partialSubject;
	private XDIAddress partialPredicate;
	private String iri;
	private String literal;

	private XDIXRef(String string, String xs, XDIArc XDIarc, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		super(string);

		this.xs = xs;
		this.XDIarc = XDIarc;
		this.partialSubject = partialSubject;
		this.partialPredicate = partialPredicate;
		this.iri = iri;
		this.literal = literal;
	}

	public static XDIXRef create(String string) {

		return ParserRegistry.getInstance().getParser().parseXDIXRef(string);
	}

	static XDIXRef fromComponents(String string, String xs, XDIArc XDIarc, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		if (xs == null) throw new ParserException("Invalid cross-reference: " + string);
		if (XDIarc == null && partialSubject == null && partialPredicate == null && iri == null && literal == null) throw new ParserException("Invalid cross-reference: " + string);
		if (XDIarc != null && (partialSubject != null || partialPredicate != null || iri != null || literal != null)) throw new ParserException("Invalid cross-reference: " + string);
		if (partialSubject != null && (XDIarc != null || partialPredicate == null || iri != null || literal != null)) throw new ParserException("Invalid cross-reference: " + string);
		if (partialPredicate != null && (XDIarc != null || partialSubject == null || iri != null || literal != null)) throw new ParserException("Invalid cross-reference: " + string);
		if (iri != null && (XDIarc != null || partialSubject != null || partialPredicate != null || literal != null)) throw new ParserException("Invalid cross-reference: " + string);
		if (literal != null && (XDIarc != null || partialSubject != null || partialPredicate != null || iri != null)) throw new ParserException("Invalid cross-reference: " + string);

		if (string == null) {

			StringBuffer buffer = new StringBuffer();
			buffer.append(xs.charAt(0));
			if (XDIarc != null) buffer.append(XDIarc.toString());
			if (partialSubject != null && partialPredicate != null) buffer.append(partialSubject.toString() + "/" + partialPredicate.toString());
			if (iri != null) buffer.append(iri);
			if (literal != null) buffer.append(literal);
			buffer.append(xs.charAt(1));

			string = buffer.toString();
		}

		return new XDIXRef(string, xs, XDIarc, partialSubject, partialPredicate, iri, literal);
	}

	/*
	 * Factory methods
	 */

	public static XDIXRef fromComponents(String xs, XDIArc XDIarc, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		return fromComponents(null, xs, XDIarc, partialSubject, partialPredicate, iri, literal);
	}

	/*
	 * Getters
	 */

	public boolean isEmpty() {

		return this.toString().length() == 2;
	}

	public boolean hasXDIArc() {

		return this.XDIarc != null;
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

	public XDIArc getXDIArc() {

		return this.XDIarc;
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

		if (this.XDIarc != null) return this.XDIarc.toString();
		if (this.partialSubject != null && this.partialPredicate != null) return this.partialSubject.toString() + "/" + this.partialPredicate.toString();
		if (this.iri != null) return this.iri;
		if (this.literal != null) return this.literal;

		return null;
	}
}
