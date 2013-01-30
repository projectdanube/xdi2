package xdi2.core.xri3.impl;

import xdi2.core.xri3.impl.parser.XDI3ParserRegistry;


public class XDI3XRef extends XDI3SyntaxComponent {

	private static final long serialVersionUID = 4875921569202236777L;

	private XDI3Segment segment;
	private XDI3Statement statement;
	private String IRI;

	public XDI3XRef(String string, XDI3Segment segment, XDI3Statement statement, String IRI) {

		super(string);

		this.segment = segment;
		this.statement = statement;
		this.IRI = IRI;
	}

	public static XDI3XRef create(String string) {

		return XDI3ParserRegistry.getInstance().parseXDI3XRef(string);
	}

	public boolean hasSegment() {

		return this.segment != null;
	}

	public boolean hasStatement() {

		return this.statement != null;
	}

	public boolean hasIRI() {

		return this.IRI != null;
	}

	public XDI3Segment getSegment() {

		return this.segment;
	}

	public XDI3Statement getStatement() {

		return this.statement;
	}

	public String getIRI() {

		return this.IRI;
	}

	public String getValue() {

		if (this.segment != null) return this.segment.toString();
		if (this.getStatement() != null) return this.statement.toString();
		if (this.IRI != null) return this.IRI;

		return null;
	}
}
