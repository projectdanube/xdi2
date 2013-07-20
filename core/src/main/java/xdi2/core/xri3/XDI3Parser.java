package xdi2.core.xri3;

import java.util.List;

public abstract class XDI3Parser {

	public abstract XDI3Statement parseXDI3Statement(String string);
	public abstract XDI3Segment parseXDI3Segment(String string);
	public abstract XDI3SubSegment parseXDI3SubSegment(String string);
	public abstract XDI3XRef parseXDI3XRef(String string);

	protected XDI3Statement makeXDI3Statement(String string, XDI3Segment subject, XDI3Segment predicate, XDI3SubSegment object) {

		return new XDI3Statement(string, subject, predicate, object);
	}

	protected XDI3Statement makeXDI3Statement(String string, XDI3Segment subject, XDI3Segment predicate, XDI3Segment object) {

		return new XDI3Statement(string, subject, predicate, object);
	}

	protected XDI3Statement makeXDI3Statement(String string, XDI3Segment subject, XDI3Segment predicate, String object) {

		return new XDI3Statement(string, subject, predicate, object);
	}

	protected XDI3Segment makeXDI3Segment(String string, List<XDI3SubSegment> subSegments) {

		return new XDI3Segment(string, subSegments);
	}

	protected XDI3SubSegment makeXDI3SubSegment(String string, Character cs, boolean singleton, boolean attribute, String literal, XDI3XRef xref) {

		return new XDI3SubSegment(string, cs, singleton, attribute, literal, xref);
	}

	protected XDI3XRef makeXDI3XRef(String string, String cf, XDI3Segment segment, XDI3Statement statement, XDI3Segment partialSubject, XDI3Segment partialPredicate, String iri, String literal) {

		return new XDI3XRef(string, cf, segment, statement, partialSubject, partialPredicate, iri, literal);
	}
}
