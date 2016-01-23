package xdi2.core.syntax;

import java.util.List;

public abstract class ParserAbstract implements Parser {

	protected XDIStatement newXDIStatement(String string, XDIAddress subject, Object predicate, Object object) {

		return XDIStatement.fromComponents(string, subject, predicate, object);
	}

	protected XDIAddress newXDIAddress(String string, List<XDIArc> arcs) {

		return XDIAddress.fromComponents(string, arcs);
	}

	protected XDIArc newXDIArc(String string, Character cs, boolean variable, boolean definition, boolean collection, boolean attribute, boolean immutable, boolean relative, String literal, XDIXRef xref) {

		return XDIArc.fromComponents(string, cs, variable, definition, collection, attribute, immutable, relative, literal, xref);
	}

	protected XDIXRef newXDIXRef(String string, String xs, XDIArc XDIarc, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		return XDIXRef.fromComponents(string, xs, XDIarc, partialSubject, partialPredicate, iri, literal);
	}
}
