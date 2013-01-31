package xdi2.core.xri3;

import xdi2.core.xri3.parser.XDI3Parser;
import xdi2.core.xri3.parser.XDI3ParserRegistry;

public class XDI3InnerGraph extends XDI3SyntaxComponent {

	private static final long serialVersionUID = 5744822906511010962L;

	private XDI3Segment subject;
	private XDI3Segment predicate;

	public XDI3InnerGraph(String string, XDI3Segment subject, XDI3Segment predicate) {

		super(string);
		
		this.subject = subject;
		this.predicate = predicate;
	}

	public static XDI3InnerGraph create(XDI3Parser parser, String string) {

		return parser.parseXDI3InnerGraph(string);
	}

	public static XDI3InnerGraph create(String string) {

		return create(XDI3ParserRegistry.getInstance(), string);
	}

	public XDI3Segment getSubject() {

		return this.subject;
	}

	public XDI3Segment getPredicate() {

		return this.predicate;
	}
}
