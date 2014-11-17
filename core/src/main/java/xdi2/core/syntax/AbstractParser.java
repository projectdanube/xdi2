package xdi2.core.syntax;

import java.util.List;

public abstract class AbstractParser implements Parser {

	protected XDIStatement newXDIStatement(String string, XDIAddress subject, Object predicate, Object object) {

		return string == null ? XDIStatement.fromComponents(subject, predicate, object) : new XDIStatement(string, subject, predicate, object);
	}

	protected XDIAddress newXDIAddress(String string, List<XDIArc> XDIarcs) {

		return string == null ? XDIAddress.fromComponents(XDIarcs) : new XDIAddress(string, XDIarcs);
	}

	protected XDIArc newXDIArc(String string, Character cs, boolean classXs, boolean attributeXs, String literal, XDIXRef xref) {

		return string == null ? XDIArc.fromComponents(cs, classXs, attributeXs, literal, xref) : new XDIArc(string, cs, classXs, attributeXs, literal, xref);
	}

	protected XDIXRef newXDIXRef(String string, String xs, XDIAddress XDIaddress, XDIAddress partialSubject, XDIAddress partialPredicate, String iri, String literal) {

		return string == null ? XDIXRef.fromComponents(xs, XDIaddress, partialSubject, partialPredicate, iri, literal) : new XDIXRef(string, xs, XDIaddress, partialSubject, partialPredicate, iri, literal);
	}
}
