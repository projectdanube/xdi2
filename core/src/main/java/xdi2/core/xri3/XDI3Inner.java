package xdi2.core.xri3;

import xdi2.core.xri3.parser.XDI3ParserRegistry;

public class XDI3Inner extends XDI3SyntaxComponent {

	private static final long serialVersionUID = 5744822906511010962L;

	private XDI3Segment subject;
	private XDI3Segment predicate;

	public XDI3Inner(String string, XDI3Segment subject, XDI3Segment predicate) {

		super(string);
		
		this.subject = subject;
		this.predicate = predicate;
	}

	public static XDI3Inner create(String string) {

		return XDI3ParserRegistry.getInstance().parseXDI3Inner(string);
	}

	public XDI3Segment getSubject() {

		return this.subject;
	}

	public XDI3Segment getPredicate() {

		return this.predicate;
	}
}
