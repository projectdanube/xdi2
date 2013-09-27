package xdi2.core.xri3;

import xdi2.core.constants.XDIConstants;

public class XDI3SubSegment extends XDI3SyntaxComponent {

	private static final long serialVersionUID = -645927779266394209L;

	private Character cs;
	private boolean classXs;
	private boolean attributeXs;
	private String literal;
	private XDI3XRef xref;

	XDI3SubSegment(String string, Character cs, boolean classXs, boolean attributeXs, String literal, XDI3XRef xref) {

		super(string);

		this.cs = cs;
		this.classXs = classXs;
		this.attributeXs = attributeXs;
		this.literal = literal;
		this.xref = xref;
	}

	public static XDI3SubSegment create(String string) {

		return XDI3ParserRegistry.getInstance().getParser().parseXDI3SubSegment(string);
	}

	public static XDI3SubSegment fromComponents(Character cs, boolean classXs, boolean attributeXs, String literal, XDI3XRef xref) {

		if (literal == null && xref == null) throw new IllegalArgumentException();

		StringBuffer buffer = new StringBuffer();
		if (cs != null) buffer.append(cs);
		if (classXs) buffer.append(XDIConstants.XS_CLASS.charAt(0));
		if (attributeXs) buffer.append(XDIConstants.XS_ATTRIBUTE.charAt(0));
		if (literal != null) buffer.append(literal);
		if (xref != null) buffer.append(xref.toString());
		if (attributeXs) buffer.append(XDIConstants.XS_ATTRIBUTE.charAt(1));
		if (classXs) buffer.append(XDIConstants.XS_CLASS.charAt(1));

		return new XDI3SubSegment(buffer.toString(), cs, classXs, attributeXs, literal, xref);
	}

	public boolean hasCs() {

		return this.cs != null;
	}

	public boolean isClassXs() {

		return this.classXs;
	}

	public boolean isAttributeXs() {

		return this.attributeXs;
	}

	public boolean hasLiteral() {

		return this.literal != null;
	}

	public boolean hasXRef() {

		return this.xref != null;
	}

	public Character getCs() {

		return this.cs;
	}

	public String getLiteral() {

		return this.literal;
	}

	public XDI3XRef getXRef() {

		return this.xref;
	}
}
